package sa.elect.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PUBLIC)
@Builder
public class ElectionResponse {
		Integer id;
		LocalDateTime start;
		LocalDateTime end;
		LocalDateTime deadline;
		Integer creatorId;
		String description;
}
