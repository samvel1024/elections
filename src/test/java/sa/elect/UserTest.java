package sa.elect;

import lombok.val;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import sa.elect.service.UserService;
import sa.elect.service.projection.Role;
import sa.elect.service.projection.ElectionUser;
import sa.elect.testutil.TestHelper;

import java.util.stream.Collectors;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class UserTest {

	@Autowired UserService uService;
	@Autowired TestHelper testHelper;



	@Test
	public void createAndAuth() {
		String studentId = testHelper.randomStudentId();
		ElectionUser created = uService.createUser(ElectionUser.builder()
			.first("Test")
			.last("Test")
			.password("pswrf")
			.studentId(studentId)
			.role(Role.USER)
			.build());
		Assert.assertNotNull(created.getId());
		ElectionUser auth = uService.authenticate(studentId, "pswrf");
		Assert.assertEquals(created, auth);
	}


	@Test(expected = Throwable.class)
	public void duplicateStudentId(){
		String studentId = testHelper.randomStudentId();
		uService.createUser(ElectionUser.builder()
			.first("Test")
			.last("Test")
			.password("1234")
			.studentId(studentId)
			.role(Role.USER)
			.build());
		uService.createUser(ElectionUser.builder()
			.first("Tests")
			.last("Testa")
			.password("12341")
			.studentId(studentId)
			.role(Role.USER)
			.build());
	}

	@Test(expected = Throwable.class)
	public void badStudentId(){
		uService.createUser(ElectionUser.builder()
			.first("Test")
			.last("Test")
			.password("1234")
			.role(Role.USER)
			.studentId(testHelper.randomStudentId() + "1")
			.build());
	}



	@Test
	public void fetchByStudentId(){
		val users = testHelper.users(3);
		val fetched = uService.loadByStudentIds(users.stream().map(ElectionUser::getStudentId).collect(Collectors.toList()));
		Assert.assertEquals(users, fetched);
	}




}

