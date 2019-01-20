package sa.elect;

import lombok.val;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import sa.elect.service.ElectionService;
import sa.elect.service.projection.Election;

import java.time.LocalDateTime;
import java.util.Collections;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ElectionTest {

	@Autowired TestHelper testHelper;
	@Autowired ElectionService elService;

	@Test(expected = Throwable.class)
	public void inconsistentDates() {
		Election test = elService.createElection(Election.builder()
			.creatorId(testHelper.someAdmin().id)
			.description("Test")
			.start(testHelper.getNow())
			.end(testHelper.getNow())
			.deadline(testHelper.getNow())
			.build(), Collections.emptyList());
		Assert.assertNotNull(test.id);
	}


	@Test
	public void createElection() {
		LocalDateTime now = testHelper.getNow();
		Election test = elService.createElection(Election.builder()
			.creatorId(testHelper.someAdmin().id)
			.description("Test")
			.start(now.plusMinutes(2))
			.end(now.plusMinutes(3))
			.deadline(now.plusMinutes(1))
			.build(), testHelper.users(3));
		Assert.assertNotNull(test.id);
	}

	@Test(expected = Throwable.class)
	public void assertAdminCannotBeInRegistry() {
		Election election = testHelper.unsavedElection();
		val users = testHelper.users(3);
		users.add(testHelper.someAdmin());
		elService.createElection(election, users);
	}


}
