package io.github.oliviercailloux.jquestions.entities;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Set;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Answer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
	private int id;

	@ManyToOne(fetch = FetchType.EAGER)
	User student;

	@ManyToOne(fetch = FetchType.EAGER)
	Question question;

	@ElementCollection
	Set<Integer> adoptedClaims;

	public Answer() {
		/* For JPA. */
	}

	public Answer(User student, Question question, Set<Integer> adoptedClaims) {
		this.student = checkNotNull(student);
		this.question = checkNotNull(question);
		this.adoptedClaims = checkNotNull(adoptedClaims);

		checkArgument(student.role.equals(User.STUDENT_ROLE));
		checkArgument(question.getTrueClaims().containsAll(adoptedClaims));
	}
}
