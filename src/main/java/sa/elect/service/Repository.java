package sa.elect.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.TextNode;
import lombok.val;
import org.intellij.lang.annotations.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class Repository {

	@Autowired JdbcTemplate jdbcTemplate;
	@Autowired Jackson2ObjectMapperBuilder builder;
	ObjectMapper mapper;

	@PostConstruct
	public void initMapper() {
		ObjectMapper objectMapper = builder.build();
		SimpleModule module = new SimpleModule();
		module.addDeserializer(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
			@Override
			public LocalDateTime deserialize(JsonParser jp, DeserializationContext deserializationContext) throws IOException {
				TextNode node = jp.getCodec().readTree(jp);
				return LocalDateTime.parse(node.textValue(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
			}
		});
		objectMapper.registerModule(module);
		mapper = objectMapper;
	}

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
