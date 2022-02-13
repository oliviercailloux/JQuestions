package io.github.oliviercailloux.jquestions;

import com.google.common.collect.ImmutableList;
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
		LOGGER.info("Loading at startup, considering {}.", evt);

		userService.persist(new User("Admin", "adm", User.ADMIN_ROLE));
		userService.persist(new User("Student-test", "test", User.STUDENT_ROLE));
		userService.persist(new User("a", "a", User.STUDENT_ROLE));
		userService.persist(new User("b", "b", User.STUDENT_ROLE));
		userService.persist(new User("c", "c", User.STUDENT_ROLE));

		final String q1ADoc = Resources.toString(getClass().getResource("q1.adoc"), StandardCharsets.UTF_8);
		final Question question = questionParser.parse(q1ADoc);
		questionService.persist(question);
		final Question q2 = questionParser.parse(q1ADoc);
		questionService.persist(q2);

		examService.persist(ImmutableList.of(question, q2), "ep");
	}
}