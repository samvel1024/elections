package sa.elect;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import sa.elect.projection.Role;
import sa.elect.projection.User;


@RunWith(SpringRunner.class)
@SpringBootTest
public class UserTest {

	@Autowired UserService uService;
	@Autowired TestHelper testHelper;



	@Test
	public void createAndAuth() {
		String studentId = testHelper.randomStudentId();
		User created = uService.createUser(User.builder()
			.first("Test")
			.last("Test")
			.password("pswrf")
			.studentId(studentId)
			.role(Role.USER)
			.build());
		Assert.assertNotNull(created.getId());
		User auth = uService.authenticate(studentId, "pswrf");
		Assert.assertEquals(created, auth);
	}


	@Test(expected = Throwable.class)
	public void duplicateStudentId(){
		String studentId = testHelper.randomStudentId();
		uService.createUser(User.builder()
			.first("Test")
			.last("Test")
			.password("1234")
			.studentId(studentId)
			.role(Role.USER)
			.build());
		uService.createUser(User.builder()
			.first("Tests")
			.last("Testa")
			.password("12341")
			.studentId(studentId)
			.role(Role.USER)
			.build());
	}

	@Test(expected = Throwable.class)
	public void badStudentId(){
		uService.createUser(User.builder()
			.first("Test")
			.last("Test")
			.password("1234")
			.role(Role.USER)
			.studentId(testHelper.randomStudentId() + "1")
			.build());
	}







}

