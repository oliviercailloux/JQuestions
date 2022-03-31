package io.github.oliviercailloux.jquestions.entities;

import java.util.Set;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
public class Question {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	/**
	 * including marked-up claims
	 */
	@Lob
	String asciiDocPhrasing;

	@ElementCollection
	Set<Integer> trueClaims;

	public Question() {
		/* For JPA. */
	}

	public Question(String asciiDocPhrasing, Set<Integer> trueClaims) {
		this.asciiDocPhrasing = asciiDocPhrasing;
		this.trueClaims = trueClaims;
	}

	public int getId() {
		return id;
	}

	public String getAsciiDocPhrasing() {
		return asciiDocPhrasing;
	}

	public Set<Integer> getTrueClaims() {
		return trueClaims;
	}
}
