package sa.elect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sa.elect.json.CreateUserRequest;
import sa.elect.service.UserService;
import sa.elect.service.projection.ElectionUser;
import sa.elect.service.projection.Role;

@RestController
@RequestMapping("user/")
public class UserEndpoint {

	@Autowired UserService userService;

	@RequestMapping(method = RequestMethod.POST)
	public void createUser(@RequestBody CreateUserRequest req) {
		userService.createUser(ElectionUser.builder()
			.first(req.first)
			.last(req.last)
			.password(req.password)
			.studentId(req.studentId)
			.role(Role.USER)
			.build());
	}

}
