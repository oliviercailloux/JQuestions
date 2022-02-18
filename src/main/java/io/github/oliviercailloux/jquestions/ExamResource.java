package io.github.oliviercailloux.jquestions;

import com.google.common.collect.ImmutableSet;
import io.github.oliviercailloux.jquestions.dao.AggregatedAnswersDao;
import io.github.oliviercailloux.jquestions.entities.Answer;
import io.github.oliviercailloux.jquestions.entities.User;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/exam/{examId}")
public class ExamResource {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(ExamResource.class);

	@Inject
	UserService userService;

	@Inject
	ExamService examService;

	@Inject
	QuestionService questionService;

	@Context
	SecurityContext securityContext;

	@PathParam("examId")
	int examId;

	@POST
	@RolesAllowed(User.STUDENT_ROLE)
	@Path("/register")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	@Transactional
	public String registerStudent(String examPassword) {
		final User current = userService.getCurrent(securityContext);
		return examService.registerStudent(current, examId, examPassword);
	}

	/**
	 * TODO
	 */
	@POST
	@RolesAllowed(User.ADMIN_ROLE)
	@Path("/unregister")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	@Transactional
	public Instant unregisterStudentAndDeleteAll(String username) {
		final User student = userService.get(username).orElseThrow(NotFoundException::new);
		examService.removeAllAnswers(student);
		return examService.removeRegistration(student, examId);
	}

	/**
	 * NOT @Consumes(MediaType.TEXT_PLAIN) with password in body: it is unclear
	 * whether HTTP accepts this, and fetch API forbids it.
	 * https://github.com/whatwg/fetch/issues/83.
	 */
	@GET
	@RolesAllowed({ User.ADMIN_ROLE, User.STUDENT_ROLE })
	@Path("/list")
	@Produces({ MediaType.APPLICATION_JSON })
	@Transactional
	public ImmutableSet<Integer> getQuestionIds(@QueryParam("personal") String personalPassword) {
		LOGGER.debug("Got request to list, with password {}.", personalPassword);
		final User current = userService.getCurrent(securityContext);
		return examService.getQuestionIdsFor(examId, current, personalPassword);
	}

	/**
	 * Should think about this registration fct.
	 */
	@GET
	@RolesAllowed(User.ADMIN_ROLE)
//	@Path("/students")
	@Produces({ MediaType.APPLICATION_JSON })
	@Transactional
	public ImmutableSet<User> getStudents() {
		return examService.getStudents();
	}

	@POST
	@RolesAllowed(User.STUDENT_ROLE)
	@Path("/answer/{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Transactional
	public void answer(@PathParam("id") int questionId, Set<Integer> adoptedClaims) {
		final User current = userService.getCurrent(securityContext);
		examService.answer(current, questionService.get(questionId), adoptedClaims);
	}

	@GET
	@RolesAllowed({ User.ADMIN_ROLE, User.STUDENT_ROLE })
	@Path("/answer/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	@Transactional
	public Response getAdoptedClaims(@PathParam("id") int questionId) {
		final Optional<Answer> answer = examService.getAnswer(questionService.get(questionId),
				userService.getCurrent(securityContext));
		return answer.map(a -> Response.ok(a.getAdoptedClaims())).orElse(Response.noContent()).build();
	}

	@GET
	@RolesAllowed(User.ADMIN_ROLE)
	@Path("/aggregatedAnswers/{id}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public AggregatedAnswersDao getAggregatedAdoptedClaims(@PathParam("id") int questionId) {
		return examService.getAggregatedAnswers(questionService.get(questionId));
	}
}