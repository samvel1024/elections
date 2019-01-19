package sa.elect.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import sa.elect.service.projection.Election;
import sa.elect.service.projection.ElectionUser;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class ElectionService {

	@Autowired Repository repo;


	public Election getById(Integer id){
		return repo.query(Election.class, "select * from election where id = ?", id);
	}


	@Transactional(isolation = Isolation.READ_COMMITTED)
	public Election createElection(Election el, Collection<ElectionUser> registry) {
		el = repo.query(Election.class, "\n" +
			"insert into election(date_start, date_end, candidate_deadline, created_by, description) VALUES\n" +
			"(?, ?, ?, ?, ?) returning *", el.start, el.end, el.deadline, el.creatorId, el.description);
		log.debug("Created election {}", el);
		for (ElectionUser u : registry) {
			repo.query("insert into election_registry(voter_id, election_id)  values (?, ?)", u.id, el.id);
			log.debug("Added voter to election {} {}", u, el);
		}
		return el;
	}

	public Collection<ElectionUser> getVoterRegistry(Election el){
		return repo.queryMultiple(ElectionUser.class, "select voter.* from election_registry " +
			"inner join election_user voter on election_registry.voter_id = voter.id where election_id = ?", el.id);
	}

	public void addCandidacy(Election el, ElectionUser u) {
		repo.query("select add_candidacy(?, ?)", u.getId(), el.getId());
	}

	public void vote(ElectionUser from, ElectionUser to, Election el) {
		repo.query("select add_vote(?, ?)", from.getId(), to.getId());
	}


}
