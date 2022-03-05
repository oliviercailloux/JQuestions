package io.github.oliviercailloux.jquestions;

import static com.google.common.base.Verify.verify;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MoreCollectors;
import io.github.oliviercailloux.jquestions.entities.User;
import io.github.oliviercailloux.wutils.Utf8StringAsBase64Sequence;
import java.util.List;
import java.util.Optional;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.ws.rs.core.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequestScoped
public class UserService {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

	@Inject
	EntityManager em;

	public User getCurrent(SecurityContext securityContext) {
		final Utf8StringAsBase64Sequence base64Name = Utf8StringAsBase64Sequence
				.fromUtf8StringAsBase64Sequence(securityContext.getUserPrincipal().getName());
		final User current = get(base64Name).orElseThrow();
		return current;
	}

	@Transactional
	public Optional<User> get(String unencodedUsername) {
		final Utf8StringAsBase64Sequence base64 = Utf8StringAsBase64Sequence.asBase64Sequence(unencodedUsername);
		final Optional<User> optional = get(base64);
		LOGGER.info("Searched for unencoded {}, thus encoded {}, returning {}.", unencodedUsername, base64, optional);
		return optional;
	}

	public Optional<User> get(Utf8StringAsBase64Sequence base64Username) {
		return getQuery(base64Username).getResultList().stream().collect(MoreCollectors.toOptional());
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
