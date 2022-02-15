package io.github.oliviercailloux.jquestions;

import com.google.common.collect.ImmutableSet;
import io.github.oliviercailloux.jquestions.dao.StudentDao;
import io.github.oliviercailloux.jquestions.entities.User;
import java.time.Instant;
import java.util.Set;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/user")
public class UserResource {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(UserResource.class);

	@Inject
	UserService userService;

	@RolesAllowed(User.ADMIN_ROLE)
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/add")
	@Transactional
	public int addStudents(Set<StudentDao> students) {
		if (students == null) {
			throw new BadRequestException();
		}
//		final ImmutableSet<User> presentUsers = students.stream().map(StudentDao::username).map(userService::get).filter(Optional::isPresent).map(Optional::orElseThrow).collect(ImmutableSet.toImmutableSet());
		final ImmutableSet<StudentDao> absentStudents = students.stream()
				.filter(s -> userService.get(s.username()).isEmpty()).collect(ImmutableSet.toImmutableSet());
		absentStudents.forEach(this::addStudent);
		return absentStudents.size();
	}

	private void addStudent(StudentDao student) {
		userService.persist(new User(student.username(), student.password(), Instant.now()));
	}
}
