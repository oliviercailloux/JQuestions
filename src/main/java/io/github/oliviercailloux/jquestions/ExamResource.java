package io.github.oliviercailloux.jquestions;

import com.google.common.collect.ImmutableSet;
import io.github.oliviercailloux.jquestions.entities.User;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/exam")
public class ExamResource {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(ExamResource.class);

	@Context
	SecurityContext securityContext;

	@Inject
	ExamService examService;

	@io.quarkus.security.User
	User current;

	@POST
	@PermitAll
	@Path("/connect")
	@Consumes({ MediaType.TEXT_PLAIN })
	@Produces({ MediaType.TEXT_PLAIN })
	@Transactional
	public String connectStudent(String username) {
		return examService.connectStudent(username);
	}

	@GET
	@RolesAllowed({ User.ADMIN_ROLE, User.STUDENT_ROLE })
	@Path("/list")
	@Produces({ MediaType.APPLICATION_JSON })
	@Transactional
	public ImmutableSet<Integer> getQuestionIds() {
		return examService.getExamFor(current);
	}

	/**
	 * Should think about this registration fct.
	 */
	@GET
	@PermitAll
//	@Path("/students")
	@Produces({ MediaType.APPLICATION_JSON })
	@Transactional
	public ImmutableSet<User> getStudents() {
		return examService.getStudents();
	}
}