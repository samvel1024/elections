package sa.elect;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import sa.elect.projection.Election;
import sa.elect.projection.User;

import java.util.Collection;

@Service
@Slf4j
public class ElectionService {

	@Autowired Repository repo;


	@Transactional(isolation = Isolation.READ_COMMITTED)
	public Election createElection(Election el, Collection<User> registry) {
		el = repo.query(Election.class, "\n" +
			"insert into election(date_start, date_end, candidate_deadline, created_by, description) VALUES\n" +
			"(?, ?, ?, ?, ?) returning *", el.start, el.end, el.deadline, el.creatorId, el.description);
		log.debug("Created election {}", el);
		for (User u : registry) {
			repo.query("insert into election_registry(voter_id, election_id)  values (?, ?)", u.id, el.id);
			log.debug("Added voter to election {} {}", u, el);
		}
		return el;
	}


}
