package io.github.oliviercailloux.jquestions.architecture;

import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
@PreMatching
public class PrincipalFilter implements ContainerRequestFilter {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(PrincipalFilter.class);

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		LOGGER.info("Filtering");
		requestContext.setSecurityContext(new MySecurityContext());
	}

}
