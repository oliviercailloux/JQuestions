package io.github.oliviercailloux.jquestions;

import io.github.oliviercailloux.jquestions.dao.StudentDao;
import io.github.oliviercailloux.jquestions.dao.UserDao;
import io.github.oliviercailloux.jquestions.entities.User;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/user")
public class UserResource {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(UserResource.class);

	@Context
	SecurityContext securityContext;

	@Inject
	UserService userService;

	@Path("/me")
	@GET
	@PermitAll
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public UserDao me() {
		if (securityContext.getUserPrincipal() == null) {
			return null;
		}
		final User current = userService.getCurrent(securityContext);
		return new UserDao(current.getUsername(), current.getRole());
	}

	/**
	 * Adds students or replace their password, if they exist already.
	 *
	 * @param students no admin
	 * @return the number of students whose username did not exist already;
	 *         equivalently, who were added (the other ones either had the given
	 *         password already and was left untouched, or their password was
	 *         changed)
	 */
	@Path("/put")
	@PUT
	@RolesAllowed(User.ADMIN_ROLE)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Transactional
	public int putStudents(@NotNull Set<StudentDao> students) {
//		final ImmutableSet<User> presentUsers = students.stream().map(StudentDao::username).map(userService::get).flatMap(Optional::stream)
//				.collect(ImmutableSet.toImmutableSet());
//		final ImmutableSet<StudentDao> absentStudents = students.stream()
//				.filter(s -> userService.get(s.username()).isEmpty()).collect(ImmutableSet.toImmutableSet());
		int added = 0;
		for (StudentDao student : students) {
			final String username = student.username();
			final Optional<User> user = userService.get(username);
			if (user.isPresent()) {
				user.get().setPassword(student.password());
				userService.merge(user.get());
			} else {
				userService.persist(new User(student.username(), student.password(), Instant.now()));
				++added;
			}
		}
		return added;
	}
}
