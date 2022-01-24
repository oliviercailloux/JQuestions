package io.github.oliviercailloux.jquestions;

import static com.google.common.base.Verify.verify;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MoreCollectors;
import com.google.common.primitives.SignedBytes;
import io.github.oliviercailloux.jquestions.entities.Answer;
import io.github.oliviercailloux.jquestions.entities.Question;
import io.github.oliviercailloux.jquestions.entities.User;
import java.text.BreakIterator;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequestScoped
public class ExamService {
	public static final int PASSWORD_CODE_POINTS = 70;

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
//		final int[] codePoints = r.ints(0, Character.MAX_CODE_POINT + 1)
		/*
		 * Let’s stick to 16 bits code points because I have problems dealing with the
		 * higher ones in javascript.
		 */
		final int[] codePoints = r.ints(0, 0xFFFF + 1)
				.filter(p -> !forbiddenTypes.contains(SignedBytes.checkedCast(Character.getType(p))))
				.limit(PASSWORD_CODE_POINTS).toArray();
		verify(codePoints.length == PASSWORD_CODE_POINTS);
		final String password = new String(codePoints, 0, codePoints.length);
		/*
		 * https://stackoverflow.com/a/7697624/,
		 * https://unicode-org.github.io/icu-docs/apidoc/released/icu4j/com/ibm/icu/text
		 * /BreakIterator.html
		 */
		final BreakIterator it = BreakIterator.getCharacterInstance(Locale.ROOT);
		it.setText(password);
		int count = 0;
		while (it.next() != BreakIterator.DONE) {
			count++;
		}
		verify(password.codePointCount(0, password.length()) == PASSWORD_CODE_POINTS);
		verify(count <= PASSWORD_CODE_POINTS);
		return password;
	}

	public ImmutableSet<Integer> getExamFor(@SuppressWarnings("unused") User current) {
		return questionService.getAllIds();
	}

	public ImmutableSet<User> getStudents() {
		return userService.getStudents();
	}

	@Transactional
	public void answer(User student, Question question, Set<Integer> adoptedClaims) {
		final Answer newAnswer = new Answer(student, question, adoptedClaims);
		final Optional<Answer> current = getAnswer(question, student);
//		LOGGER.info(current.map(a -> "Replacing").orElse("Storing") + " answer from {} to {}.", current, question);
		current.ifPresent(em::remove);
		/*
		 * https://jira.spring.io/si/jira.issueviews:issue-html/DATAJPA-727/DATAJPA-727.
		 * html, https://stackoverflow.com/questions/18853146/insert-after-delete-same-
		 * transaction-in-spring-data-jpa
		 */
		current.ifPresent(a -> em.flush());
		em.persist(newAnswer);
	}

	public Optional<Answer> getAnswer(Question question, User student) {
		final List<Answer> answers = em.createNamedQuery("getFromStudentAndQuestion", Answer.class)
				.setParameter("student", student).setParameter("question", question).getResultList();
		verify(answers.size() <= 1, answers.toString());
		return answers.stream().collect(MoreCollectors.toOptional());
	}

}
