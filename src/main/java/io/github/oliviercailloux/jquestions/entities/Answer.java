package io.github.oliviercailloux.jquestions.entities;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.MoreObjects;
import java.util.Set;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "student_id", "question_id" }) })
@NamedQuery(name = "getAnswerFromStudentAndQuestion", query = "SELECT a FROM Answer a INNER JOIN a.student s INNER JOIN a.question q WHERE s = :student AND q = :question")
@NamedQuery(name = "getAnswersFromStudent", query = "SELECT a FROM Answer a INNER JOIN a.student s WHERE s = :student")
@NamedQuery(name = "getAnswersToQuestion", query = "SELECT a FROM Answer a INNER JOIN a.question q WHERE q = :question")
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
	}

	public User getStudent() {
		return student;
	}

	public Question getQuestion() {
		return question;
	}

	public Set<Integer> getAdoptedClaims() {
		return adoptedClaims;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("id", id).add("student", student).add("question", question)
				.add("adopted claims", adoptedClaims).toString();
	}
}
