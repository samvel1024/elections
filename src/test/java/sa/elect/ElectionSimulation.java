package sa.elect;

import lombok.RequiredArgsConstructor;
import sa.elect.json.CreateElectionRequest;
import sa.elect.json.ElectionJson;
import sa.elect.rest.ElectionEndpoint;
import sa.elect.service.projection.ElectionUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ElectionSimulation {

	static final int DELAY = 20;
	final ElectionUser admin;
	final List<ElectionUser> candidates;
	final List<ElectionUser> registry;
	final ElectionEndpoint api;
	Integer electionId;

	ElectionJson getElection() {
		return api.getElections(admin).stream()
			.filter(el -> el.id.equals(electionId))
			.findFirst()
			.orElseThrow(RuntimeException::new);
	}


	ElectionJson createElection() {
		LocalDateTime now = LocalDateTime.now();
		ElectionJson el = api.createElection(CreateElectionRequest.builder()
			.deadline(now.plusSeconds(DELAY))
			.start(now.plusSeconds(DELAY * 2))
			.end(now.plusSeconds(DELAY * 3))
			.desc("Test election")
			.registryIds(registry.stream().map(ElectionUser::getStudentId).collect(Collectors.toList()))
			.build(), admin);
		electionId = el.id;
		return el;
	}

	ElectionJson registerCandidates() {
		for (ElectionUser u : candidates) {
			api.addMyCandidacy(electionId, u);
		}
		return getElection();
	}

	void castVote(ElectionUser user, ElectionUser candidate) {
		api.castMyVote(candidate.id, electionId, user);
	}

}
