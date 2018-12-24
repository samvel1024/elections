package sa.elect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sa.elect.service.Repository;
import sa.elect.service.projection.Election;
import sa.elect.service.projection.ElectionUser;

import java.time.LocalDateTime;
import java.util.Collection;

@Component
public class TestHelper {


	@Autowired Repository repo;

	private TestHelper() {

	}

	public String randomStudentId() {
		return "aa" + String.valueOf(Math.random()).substring(2, 8);
	}

	public ElectionUser someAdmin() {
		return repo.query(ElectionUser.class, "select * from election_user where role = 'ADMIN' limit 1");
	}

	public LocalDateTime getNow(){
		return repo.queryValue(LocalDateTime.class, "select now()");
	}

	public Collection<ElectionUser> users(){
		return repo.queryMultiple(ElectionUser.class, "select * from election_user where role='USER' limit 3");
	}

	public Election unsavedElection(){
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
