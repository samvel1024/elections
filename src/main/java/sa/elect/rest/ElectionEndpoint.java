package sa.elect.rest;

import io.swagger.annotations.ApiOperation;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sa.elect.json.CreateElectionRequest;
import sa.elect.json.ElectionJson;
import sa.elect.service.ElectionService;
import sa.elect.service.UserService;
import sa.elect.service.projection.Election;
import sa.elect.service.projection.ElectionUser;

import java.util.List;

@RequestMapping("election")
@RestController
public class ElectionEndpoint {

	@Autowired ElectionService electionService;
	@Autowired UserService userService;
	@Autowired MapperFacade mapper;

	@RequestMapping(method = RequestMethod.POST)
	@PreAuthorize("hasAuthority('ADMIN')")
	public ElectionJson createElection(@RequestBody CreateElectionRequest req, @AuthenticationPrincipal ElectionUser user) {
		Election el = electionService.createElection(Election.builder()
			.creatorId(user.id)
			.deadline(req.deadline)
			.description(req.desc)
			.end(req.end)
			.start(req.start)
			.build(), userService.loadByStudentIds(req.getRegistryIds()));
		return mapper.map(el, ElectionJson.class);
	}

	@RequestMapping(method = RequestMethod.POST, path = "/{electionId}/myCandidacy")
	@PreAuthorize("hasAuthority('USER')")
	public void addMyCandidacy(@PathVariable Integer electionId, @AuthenticationPrincipal ElectionUser user) {
		electionService.addCandidacy(electionService.getById(electionId), user);
	}

	@RequestMapping(method = RequestMethod.POST, path = "/{electionId}/vote")
	@PreAuthorize("hasAuthority('USER')")
	public void castMyVote(@RequestParam("candidateId") Integer targetUserId,
	                       @PathVariable Integer electionId,
	                       @AuthenticationPrincipal ElectionUser user) {
		electionService.vote(user, userService.getById(targetUserId), electionService.getById(electionId));
	}

	@ApiOperation("Returns accessible elections for the user. For admins returns all elections.")
	@RequestMapping(method = RequestMethod.GET)
	public List<ElectionJson> getElections(@AuthenticationPrincipal ElectionUser user) {
		return mapper.mapAsList(electionService.getElectionsForUser(user), ElectionJson.class);
	}


}
