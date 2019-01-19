package sa.elect.rest;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import net.rakugakibox.spring.boot.orika.OrikaMapperFactoryConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sa.elect.json.ElectionJson;
import sa.elect.json.ElectionResultJson;
import sa.elect.json.ElectionStage;
import sa.elect.json.UserJson;
import sa.elect.service.ElectionService;
import sa.elect.service.projection.Election;
import sa.elect.service.projection.ElectionResult;
import sa.elect.service.projection.ElectionUser;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Configuration
public class CustomMappingConfig implements OrikaMapperFactoryConfigurer {

	@Autowired List<CustomMapper<?, ?>> mappers;


	@Bean
	public CustomMapper<Election, ElectionJson> map1(@Autowired ElectionService electionService) {
		return new CustomMapper<>() {

			private ElectionStage electionStage(Election election) {
				List<LocalDateTime> dates = List.of(election.deadline, election.start, election.end);
				int pos = Collections.binarySearch(dates, LocalDateTime.now());
				pos = pos < 0 ? -(pos + 1) : pos;
				return ElectionStage.values()[pos];
			}

			@Override
			public void mapAtoB(Election election, ElectionJson electionJson, MappingContext context) {
				electionJson.stage = electionStage(election);
				electionJson.voterRegistry = mapperFacade.mapAsList(electionService.getVoterRegistry(election), UserJson.class);
				electionJson.candidates = mapperFacade.mapAsList(electionService.getCandidates(election), UserJson.class);

				if (electionJson.stage == ElectionStage.CLOSED) {
					electionJson.results = mapperFacade.mapAsList(electionService.getResults(election), ElectionResultJson.class);
				} else {
					electionJson.results = List.of();
				}
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

	@Bean
	public CustomMapper<ElectionResult, ElectionResultJson> map3() {
		return new CustomMapper<>() {
			@Override
			public void mapAtoB(ElectionResult user, ElectionResultJson userJson, MappingContext context) {
				userJson.name = user.first + " " + user.last;
			}
		};
	}

	@Override
	@SuppressWarnings("unchecked")
	public void configure(MapperFactory orikaMapperFactory) {
		for (CustomMapper m : mappers) {
			orikaMapperFactory.classMap(m.getAType(), m.getBType()).byDefault().customize(m).register();
		}
	}

}