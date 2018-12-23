package sa.elect;

import lombok.val;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import sa.elect.projection.Election;

import java.time.LocalDateTime;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ElectionTest {

	@Autowired TestHelper testHelper;
	@Autowired ElectionService elService;

	@Test(expected = Throwable.class)
	public void inconsistentDates(){
		Election test = elService.createElection(Election.builder()
			.creatorId(testHelper.someAdmin().id)
			.description("Test")
			.start(testHelper.getNow())
			.end(testHelper.getNow())
			.deadline(testHelper.getNow())
			.build(), List.of());
		Assert.assertNotNull(test.id);
	}


	@Test
	public void createElection(){
		LocalDateTime now = testHelper.getNow();
		Election test = elService.createElection(Election.builder()
			.creatorId(testHelper.someAdmin().id)
			.description("Test")
			.start(now.plusMinutes(2))
			.end(now.plusMinutes(3))
			.deadline(now.plusMinutes(1))
			.build(), testHelper.users());
		Assert.assertNotNull(test.id);
	}

	@Test(expected = Throwable.class)
	public void assertAdminCannotBeInRegistry() {
		Election election = testHelper.unsavedElection();
		val users = testHelper.users();
		users.add(testHelper.someAdmin());
		elService.createElection(election, users);
	}



}
