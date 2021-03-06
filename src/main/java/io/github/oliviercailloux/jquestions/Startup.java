package io.github.oliviercailloux.jquestions;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import io.github.oliviercailloux.jquestions.entities.Question;
import io.github.oliviercailloux.jquestions.entities.User;
import io.quarkus.runtime.StartupEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class Startup {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(Startup.class);

	@Inject
	EntityManager em;

	@Inject
	UserService userService;

	@Inject
	QuestionService questionService;
	@Inject
	ExamService examService;

	@Inject
	QuestionParser questionParser;

	@Transactional
	public void loadAtStartup(@Observes StartupEvent evt) throws IOException {
		LOGGER.info("Loading at startup due to {}.", evt);

		userService.persist(new User("Admin", "adm", User.ADMIN_ROLE));
		userService.persist(new User("Student-test", "test", User.STUDENT_ROLE));
		userService.persist(new User("a", "a", User.STUDENT_ROLE));

		final String examAsciiDoc = Resources.toString(getClass().getResource("Exam.adoc"), StandardCharsets.UTF_8);
		final ImmutableSet<Question> questions = questionParser.parseQuestions(examAsciiDoc);
		questions.forEach(questionService::persist);

		examService.persist(questions.asList(), "zepw");
	}
}