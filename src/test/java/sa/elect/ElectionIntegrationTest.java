package sa.elect;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import sa.elect.json.ElectionJson;
import sa.elect.json.ElectionResultJson;
import sa.elect.json.ElectionStage;
import sa.elect.json.UserJson;
import sa.elect.rest.ElectionEndpoint;
import sa.elect.service.projection.ElectionUser;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;


@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
@Transactional
public class ElectionIntegrationTest {

	@Autowired ElectionEndpoint electionEndpoint;
	@Autowired TestHelper testHelper;


	private void sleepUntill(LocalDateTime t) {
		long sleep = Duration.between(LocalDateTime.now(), t).toMillis();
		try {
			Thread.sleep(Math.max(0, sleep));
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException(e);
		}
	}

	private <L, R, T> void assertIdentialCollections(Collection<L> col1, Function<L, T> mapper1, Collection<R> col2, Function<R, T> mapper2) {
		List<T> left = col1.stream().map(mapper1).sorted().collect(Collectors.toList());
		List<T> right = col2.stream().map(mapper2).sorted().collect(Collectors.toList());
		assertEquals(left, right);
	}

	private void setFakeAuthContext() {
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(testHelper.someAdmin(), "",
			Arrays.asList(new SimpleGrantedAuthority("USER"), new SimpleGrantedAuthority("ADMIN")));
		SecurityContextHolder.getContext().setAuthentication(token);
	}

	@Test
	public void fullElectionTest() {
		setFakeAuthContext();
		List<ElectionUser> registry = new ArrayList<>(testHelper.users(15));
		List<ElectionUser> candidates = registry.subList(0, 4);
		ElectionSimulation el = new ElectionSimulation(testHelper.someAdmin(), candidates, registry, electionEndpoint);

		// Create the election
		ElectionJson initialJson = el.createElection();
		assertEquals(ElectionStage.WAITING_FOR_CANDIDATES, el.getElection().stage);
		assertIdentialCollections(registry, ElectionUser::getStudentId, initialJson.getVoterRegistry(), UserJson::getStudentId);

		// Add the candidates
		ElectionJson afterCandidacy = el.registerCandidates();
		assertIdentialCollections(candidates, ElectionUser::getStudentId, afterCandidacy.getCandidates(), UserJson::getStudentId);
		assertEquals(ElectionStage.WAITING_FOR_CANDIDATES, el.getElection().stage);

		// State change as time passes
		log.debug("Waiting for deadline of candidates to end");
		sleepUntill(initialJson.deadline);
		assertEquals(ElectionStage.WAITING_TO_START, el.getElection().stage);
		log.debug("Waiting for election to start");
		sleepUntill(initialJson.start);
		assertEquals(ElectionStage.IN_PROGRESS, el.getElection().stage);

		//Send votes
		FreqMap<String> expectedVotes = new FreqMap<>();
		for (int i = 0; i < registry.size(); ++i) {
			ElectionUser cand = candidates.get(i % candidates.size());
			expectedVotes.increment(cand.studentId);
			el.castVote(registry.get(i % registry.size()), cand);
		}
		assertEquals(0, el.getElection().results.size());

		//Assert results
		sleepUntill(initialJson.getEnd());
		assertEquals(ElectionStage.CLOSED, el.getElection().stage);
		List<ElectionResultJson> results = el.getElection().results;
		val resultMap = new HashMap<String, Integer>();
		for (val res : results) {
			resultMap.put(res.studentId, res.voteCount);
		}
		Assert.assertEquals(expectedVotes, resultMap);
	}


}
