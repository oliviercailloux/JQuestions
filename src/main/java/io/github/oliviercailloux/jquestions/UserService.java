package io.github.oliviercailloux.jquestions;

import static com.google.common.base.Verify.verify;

import com.google.common.collect.ImmutableSet;
import io.github.oliviercailloux.jquestions.entities.User;
import io.github.oliviercailloux.wutils.Utf8StringAsBase64Sequence;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequestScoped
public class UserService {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

	@Inject
	EntityManager em;

	@Transactional
	public User get(String unencodedUsername) {
		final Utf8StringAsBase64Sequence base64 = Utf8StringAsBase64Sequence.asBase64Sequence(unencodedUsername);
		LOGGER.info("Searching for unencoded {}, thus encoded {}.", unencodedUsername, base64);
		return get(base64);
	}

	public User get(Utf8StringAsBase64Sequence base64Username) {
		return getQuery(base64Username).getSingleResult();
	}

	public boolean exists(String username) {
		final long matches = getQuery(Utf8StringAsBase64Sequence.asBase64Sequence(username)).getResultStream().count();
		verify(matches <= 1);
		return matches == 1;
	}

	private TypedQuery<User> getQuery(Utf8StringAsBase64Sequence base64Username) {
		final TypedQuery<User> q = em.createNamedQuery("getBase64User", User.class);
		q.setParameter("username", base64Username.toString());
		return q;
	}

	@Transactional
	public void persist(User user) {
		em.persist(user);
	}

	public ImmutableSet<User> getStudents() {
		final TypedQuery<User> q = em.createNamedQuery("getStudents", User.class);
		final List<User> result = q.getResultList();
		return ImmutableSet.copyOf(result);
	}

}
