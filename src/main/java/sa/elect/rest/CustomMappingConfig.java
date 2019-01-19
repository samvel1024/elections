package sa.elect.rest;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.Mapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import net.rakugakibox.spring.boot.orika.OrikaMapperFactoryConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sa.elect.json.ElectionJson;
import sa.elect.json.ElectionStage;
import sa.elect.json.UserJson;
import sa.elect.service.ElectionService;
import sa.elect.service.projection.Election;
import sa.elect.service.projection.ElectionUser;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class CustomMappingConfig implements OrikaMapperFactoryConfigurer {

	@Autowired List<CustomMapper<?, ?>> mappers;


	@Bean
	public CustomMapper<Election, ElectionJson> map1(@Autowired ElectionService electionService) {
		return new CustomMapper<>() {
			@Override
			public void mapAtoB(Election election, ElectionJson electionJson, MappingContext context) {
				List<LocalDateTime> dates = List.of(election.deadline, election.start, election.end);
				int pos = Collections.binarySearch(dates, LocalDateTime.now());
				pos = pos < 0 ? -(pos + 1) : pos;
				electionJson.stage = ElectionStage.values()[pos];
				electionJson.voterRegistry = electionService.getVoterRegistry(election)
					.stream()
					.map(u -> mapperFacade.map(u, UserJson.class))
					.collect(Collectors.toList());
			}
		};
	}

	@Bean
	public CustomMapper<ElectionUser, UserJson> map2() {
		return new CustomMapper<>() {
			@Override
			public void mapAtoB(ElectionUser user, UserJson userJson, MappingContext context) {
				userJson.name = user.first + " " + user.last;
			}
		};
	}

	@Override
	@SuppressWarnings("unchecked")
	public void configure(MapperFactory orikaMapperFactory) {
		for (Mapper m : mappers) {
				orikaMapperFactory.classMap(m.getAType(), m.getBType()).byDefault().customize(m).register();
		}
	}

}