package sa.elect;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import sa.elect.projection.User;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestSerialization {

	@Autowired TestHelper testHelper;

	@Test
	public void testRowConversion(){
		Map<String, Object> m = new HashMap<>();
		m.put("first_name", "S");
		m.put("last_name", "A");
		m.put("id", 1);
		m.put("role", "USER");
		m.put("password", "USER");
		User u = new ObjectMapper().convertValue(m, User.class);
		System.out.println(u);
	}

	@Test
	public void testLocalDateTime(){
		assertNotNull(testHelper.getNow());
	}
}
