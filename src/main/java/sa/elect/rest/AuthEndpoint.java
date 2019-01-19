package sa.elect.rest;

import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sa.elect.json.CreateUserRequest;
import sa.elect.json.JwtAuthenticationRequest;
import sa.elect.json.UserJson;
import sa.elect.security.JwtTokenProvider;
import sa.elect.security.SystemUser;
import sa.elect.service.UserService;
import sa.elect.service.projection.ElectionUser;
import sa.elect.service.projection.Role;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("auth")
public class AuthEndpoint {

	@Autowired JwtTokenProvider jwtTokenProvider;
	@Autowired AuthenticationManager authenticationManager;
	@Autowired UserService userService;
	@Autowired MapperFacade mapperFacade;


	@RequestMapping(value = "/signin", method = RequestMethod.POST)
	public ResponseEntity<UserJson> signin(@RequestBody JwtAuthenticationRequest req, HttpServletResponse response) {
		Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(req.studentId, req.password));
		String token = jwtTokenProvider.createToken(req.studentId);
		response.addCookie(new Cookie("jwt-token", token));
		ElectionUser principal = (SystemUser) authenticate.getPrincipal();
		return new ResponseEntity<>(mapperFacade.map(principal, UserJson.class), HttpStatus.OK);
	}

	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public void signup(@RequestBody CreateUserRequest user) {
		userService.createUser(ElectionUser.builder()
			.password(user.password)
			.studentId(user.studentId)
			.first(user.first)
			.last(user.last)
			.role(Role.USER)
			.build());
	}

}
