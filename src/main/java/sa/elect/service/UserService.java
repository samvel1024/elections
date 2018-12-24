package sa.elect.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import sa.elect.SystemUser;
import sa.elect.service.projection.Election;
import sa.elect.service.projection.ElectionUser;

import java.util.Optional;

@Service
@Slf4j
public class UserService implements UserDetailsService {

	@Autowired Repository repo;
	@Autowired PasswordEncoder encoder;

	public ElectionUser createUser(ElectionUser user) {
		String pass = encoder.encode(user.password);
		ElectionUser u =  repo.query(ElectionUser.class, "insert into election_user (first_name, last_name, student_id, password, role)\n" +
			"values (?, ?, ?, ?, ?) returning *", user.first, user.last, user.studentId, pass, user.role.toString());
		log.debug("Created user {}", u);
		return u;
	}

	public ElectionUser authenticate(String studentId, String pswrd) {
		Optional<ElectionUser> u = repo.queryOptional(ElectionUser.class, "select * from election_user where student_id = ?", studentId);
		Assert.isTrue(u.map(a -> BCrypt.checkpw(pswrd, a.getPassword()))
			.orElse(false), "Incorrect username or password");
		return u.get();
	}

	@Override
	public SystemUser loadUserByUsername(String username) throws UsernameNotFoundException {
		ElectionUser u = repo.query(ElectionUser.class, "select * from election_user where student_id = ?", username);
		return new SystemUser(u);
	}


}
