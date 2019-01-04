package sa.elect.rest;

import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sa.elect.json.CreateElectionRequest;
import sa.elect.json.ElectionResponse;
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

	@RequestMapping(method = RequestMethod.POST, path = "/{electionId}/myCandidacy")
	@PreAuthorize("hasAuthority('USER')")
	public void addMyCandidacy(@PathVariable Integer electionId, @AuthenticationPrincipal SystemUser user) {
		electionService.addCandidacy(electionService.getById(electionId), user);
	}

	@RequestMapping(method = RequestMethod.POST, path = "/{electionId}/vote")
	@PreAuthorize("hasAuthority('USER')")
	public void castMyCote(@RequestParam("target_user_id") Integer targetUserId,
	                       @PathVariable Integer electionId,
	                       @AuthenticationPrincipal SystemUser user) {
		electionService.vote(user, userService.getById(targetUserId), electionService.getById(electionId));
	}



}
