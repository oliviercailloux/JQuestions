package io.github.oliviercailloux.jquestions;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Verify.verify;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MoreCollectors;
import com.google.common.primitives.SignedBytes;
import io.github.oliviercailloux.jaris.xml.DomHelper;
import io.github.oliviercailloux.jquestions.entities.User;
import io.github.oliviercailloux.publish.DocBookHelper;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Random;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.xml.transform.stream.StreamSource;
import org.asciidoctor.Asciidoctor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

@RequestScoped
public class ExamService {
	public static final int MINIMAL_PASSWORD_LENGTH = 70;

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(ExamService.class);

	@Inject
	EntityManager em;
	@Inject
	UserService userService;

	@Inject
	QuestionService questionService;

	@Transactional
	public String connectStudent(String username) throws WebApplicationException {
		if (userService.exists(username)) {
			throw new WebApplicationException(Response.Status.CONFLICT);
		}

		final String password = generatePassword();
		final User student = new User(username, password, Instant.now());
		em.persist(student);
		return password;
	}

	private String generatePassword() {
		/*
		 * Using https://stackoverflow.com/q/41109905/ and
		 * https://stackoverflow.com/a/57381369/.
		 */
		final Random r = new Random();
		final ImmutableSet<Byte> forbiddenTypes = ImmutableSet.of(Character.PRIVATE_USE, Character.SURROGATE,
				Character.UNASSIGNED);
		final int[] codePoints = r.ints(0, Character.MAX_CODE_POINT + 1)
				.filter(p -> !forbiddenTypes.contains(SignedBytes.checkedCast(Character.getType(p))))
				.limit(MINIMAL_PASSWORD_LENGTH).toArray();
		verify(codePoints.length == MINIMAL_PASSWORD_LENGTH);
		final String password = new String(codePoints, 0, codePoints.length);
		verify(password.codePointCount(0, password.length()) == MINIMAL_PASSWORD_LENGTH);
		verify(password.length() >= MINIMAL_PASSWORD_LENGTH);
		return password;
	}

	public ImmutableSet<Integer> getExamFor(@SuppressWarnings("unused") User current) {
		return questionService.getAllIds();
	}

}
