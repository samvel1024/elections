package sa.elect.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.lang.reflect.InvocationTargetException;

@Slf4j
@ControllerAdvice
public class EndpointExceptionHandler extends ResponseEntityExceptionHandler {

	/**
	 * Catch all for any other exceptions...
	 */
	@ExceptionHandler({Exception.class})
	@ResponseBody
	public ResponseEntity<?> handleAnyException(Exception e) {
		return errorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Handle failures commonly thrown from code
	 */
	@ExceptionHandler({InvocationTargetException.class, IllegalArgumentException.class, ClassCastException.class,
		ConversionFailedException.class})
	@ResponseBody
	public ResponseEntity handleMiscFailures(Throwable t) {
		return errorResponse(t, HttpStatus.BAD_REQUEST);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		ResponseEntity<ExceptionDto> responseEntity = errorResponse(ex, status);
		return new ResponseEntity<>(responseEntity.getBody(), status);
	}


	protected ResponseEntity<ExceptionDto> errorResponse(Throwable throwable,
	                                                     HttpStatus status) {
		if (null != throwable) {
			log.error("error caught: " + throwable.getMessage(), throwable);
			return response(ExceptionDto.fromException(throwable), status);
		} else {
			log.error("unknown error caught in RESTController, {}", status);
			return response(null, status);
		}
	}

	protected <T> ResponseEntity<T> response(T body, HttpStatus status) {
		log.debug("Responding with a status of {}", status);
		return new ResponseEntity<>(body, new HttpHeaders(), status);
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	static class ExceptionDto {
		@JsonProperty
		String stacktrace;
		@JsonProperty
		String message;

		public static ExceptionDto fromException(Throwable t) {
			return new ExceptionDto(ExceptionUtils.getStackTrace(t).replace("\n", " ").replace("\t", " "), t.getMessage());
		}
	}
}