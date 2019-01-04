package sa.elect.rest;

import io.jsonwebtoken.lang.Assert;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.Mapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import net.rakugakibox.spring.boot.orika.OrikaMapperFactoryConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import sa.elect.json.ElectionResponse;
import sa.elect.json.ElectionStage;
import sa.elect.service.projection.Election;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Configuration
public class CustomMappingConfig implements OrikaMapperFactoryConfigurer {



	@Autowired List<CustomMapper<?, ?>> mappers;


	@Bean
	public CustomMapper<Election, ElectionResponse> map(){
		return new CustomMapper<>() {
			@Override
			public void mapAtoB(Election election, ElectionResponse electionResponse, MappingContext context) {
				List<LocalDateTime> dates = List.of(election.deadline, election.start, election.end);
				int pos = Collections.binarySearch(dates, LocalDateTime.now());
				pos = pos < 0 ? -(pos + 1) : pos;
				electionResponse.stage = ElectionStage.values()[pos];
			}
		};
	}


	@Override
	@SuppressWarnings("unchecked")
	public void configure(MapperFactory orikaMapperFactory) {
//		for(Mapper m: mappers){
//			orikaMapperFactory.classMap(m.getAType(), m.getBType()).byDefault().customize(m);
//		}
		orikaMapperFactory.classMap(Election.class, ElectionResponse.class).byDefault(
		).customize(map());
	}

}