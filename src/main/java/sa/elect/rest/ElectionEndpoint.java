package sa.elect.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sa.elect.json.CreateElectionRequest;
import sa.elect.json.ElectionResponse;
import ma.glasnost.orika.MapperFacade;
import sa.elect.security.SystemUser;
import sa.elect.service.ElectionService;
import sa.elect.service.UserService;
import sa.elect.service.projection.Election;

@RequestMapping("election")
@RestController
public class ElectionEndpoint {

	@Autowired ElectionService electionService;
	@Autowired UserService userService;
	@Autowired MapperFacade mapper;

	@RequestMapping(method = RequestMethod.POST)
	@PreAuthorize("hasAuthority('ADMIN')")
	public ElectionResponse createElection(@RequestBody CreateElectionRequest req, @AuthenticationPrincipal SystemUser user) {
		Election el = electionService.createElection(Election.builder()
			.creatorId(user.id)
			.deadline(req.deadline)
			.description(req.desc)
			.end(req.end)
			.start(req.start)
			.build(), userService.loadByStudentIds(req.getRegistryIds()));
		return mapper.map(el, ElectionResponse.class);
	}
}
