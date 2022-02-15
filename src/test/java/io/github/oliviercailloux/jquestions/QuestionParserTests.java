package io.github.oliviercailloux.jquestions;

import static com.google.common.base.Verify.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import io.github.oliviercailloux.jquestions.entities.Question;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import org.asciidoctor.Asciidoctor;
import org.junit.jupiter.api.Test;

public class QuestionParserTests {
	@Test
	void testParsing() throws Exception {
		{
			final Matcher matcher = QuestionParser.SECTION_PATTERN.matcher("== Q\nploum");
			verify(matcher.find());
			verify(matcher.group("title").equals("Q"));
			verify(matcher.group("content").equals("ploum"), matcher.group("content"));
		}
		{
			final Matcher matcher = QuestionParser.SECTION_PATTERN.matcher("== Q\rploum\nplim");
			verify(matcher.find());
			verify(matcher.group("title").equals("Q"));
			verify(matcher.group("content").equals("ploum\nplim"), matcher.group("content"));
		}
		{
			final Matcher matcher = QuestionParser.SECTION_PATTERN.matcher("== Q\nploum\rplim\n== Q 2\np2");
			verify(matcher.find());
			verify(matcher.group("title").equals("Q"));
			verify(matcher.group("content").equals("ploum\rplim\n"), matcher.group("content"));
			verify(matcher.find());
			verify(matcher.group("title").equals("Q 2"));
			verify(matcher.group("content").equals("p2"), matcher.group("content"));
		}
		{
			final Matcher matcher = QuestionParser.SECTION_PATTERN.matcher("== Q\nploum\rplim\n\n\r== Q 2\np2");
			verify(matcher.find());
			verify(matcher.group("title").equals("Q"));
			verify(matcher.group("content").equals("ploum\rplim\n\n\r"), matcher.group("content"));
			verify(matcher.find());
			verify(matcher.group("title").equals("Q 2"));
			verify(matcher.group("content").equals("p2"), matcher.group("content"));
		}
		{
			final Matcher matcher = QuestionParser.SECTION_PATTERN.matcher("== Q\nploum\n\n== Q 2\np2");
			verify(matcher.find());
			verify(matcher.group("title").equals("Q"));
			assertEquals("ploum\n\n", matcher.group("content"));
			verify(matcher.find());
			verify(matcher.group("title").equals("Q 2"));
			verify(matcher.group("content").equals("p2"), matcher.group("content"));
		}

		final String asciiDoc = Resources.toString(getClass().getResource("Exam 1.adoc"), StandardCharsets.UTF_8);
		final ImmutableSet<Question> parsed = QuestionParser.instance(Asciidoctor.Factory.create())
				.parseQuestions(asciiDoc);
		assertEquals(6, parsed.size());
	}
}
