package io.github.oliviercailloux.jquestions.entities;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.MoreObjects;
import io.github.oliviercailloux.wutils.Utf8StringAsBase64Sequence;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
import java.time.Instant;
import java.util.Optional;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@UserDefinition
@NamedQuery(name = "getBase64User", query = "SELECT u FROM User u WHERE u.usernameUtf8ThenBase64Encoded = :username")
@NamedQuery(name = "getStudents", query = "SELECT u FROM User u WHERE u.role = " + User.STUDENT_ROLE)
public class User {
	public static final String ADMIN_ROLE = "Admin";

	public static final String STUDENT_ROLE = "Student";

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(User.class);

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
	private int id;

	@Username
	@NotNull
	@Column(unique = true)
	String usernameUtf8ThenBase64Encoded;

	@Password
	@NotNull
	@Column(unique = true)
	@JsonIgnore
	String passwordUtf8ThenBase64EncodedThenEncrypted;

	@Roles
	@NotNull
	String role;

	Instant firstPostTime;

	public User() {
		/* For JPA. */
	}

	public User(String username, String password, String role) {
		this(username, password, role, Optional.empty());
	}

	public User(String username, String password, Instant firstPostTime) {
		this(username, password, STUDENT_ROLE, Optional.of(firstPostTime));
	}

	private User(String username, String password, String role, Optional<Instant> firstPostTime) {
		this.usernameUtf8ThenBase64Encoded = Utf8StringAsBase64Sequence.asBase64Sequence(username).toString();
		LOGGER.debug("Username {} stored as {}.", username, this.usernameUtf8ThenBase64Encoded);
		final String passwordUtf8ThenBase64Encoded = Utf8StringAsBase64Sequence.asBase64Sequence(password).toString();
		this.passwordUtf8ThenBase64EncodedThenEncrypted = BcryptUtil.bcryptHash(passwordUtf8ThenBase64Encoded);
		this.role = checkNotNull(role);
		this.firstPostTime = checkNotNull(firstPostTime).orElse(null);
		if (firstPostTime.isPresent()) {
			checkArgument(role.equals(STUDENT_ROLE));
		}
	}

	public String getUsername() {
		return Utf8StringAsBase64Sequence.fromUtf8StringAsBase64Sequence(usernameUtf8ThenBase64Encoded)
				.decodeToString();
	}

	public String getRole() {
		return role;
	}

	public Optional<Instant> getFirstPostTime() {
		return Optional.ofNullable(firstPostTime);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("id", id).add("username base64", usernameUtf8ThenBase64Encoded)
				.add("role", role).add("firstPostTime", firstPostTime).toString();
	}
}
