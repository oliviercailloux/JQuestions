package io.github.oliviercailloux.jquestions.entities;

import static com.google.common.base.Preconditions.checkNotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.MoreObjects;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class ExamQuestion {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
	private int id;

	@ManyToOne(fetch = FetchType.EAGER)
	Question question;

	@ManyToOne(fetch = FetchType.EAGER)
	Exam exam;

	public ExamQuestion() {
		/* For JPA. */
	}

	public ExamQuestion(Question question, Exam exam) {
		this.question = checkNotNull(question);
		this.exam = checkNotNull(exam);
	}

	public Question getQuestion() {
		return question;
	}

	public Exam getExam() {
		return exam;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("id", id).add("question", question).add("exam", exam).toString();
	}
}
