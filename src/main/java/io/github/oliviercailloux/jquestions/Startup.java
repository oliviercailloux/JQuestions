package io.github.oliviercailloux.jquestions;

import com.google.common.io.Resources;
import io.github.oliviercailloux.jquestions.entities.Question;
import io.github.oliviercailloux.jquestions.entities.User;
import io.quarkus.runtime.StartupEvent;
import java.io.IOException;
import java.net.URL;
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
	QuestionService questionService;
	@Inject
	UserService userService;

	@Inject
	QuestionParser questionParser;

	@Transactional
	public void loadAtStartup(@Observes StartupEvent evt) throws IOException {
		LOGGER.info("Loading at startup, considering {}.", evt);

		userService.persist(new User("Admin", "adm", User.ADMIN_ROLE));

		final URL q1Url = getClass().getResource("q1.adoc");
		final String q1ADoc = Resources.toString(q1Url, StandardCharsets.UTF_8);
		final Question question = questionParser.parse(q1ADoc);
		questionService.persist(question);
	}
}