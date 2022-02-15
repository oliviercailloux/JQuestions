package io.github.oliviercailloux.jquestions;

import com.google.common.collect.ImmutableSet;
import io.github.oliviercailloux.jquestions.entities.Question;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequestScoped
public class QuestionService {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(QuestionService.class);

	@Inject
	EntityManager em;
	@Inject
	QueryHelper queryHelper;
	@Inject
	QuestionConverter converter;

	@Transactional
	public Question get(int id) {
		return em.find(Question.class, id);
	}

	@Transactional
	public ImmutableSet<Question> getAll() {
		final TypedQuery<Question> q = em.createQuery(queryHelper.selectAll(Question.class));
		final List<Question> result = q.getResultList();
		return ImmutableSet.copyOf(result);
	}

	@Transactional
	public ImmutableSet<Integer> getAllIds() {
		return getAll().stream().map(Question::getId).collect(ImmutableSet.toImmutableSet());
	}

	@Transactional
	public void persist(Question question) {
		em.persist(question);
	}

	public String getAsXhtml(String phrasingAsciiDoc) {
		return converter.toXhtml(phrasingAsciiDoc);
	}
}
