package sa.elect;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import sa.elect.service.Repository;
import sa.elect.service.projection.ElectionUser;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class TestSerialization {

	@Autowired TestHelper testHelper;
	@Autowired Repository repo;

	@Test
	public void testRowConversion() {
		Map<String, Object> m = new HashMap<>();
		m.put("first_name", "S");
		m.put("last_name", "A");
		m.put("id", 1);
		m.put("role", "USER");
		m.put("password", "USER");
		ElectionUser u = new ObjectMapper().convertValue(m, ElectionUser.class);
		System.out.println(u);
	}

	@Test
	public void testLocalDateTime() {
		assertNotNull(testHelper.getNow());
	}
}
