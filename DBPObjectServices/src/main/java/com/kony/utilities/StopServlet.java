package com.kony.utilities;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.konylabs.middleware.servlet.IntegrationCustomServlet;

@IntegrationCustomServlet(urlPatterns = { "dummy" })
public class StopServlet extends HttpServlet {

	private static final Logger logger = LogManager.getLogger(StopServlet.class);

	@Override
	public void init() throws ServletException {
		/*
		 * Workaround. Loading the following referenced custom classes in this method to
		 * avoid java.lang.NoClassDefFoundError on custom classes when destroy() is
		 * called on app un-publish.
		 */
		logger.debug("Invoked init from AlertEngine");
		loadCustomClasses();
	}

	@Override
	public void destroy() {
		logger.debug("Invoked destroy from AlertEngine");
		HikariConfiguration.closeDatasource();
	}

	public void loadCustomClasses() {
		// Load Custom Classes
	}
}
