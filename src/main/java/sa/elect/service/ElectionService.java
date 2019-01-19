package sa.elect.service;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import sa.elect.service.projection.Election;
import sa.elect.service.projection.ElectionResult;
import sa.elect.service.projection.ElectionUser;

import java.util.ArrayList;
import java.util.Collection;

@Service
@Slf4j
public class ElectionService {

	@Autowired Repository repo;


	public Election getById(Integer id) {
		return repo.query(Election.class, "select * from election where id = ?", id);
	}


	@Transactional(isolation = Isolation.READ_COMMITTED)
	public Election createElection(Election el, Collection<ElectionUser> registry) {
		Assert.isTrue(registry.size() > 0, "Registry cannot be empty");
		el = repo.query(Election.class, "\n" +
			"insert into election(date_start, date_end, candidate_deadline, created_by, description) VALUES\n" +
			"(?, ?, ?, ?, ?) returning *", el.start, el.end, el.deadline, el.creatorId, el.description);
		log.debug("Created election {}", el);
		StringBuilder values = new StringBuilder();
		val args = new ArrayList<Object>();
		for (ElectionUser u : registry) {
			args.add(u.id);
			args.add(el.id);
			values.append("(?, ?),");
			log.debug("Added voter to election {} {}", u, el);
		}
		values.setCharAt(values.length() - 1, ' ');
		repo.query("insert into election_registry(voter_id, election_id)  values " + values.toString(), args.toArray());
		return el;
	}

	public Collection<ElectionUser> getVoterRegistry(Election el) {
		return repo.queryMultiple(ElectionUser.class, "select voter.* from election_registry " +
			"inner join election_user voter on election_registry.voter_id = voter.id where election_id = ?", el.id);
	}

	public Collection<ElectionUser> getCandidates(Election el) {
		return repo.queryMultiple(ElectionUser.class, "select voter.* from election_registry " +
			"inner join election_user voter on election_registry.voter_id = voter.id where election_id = ? and election_registry.is_candidate = true", el.id);
	}

	public void addCandidacy(Election el, ElectionUser u) {
		repo.query("update election_registry set is_candidate = true \n" +
			"where election_id = ? and voter_id = ?", el.id, u.id);
	}

	private Integer getRegistryId(Election election, ElectionUser user) {
		return repo.queryValue(Integer.class,
			"select id from election_registry where election_id = ? and voter_id = ? ", election.id, user.id);
	}

	public void vote(ElectionUser from, ElectionUser to, Election el) {
		Integer voter = getRegistryId(el, from);
		Integer candidate = getRegistryId(el, to);
		repo.query("insert into vote (voter_reg_id, candidate_reg_id) values (?, ?)", voter, candidate);
	}

	private Collection<Election> getElectionsForAdmin(ElectionUser user) {
		return repo.queryMultiple(Election.class, "select * from election order by id desc");
	}


	private Collection<Election> getElectionsForVoter(ElectionUser user) {
		return repo.queryMultiple(Election.class, "select el.* from election_registry " +
			"inner join election el on election_registry.election_id = el.id " +
			"where election_registry.voter_id = ? " +
			"order by el.id desc ", user.id);
	}


	public Collection<Election> getElectionsForUser(ElectionUser user) {
		switch (user.getRole()) {
			case ADMIN:
				return getElectionsForAdmin(user);
			case USER:
				return getElectionsForVoter(user);
			default:
				throw new RuntimeException();
		}
	}

	public Collection<ElectionResult> getResults(Election election) {
		return repo.queryMultiple(ElectionResult.class,
			"select max(voter.id)  as id," +
				"max(voter.first_name) as first_name," +
				"max(voter.last_name)  as last_name," +
				"max(voter.student_id) as student_id," +
				"max(voter.role)       as role," +
				"max(voter.password)   as password," +
				"max(voter.student_id) as student_id," +
				"count(*)              as vote_count " +
				"from vote" +
				"       inner join election_registry er on vote.candidate_reg_id = er.id " +
				"       inner join election_user voter on er.voter_id = voter.id " +
				"where er.election_id = ?" +
				"group by vote.candidate_reg_id ", election.id);
	}
}
