package io.github.oliviercailloux.jquestions;

import io.github.oliviercailloux.jquestions.entities.User;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/question")
public class QuestionResource {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(QuestionResource.class);

	@Inject
	QuestionService questionService;

	@io.quarkus.security.User
	User current;

	@GET
	@RolesAllowed({ User.ADMIN_ROLE, User.STUDENT_ROLE })
	@Path("/phrasing/{id}")
	@Produces({ "text/asciidoc" })
	@Transactional
	public String getPhrasingAsciiDoc(@PathParam("id") int id) {
		return questionService.get(id).getAsciiDocPhrasing();
	}

	@GET
	@RolesAllowed({ User.ADMIN_ROLE, User.STUDENT_ROLE })
	@Path("/phrasing/{id}")
	@Produces({ MediaType.APPLICATION_XHTML_XML })
	@Transactional
	public String getPhrasingXhtml(@PathParam("id") int id) {
		return questionService.getAsXhtml(questionService.get(id).getAsciiDocPhrasing());
	}
}