package sa.elect.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import sa.elect.SystemUser;
import sa.elect.service.projection.ElectionUser;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

	@Autowired Repository repo;

	public ElectionUser createUser(ElectionUser user) {
		String pass = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
		return repo.query(ElectionUser.class, "insert into election_user (first_name, last_name, student_id, password, role)\n" +
			"values (?, ?, ?, ?, ?) returning *", user.first, user.last, user.studentId, pass, user.role.toString());
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
