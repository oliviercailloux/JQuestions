package io.github.oliviercailloux.jquestions;

import static com.google.common.base.Verify.verify;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MoreCollectors;
import com.google.common.primitives.SignedBytes;
import io.github.oliviercailloux.jquestions.dao.AggregatedAnswersDao;
import io.github.oliviercailloux.jquestions.entities.Answer;
import io.github.oliviercailloux.jquestions.entities.Exam;
import io.github.oliviercailloux.jquestions.entities.ExamQuestion;
import io.github.oliviercailloux.jquestions.entities.Question;
import io.github.oliviercailloux.jquestions.entities.StudentRegistration;
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
import javax.ws.rs.ClientErrorException;
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
	public Exam get(int id) {
		return em.find(Exam.class, id);
	}

	@Transactional
	public void persist(List<Question> questions, String password) {
		final Exam exam = new Exam(password);

		final ImmutableList<ExamQuestion> qs = questions.stream().map(q -> new ExamQuestion(q, exam))
				.collect(ImmutableList.toImmutableList());
		qs.stream().forEach(exam::addQuestion);

		em.persist(exam);
		qs.stream().forEach(em::persist);
	}

	@Transactional
	public String registerStudent(User student, int examId, String examPassword) throws WebApplicationException {
		final Exam exam = get(examId);
		if (!exam.getPassword().equals(examPassword)) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		final Set<User> registeredStudents = exam.getRegisteredStudents();
		LOGGER.debug("Registered to {}: {}.", examId, registeredStudents);
		if (registeredStudents.contains(student)) {
			throw new WebApplicationException(Response.Status.CONFLICT);
		}
		LOGGER.debug("Student {} not contained.", student);

		final String personalPassword = generatePassword();
		final StudentRegistration registration = new StudentRegistration(student, exam, personalPassword,
				Instant.now());
		exam.addStudent(registration);
		em.persist(registration);
		em.persist(exam);
		return personalPassword;
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

	@Transactional
	public ImmutableSet<Integer> getQuestionIdsFor(int examId, User current, String personalPassword)
			throws WebApplicationException {
		final Exam exam = get(examId);

		if (!current.getRole().equals(User.ADMIN_ROLE)) {
			final Optional<StudentRegistration> registrationOpt = exam.getRegistration(current);
			final StudentRegistration registration = registrationOpt
					.orElseThrow(() -> new WebApplicationException(Response.Status.NOT_FOUND));
			if (!registration.getPersonalExamPassword().equals(personalPassword)) {
				throw new WebApplicationException(Response.Status.NOT_FOUND);
			}
		}

		final ImmutableSet<Integer> ids = exam.getQuestions().stream().map(Question::getId)
				.collect(ImmutableSet.toImmutableSet());
		LOGGER.info("Returning ids {}.", ids);
		return ids;
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

	@Transactional
	public Optional<Answer> getAnswer(Question question, User student) {
		final List<Answer> answers = em.createNamedQuery("getAnswerFromStudentAndQuestion", Answer.class)
				.setParameter("student", student).setParameter("question", question).getResultList();
		verify(answers.size() <= 1, answers.toString());
		return answers.stream().collect(MoreCollectors.toOptional());
	}

	@Transactional
	public AggregatedAnswersDao getAggregatedAnswers(Question question) {
		final List<Answer> answers = em.createNamedQuery("getAnswersToQuestion", Answer.class)
				.setParameter("question", question).getResultList();
		final ImmutableMultiset<Integer> countByClaim = answers.stream().flatMap(a -> a.getAdoptedClaims().stream())
				.collect(ImmutableMultiset.toImmutableMultiset());

		final ImmutableMap<String, Integer> countByClaimAsMap = countByClaim.elementSet().stream()
				.collect(ImmutableMap.toImmutableMap(c -> c.toString(), countByClaim::count));
		return new AggregatedAnswersDao(answers.size(), countByClaimAsMap);
	}

	@Transactional
	public int removeAllAnswers(int examId, User student) {
		final List<Answer> answers = em.createNamedQuery("getAnswersFromStudentAndExam", Answer.class)
				.setParameter("student", student).setParameter("exam", examId).getResultList();
		answers.forEach(em::remove);
		return answers.size();
	}

	/**
	 * TODO remove this method
	 */
	@Transactional
	public int removeAllAnswers(User student) {
		final List<Answer> answers = em.createNamedQuery("getAnswersFromStudent", Answer.class)
				.setParameter("student", student).getResultList();
		answers.forEach(em::remove);
		return answers.size();
	}

	@Transactional
	public Instant removeRegistration(User student, int exam) throws ClientErrorException {
		return removeRegistration(student, get(exam));
	}

	@Transactional
	public Instant removeRegistration(User student, Exam exam) throws ClientErrorException {
		final List<StudentRegistration> registrations = em.createNamedQuery("get", StudentRegistration.class)
				.setParameter("student", student).setParameter("exam", exam).getResultList();
		verify(registrations.size() <= 1, registrations.toString());
		final Optional<StudentRegistration> registrationOpt = registrations.stream()
				.collect(MoreCollectors.toOptional());
		final StudentRegistration registration = registrationOpt
				.orElseThrow(() -> new ClientErrorException("Not registered", Response.Status.CONFLICT));
		em.remove(registration);
		return registration.getCreationTime();
	}

}
