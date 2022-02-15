package io.github.oliviercailloux.jquestions;

import io.github.oliviercailloux.jquestions.dao.StudentDao;
import io.github.oliviercailloux.jquestions.entities.User;
import java.time.Instant;
import java.util.Set;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

@Path("/user")
public class UserResource {
	@Inject
	UserService userService;

	@RolesAllowed(User.ADMIN_ROLE)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/add")
	public void addStudents(Set<StudentDao> students) {
		students.forEach(this::addStudent);
	}

	private void addStudent(StudentDao student) {
		userService.persist(new User(student.username(), student.password(), Instant.now()));
	}
}
