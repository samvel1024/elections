package sa.elect.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.intellij.lang.annotations.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class Repository {

	@Autowired JdbcTemplate jdbcTemplate;
	@Autowired ObjectMapper mapper;


	public <T> T queryValue(Class<T> klass, @Language("SQL") String query, Object... args) {
		return jdbcTemplate.queryForObject(query, args, klass);
	}

	public <T> T query(Class<T> klass, @Language("SQL") String query, Object... args) {
		return mapper.convertValue(jdbcTemplate.queryForMap(query, args), klass);
	}

	public <T> Optional<T> queryOptional(Class<T> klass, @Language("SQL") String query, Object... args) {
		val map = jdbcTemplate.queryForList(query, args);
		if (map.size() > 1)
			throw new RuntimeException("More than one result returned");
		return map.stream().findAny().map(el -> mapper.convertValue(el, klass));
	}

	public <T> Collection<T> queryMultiple(Class<T> klass, @Language("SQL") String query, Object... args) {
		return jdbcTemplate.queryForList(query, args)
			.stream()
			.map(el -> mapper.convertValue(el, klass))
			.collect(Collectors.toCollection(LinkedList::new));
	}

	public void query(@Language("SQL") String query, Object... args) {
		jdbcTemplate.update(query, args);
	}

}
