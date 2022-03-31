package io.github.oliviercailloux.quarkus;

import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.oliviercailloux.jquestions.UserService;
import io.github.oliviercailloux.jquestions.dao.StudentDao;
import io.github.oliviercailloux.jquestions.entities.User;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import java.time.Instant;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@QuarkusTest
public class TestTransactionTests {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(TestTransactionTests.class);

	private static StudentDao student;

	@BeforeAll
	public static void initData() {
		student = new StudentDao("user test " + Instant.now().toString(), "p");
	}

	@Inject
	UserService userService;

	/**
	 * Note that it is important that we use different user instances, otherwise
	 * their entity state remains.
	 */
	private User newUserInstance() {
		return new User(student.username(), student.password(), User.STUDENT_ROLE);
	}

	@Test
	@TestTransaction
	public void testAddStudent() {
		userService.persist(newUserInstance());
	}

	@Test
	@TestTransaction
	public void testAddSameTwoUsers() {
		userService.persist(newUserInstance());
	}

	@Test
	@TestTransaction
	public void testAddDuplicatedUsers() {
		userService.persist(newUserInstance());
		assertThrows(PersistenceException.class, () -> userService.persist(newUserInstance()));
	}
}