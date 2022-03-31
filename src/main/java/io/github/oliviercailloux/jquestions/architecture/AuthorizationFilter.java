package io.github.oliviercailloux.jquestions.architecture;

import java.io.IOException;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
@Priority(Priorities.AUTHORIZATION)
@HelloBinding
public class AuthorizationFilter implements ContainerRequestFilter {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationFilter.class);

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		LOGGER.info("Filtering");
	}

}
