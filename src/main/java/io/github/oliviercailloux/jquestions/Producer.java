package io.github.oliviercailloux.jquestions;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import org.asciidoctor.Asciidoctor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class Producer {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(Producer.class);

	@Produces
	@ApplicationScoped
	public Asciidoctor getAsciidoctor() {
		LOGGER.debug("Creating asciidoctor factory.");
		final Asciidoctor asciidoctor = Asciidoctor.Factory.create();
		LOGGER.debug("Created asciidoctor factory.");
		return asciidoctor;
	}

	@Produces
	@ApplicationScoped
	public QuestionParser getParser(Asciidoctor asciidoctor) {
		LOGGER.debug("Producing QuestionParser instance.");
		final QuestionParser instance = QuestionParser.instance(asciidoctor);
		return instance;
	}

	@Produces
	@ApplicationScoped
	public QuestionConverter getConverter(Asciidoctor asciidoctor) {
		LOGGER.debug("Producing QuestionConverter instance.");
		final QuestionConverter instance = QuestionConverter.instance(asciidoctor);
		return instance;
	}

	public void close(@Disposes Asciidoctor asciidoctor) {
		LOGGER.debug("Disposing of asciidoctor factory.");
		asciidoctor.close();
	}
}
