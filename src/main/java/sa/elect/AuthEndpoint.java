package sa.elect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sa.elect.json.JwtAuthenticationRequest;
import sa.elect.security.JwtTokenUtil;
import sa.elect.json.JwtAuthenticationResponse;
import sa.elect.service.UserService;

@RestController
public class AuthEndpoint {

	@Autowired UserService userService;
	@Autowired AuthenticationManager authenticationManager;

	@Autowired
	JwtTokenUtil jwtTokenUtil;

	@Autowired
	UserService userDetailsService;

	@RequestMapping(value = "auth", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest) throws AuthenticationException {
		final Authentication authentication = authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(
				authenticationRequest.getEmail(),
				authenticationRequest.getPassword()
			)
		);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		final SystemUser userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
		final String token = jwtTokenUtil.generateToken(userDetails);

		return ResponseEntity.ok(new JwtAuthenticationResponse(token, userDetails.id));
	}

}
