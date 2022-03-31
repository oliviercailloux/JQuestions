package io.github.oliviercailloux.jquestions.entities;

import static com.google.common.base.Preconditions.checkNotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.oliviercailloux.jquestions.UserResource;
import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.NumericDate;

/**
 * Bijects with jwtid and jwt representation.
 */
@NamedQuery(name = "get", query = "SELECT r FROM StudentRegistration r INNER JOIN r.user u INNER JOIN r.exam e WHERE u = :student AND e = :exam")
@NamedQuery(name = "registrationFromJwtId", query = "SELECT r FROM StudentRegistration r WHERE r.jwtId = :jwtId")
@Entity
public class StudentRegistration {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
	private int id;

	@ManyToOne(fetch = FetchType.EAGER)
	User user;

	@ManyToOne(fetch = FetchType.EAGER)
	Exam exam;

	@Column(unique = true)
	String jwtId;

	Instant creationTime;

	public StudentRegistration() {
		/* For JPA. */
	}

	public StudentRegistration(User user, Exam exam, String jwtId, Instant creationTime) {
		this.user = checkNotNull(user);
		this.exam = checkNotNull(exam);
		this.jwtId = checkNotNull(jwtId);
		this.creationTime = checkNotNull(creationTime);
	}

	public User getUser() {
		return user;
	}

	public Exam getExam() {
		return exam;
	}

	public String getJwtId() {
		return jwtId;
	}

	public Instant getCreationTime() {
		return creationTime;
	}

	public JwtClaims asClaims() {
		final JwtClaims claims = new JwtClaims();
		claims.setIssuer(UserResource.class.getPackageName());
		/*
		 * “The "aud" (audience) claim identifies the recipients that the JWT is
		 * intended for. Each principal intended to process the JWT MUST identify itself
		 * with a value in the audience claim.” --
		 * https://datatracker.ietf.org/doc/html/rfc7519#section-4.1.3. This token
		 * should be shown to the exam, which will then grant access to itself.
		 */
		claims.setAudience(exam.guid());
		claims.setExpirationTimeMinutesInTheFuture(60);
//        secureRandom = (secureRandom == null) ? new SecureRandom() : secureRandom;
//        byte[] bytes = new byte[length];
//        secureRandom.nextBytes(bytes);
//        byte[] rndbytes = ByteUtil.randomBytes(numberOfBytes);
//        String jti = Base64Url.encode(rndbytes);
		claims.setJwtId(jwtId);
		claims.setIssuedAt(NumericDate.fromSeconds(creationTime.getEpochSecond()));
		claims.setNotBeforeMinutesInThePast(2);
		claims.setSubject(user.getUsername());
		claims.setStringListClaim("groups", User.STUDENT_ROLE);

		return claims;
	}
}
