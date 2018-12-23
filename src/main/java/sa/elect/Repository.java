package sa.elect;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class Repository {

	@Autowired JdbcTemplate jdbcTemplate;
	@Autowired ObjectMapper mapper;
	private ResultSetExtractor<?> voidExtractor = a -> null;


	public <T> T queryValue(Class<T> klass, String query, Object ...args){
		return jdbcTemplate.queryForObject(query, args, klass);
	}

	public <T> T query(Class<T> klass, String query, Object ...args){
		return mapper.convertValue(jdbcTemplate.queryForMap(query, args), klass);
	}

	public <T> Optional<T> queryOptional(Class<T> klass, String query, Object... args) {
		val map = jdbcTemplate.queryForList(query, args);
		if (map.size() > 1)
			throw new RuntimeException("More than one result returned");
		return map.stream().findAny().map(el -> mapper.convertValue(el, klass));
	}

	public <T> Collection<T> queryMultiple(Class<T> klass, String query, Object... args) {
		return jdbcTemplate.queryForList(query, args)
			.stream()
			.map(el -> mapper.convertValue(el, klass))
			.collect(Collectors.toCollection(LinkedList::new));
	}

	public void query(String query, Object ...args) {
		jdbcTemplate.update(query, args);
	}

}
