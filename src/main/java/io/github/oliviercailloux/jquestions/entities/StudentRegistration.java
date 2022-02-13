package io.github.oliviercailloux.jquestions.entities;

import static com.google.common.base.Preconditions.checkNotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.Instant;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

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

	String personalExamPassword;

	Instant creationTime;

	public StudentRegistration() {
		/* For JPA. */
	}

	public StudentRegistration(User user, Exam exam, String personalExamPassword, Instant creationTime) {
		this.user = checkNotNull(user);
		this.exam = checkNotNull(exam);
		this.personalExamPassword = checkNotNull(personalExamPassword);
		this.creationTime = checkNotNull(creationTime);
	}

	public User getUser() {
		return user;
	}

	public Exam getExam() {
		return exam;
	}

	public String getPersonalExamPassword() {
		return personalExamPassword;
	}

	public Instant getCreationTime() {
		return creationTime;
	}
}
