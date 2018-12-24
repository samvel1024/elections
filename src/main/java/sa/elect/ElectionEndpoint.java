package sa.elect;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sa.elect.json.CreateElectionRequest;

@RequestMapping("election")
@RestController
public class ElectionEndpoint {

	@RequestMapping(method = RequestMethod.POST)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String createElection(@RequestBody CreateElectionRequest req, @AuthenticationPrincipal SystemUser user) {
		return "A";
	}
}
