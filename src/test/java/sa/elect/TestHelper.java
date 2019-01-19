package sa.elect.testutil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sa.elect.service.Repository;
import sa.elect.service.UserService;
import sa.elect.service.projection.Election;
import sa.elect.service.projection.ElectionUser;
import sa.elect.service.projection.Role;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.IntStream;

@Component
public class TestHelper {

	public static final String PSWRD = "pswrd";

	@Autowired Repository repo;
	@Autowired UserService userService;

	private TestHelper() {

	}

	public String randomStudentId() {
		return "aa" + String.valueOf(Math.random()).substring(2, 8);
	}

	public ElectionUser someAdmin() {
		return repo.queryOptional(ElectionUser.class, "select * from election_user where role = 'ADMIN' limit 1").orElseGet(() ->
			userService.createUser(ElectionUser.builder()
				.role(Role.ADMIN)
				.first("Admin")
				.last("Admin")
				.password(PSWRD)
				.studentId(randomStudentId())
				.build())
		);
	}

	public LocalDateTime getNow() {
		return repo.queryValue(LocalDateTime.class, "select now()");
	}

	public Collection<ElectionUser> users(int limit) {
		Collection<ElectionUser> stuednts = repo.queryMultiple(ElectionUser.class, "select * from election_user where role='USER' limit ?", limit);
		if (stuednts.size() < limit) {
			IntStream.range(stuednts.size(), limit).forEach(i -> {
				stuednts.add(userService.createUser(ElectionUser.builder()
					.role(Role.USER)
					.first("Crazy")
					.last("Student")
					.password(PSWRD)
					.studentId(randomStudentId())
					.build()));
			});
		}
		return stuednts;
	}

	public Election unsavedElection() {
		LocalDateTime now = getNow();
		return Election.builder()
			.creatorId(someAdmin().id)
			.description("Test")
			.start(now.plusMinutes(2))
			.end(now.plusMinutes(3))
			.deadline(now.plusMinutes(1))
			.build();
	}

}
