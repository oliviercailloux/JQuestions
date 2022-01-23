package io.github.oliviercailloux.jquestions;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Verify.verify;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MoreCollectors;
import io.github.oliviercailloux.jquestions.entities.Question;
import java.util.List;
import java.util.stream.IntStream;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Options;
import org.asciidoctor.ast.Document;
import org.asciidoctor.ast.ListItem;
import org.asciidoctor.ast.StructuralNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuestionParser {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(QuestionParser.class);

	private static final String TRUE_CLAIM_MARK = "[true-claim] ";

	public static QuestionParser instance(Asciidoctor asciidoctor) {
		return new QuestionParser(asciidoctor);
	}

	private final Asciidoctor asciidoctor;

	private QuestionParser(Asciidoctor asciidoctor) {
		this.asciidoctor = checkNotNull(asciidoctor);
	}

	public Question parse(String questionAsciiDoc) {
		/* https://github.com/asciidoctor/asciidoctor/issues/2716 */
		final Document doc = asciidoctor.load(questionAsciiDoc, Options.builder().build());

		final List<StructuralNode> blocks = doc.getBlocks();
		final ImmutableList<StructuralNode> listBlocks = blocks.stream().filter(b -> b.getContext().equals("olist"))
				.collect(ImmutableList.toImmutableList());
		checkArgument(listBlocks.size() == 1, listBlocks.size());
		final StructuralNode listStructural = listBlocks.stream().collect(MoreCollectors.onlyElement());
		final org.asciidoctor.ast.List listOfClaims = (org.asciidoctor.ast.List) listStructural;
		checkArgument(listOfClaims.getContext().equals("olist"));

		final List<StructuralNode> claims = listOfClaims.getItems();
		checkArgument(!claims.isEmpty(), claims.size());
		checkArgument(claims.stream().allMatch(c -> c.getContext().equals("list_item")));
		checkArgument(claims.stream().allMatch(c -> ListItem.class.isAssignableFrom(c.getClass())));

		final ImmutableList<ListItem> claimItems = claims.stream().map(c -> (ListItem) c)
				.collect(ImmutableList.toImmutableList());
		checkArgument(claimItems.stream().allMatch(ListItem::hasText));

		final ImmutableSet<Integer> trueClaims = IntStream.range(1, claimItems.size())
				.filter(i -> claimItems.get(i - 1).getText().startsWith(TRUE_CLAIM_MARK)).boxed()
				.collect(ImmutableSet.toImmutableSet());

		final String phrasing = questionAsciiDoc.replace(TRUE_CLAIM_MARK, "");

		return new Question(phrasing, trueClaims);
	}

	@SuppressWarnings("unused")
	private void removeTrueClaimMark(ListItem claim) {
		/*
		 * Unfortunately, impossible to obtain the modified AsciiDoc form.
		 * https://github.com/asciidoctor/asciidoctorj/issues/949
		 */
//		claimItems.forEach(this::removeTrueClaimMark);

		final boolean marked = claim.getText().startsWith(TRUE_CLAIM_MARK);
		final String source = claim.getSource();
		final boolean markedSource = source.startsWith(TRUE_CLAIM_MARK);
		checkArgument(marked == markedSource);

		if (!marked) {
			return;
		}

		final String shortenedSource = source.substring(TRUE_CLAIM_MARK.length());
		verify(!source.equals(shortenedSource));
		claim.setSource(shortenedSource);
	}
}
