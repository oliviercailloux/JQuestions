package io.github.oliviercailloux.jquestions;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.VerifyException;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.github.oliviercailloux.jaris.exceptions.Unchecker;
import io.github.oliviercailloux.jaris.xml.DomHelper;
import io.github.oliviercailloux.jaris.xml.XmlName;
import io.github.oliviercailloux.publish.DocBookHelper;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.IntStream;
import javax.xml.transform.stream.StreamSource;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class QuestionConverter {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(QuestionConverter.class);

	private static final Unchecker<IOException, VerifyException> NO_IO = Unchecker.wrappingWith(VerifyException::new);

	public static QuestionConverter instance(Asciidoctor asciidoctor) {
		return new QuestionConverter(asciidoctor);
	}

	private final Asciidoctor asciidoctor;

	private QuestionConverter(Asciidoctor asciidoctor) {
		this.asciidoctor = checkNotNull(asciidoctor);
	}

	public String toXhtml(String phrasingAsciiDoc) {
		final String phrasingDocBook = asciidoctor.convert(phrasingAsciiDoc,
				Options.builder().headerFooter(true).backend("docbook").build());
		LOGGER.info("Validating DocBook.");
		final DocBookHelper helper = DocBookHelper.usingDefaultFactory();
		NO_IO.call(() -> helper.verifyValid(new StreamSource(new StringReader(phrasingDocBook))));
		LOGGER.info("Converting DocBook.");
		final StreamSource localSource = new StreamSource(
				"file:///usr/share/xml/docbook/stylesheet/docbook-xsl-ns/xhtml5/docbook.xsl");
		final StreamSource phrasingDocBookSource = new StreamSource(new StringReader(phrasingDocBook));
		final String phrasingXhtml = helper
				.getDocBookTransformer(localSource, ImmutableMap.of(XmlName.localName("docbook.css.source"), ""))
				.transform(phrasingDocBookSource);
//		final String phrasingXhtml = helper.docBookTo(phrasingDocBookSource,
//				DocBookHelper.TO_XHTML_STYLESHEET);
//				localSource);
		NO_IO.call(() -> Files.writeString(Path.of("q1.html"), phrasingXhtml));
		final DomHelper domHelper = DomHelper.domHelper();
		final Document phrasingDoc = domHelper.asDocument(new StreamSource(new StringReader(phrasingXhtml)));
		final ImmutableList<Node> level0 = DomHelper.toList(phrasingDoc.getChildNodes());
		checkArgument(level0.size() == 2);
		final Node docType = level0.get(0);
		checkArgument(docType.getNodeType() == Node.DOCUMENT_TYPE_NODE);
		final Node htmlElement = level0.get(1);
		checkArgument(htmlElement.getNodeType() == Node.ELEMENT_NODE);
		checkArgument(htmlElement.getNodeName().equals("html"));
		final ImmutableList<Element> level1 = DomHelper.toElements(htmlElement.getChildNodes());
		checkArgument(level1.size() == 2);
		checkArgument(level1.get(0).getNodeName().equals("head"));
		final Element body = level1.get(1);
		checkArgument(body.getNodeName().equals("body"));
//		final Element body = level1.stream().filter(e -> e.getLocalName().equals("body"))
//				.collect(MoreCollectors.onlyElement());
		LOGGER.info("Reached: {}.", DomHelper.toDebugString(body));
		final ImmutableList<Element> inBody = DomHelper.toElements(body.getChildNodes());
		checkArgument(inBody.size() == 1);
		final Element section = inBody.get(0);
		LOGGER.info("Name reached: {}.", section.getNodeName());
		checkArgument(section.getNodeName().equals("section"));
		final ImmutableList<Element> liElements = DomHelper
				.toElements(section.getElementsByTagNameNS(DomHelper.XHTML_NS_URI.toString(), "li"));

		IntStream.range(1, liElements.size() + 1).forEach(i -> liElements.get(i - 1)
				.insertBefore(newCheckbox(phrasingDoc, i), liElements.get(i - 1).getFirstChild()));
		return domHelper.toString(phrasingDoc);
	}

	private Element newCheckbox(final Document document, final int claimNumber) {
		final Element checkboxElement = document.createElementNS(DomHelper.XHTML_NS_URI.toString(), "input");
		checkboxElement.setAttribute("type", "checkbox");
		checkboxElement.setAttribute("id", "claim-" + claimNumber);
		return checkboxElement;
	}

}
