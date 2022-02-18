package io.github.oliviercailloux.jquestions.dao;

import static com.google.common.base.Verify.verify;

import java.util.Map;

public record AggregatedAnswersDao(int countAnswers, Map<String, Integer> countByClaim) {
	public AggregatedAnswersDao {
		verify(countByClaim.values().stream().allMatch(n -> n <= countAnswers));
	}
}
