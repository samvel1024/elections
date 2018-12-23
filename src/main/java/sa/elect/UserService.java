package sa.elect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import sa.elect.projection.User;

import java.util.Optional;

@Service
public class UserService {

	@Autowired Repository repo;

	public User createUser(User user) {
		String pass = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
		return repo.query(User.class, "insert into election_user (first_name, last_name, student_id, password, role)\n" +
			"values (?, ?, ?, ?, ?) returning *", user.first, user.last, user.studentId, pass, user.role.toString());
	}

	public User authenticate(String studentId, String pswrd) {
		Optional<User> u = repo.queryOptional(User.class, "select * from election_user where student_id = ?", studentId);
		Assert.isTrue(u.map(a -> BCrypt.checkpw(pswrd, a.getPassword()))
			.orElse(false), "Incorrect username or password");
		return u.get();
	}

}
