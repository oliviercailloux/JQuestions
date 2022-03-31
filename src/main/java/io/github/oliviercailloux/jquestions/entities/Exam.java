package io.github.oliviercailloux.jquestions.entities;

import static com.google.common.base.Verify.verify;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MoreCollectors;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Exam {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
	private int id;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "exam")
	List<ExamQuestion> questions;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "exam")
	Set<StudentRegistration> registeredStudents;

	String password;

	public Exam() {
		/* For JPA. */
	}

	public Exam(String password) {
		/*
		 * We shouldn’t end up with empty questions, but I allow this here for
		 * persistence reasons (creating first, then adding).
		 */
		this.questions = new ArrayList<>();
		this.registeredStudents = new LinkedHashSet<>();
		this.password = password;
	}

	public ImmutableList<Question> getQuestions() {
		verify(questions.stream().map(ExamQuestion::getExam).allMatch(e -> e.equals(this)));
		verify(questions.stream().map(ExamQuestion::getQuestion).map(Question::getId).count() == questions.size());
		return questions.stream().map(ExamQuestion::getQuestion).collect(ImmutableList.toImmutableList());
	}

	public Set<StudentRegistration> getRegistrations() {
		verify(registeredStudents.stream().map(StudentRegistration::getExam).allMatch(e -> e.equals(this)));
		return registeredStudents;
	}

	public Optional<StudentRegistration> getRegistration(User u) {
		return getRegistrations().stream().filter(r -> r.getUser().equals(u)).collect(MoreCollectors.toOptional());
	}

	public ImmutableSet<User> getRegisteredStudents() {
		return getRegistrations().stream().map(StudentRegistration::getUser).collect(ImmutableSet.toImmutableSet());
	}

	public String getPassword() {
		return password;
	}

	public void addQuestion(ExamQuestion examQuestion) {
		questions.add(examQuestion);
	}

	public void addStudent(StudentRegistration registration) {
		registeredStudents.add(registration);
	}

	public String guid() {
		return String.valueOf(id);
	}

}
