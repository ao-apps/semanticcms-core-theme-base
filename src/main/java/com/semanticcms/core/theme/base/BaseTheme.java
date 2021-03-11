/*
 * semanticcms-core-theme-base - Base SemanticCMS theme to simplify the implementation of other themes.
 * Copyright (C) 2016, 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of semanticcms-core-theme-base.
 *
 * semanticcms-core-theme-base is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * semanticcms-core-theme-base is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with semanticcms-core-theme-base.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.semanticcms.core.theme.base;

import com.aoindustries.net.Path;
import com.aoindustries.net.pathspace.Prefix;
import com.aoindustries.servlet.firewall.pathspace.FirewallComponent;
import com.aoindustries.servlet.firewall.pathspace.FirewallPathSpace;
import com.aoindustries.servlet.firewall.pathspace.Rules.pathMatch;
import static com.aoindustries.servlet.firewall.rules.Rules.and;
import com.aoindustries.servlet.firewall.rules.Rules.chain;
import com.aoindustries.servlet.firewall.rules.Rules.request;
import com.aoindustries.servlet.firewall.rules.Rules.response;
import com.aoindustries.servlet.http.Dispatcher;
import com.semanticcms.core.model.Page;
import com.semanticcms.core.renderer.html.HtmlRenderer;
import com.semanticcms.core.renderer.html.Theme;
import com.semanticcms.core.renderer.html.View;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.SkipPageException;

/**
 * An absolutely minimal base theme, meets the technical requirements but offers
 * nothing in the way of navigation or style.
 */
public class BaseTheme extends Theme {

	private static final String NAME = HtmlRenderer.DEFAULT_THEME_NAME;

	private static final String PREFIX = "/semanticcms-core-theme-base";

	private static final String JSPX_TARGET = PREFIX + "/theme.jspx";

	@WebListener("Registers the \"" + NAME + "\" theme in HtmlRenderer.")
	public static class Initializer implements ServletContextListener {

		@Override
		public void contextInitialized(ServletContextEvent event) {
			ServletContext servletContext = event.getServletContext();

			HtmlRenderer.getInstance(servletContext).addTheme(new BaseTheme());

			// TODO: Move to /META-INF/semanticcms-servlet-space.xml?
			// TODO: Allow semanticcms-servlet-space.xml anywhere in the directory structure?
			FirewallPathSpace.getInstance(servletContext).add(
				FirewallComponent.newInstance(
					Prefix.valueOf(PREFIX + Prefix.WILDCARD_SUFFIX),
					// Block direct access via request
					request.dispatcherType.isRequest(response.sendError.NOT_FOUND),
					// Only allow *.inc.jsp via include
					and(
						request.dispatcherType.isInclude,
						pathMatch.path.endsWith(".inc.jsp"),
						chain.doFilter
					),
					// *.jspx as forward only, but not including *.inc.jspx
					request.dispatcherType.isForward(
						pathMatch.path.endsWith(".jspx",
							pathMatch.path.endsWith(".inc.jspx", response.sendError.FORBIDDEN),
							chain.doFilter
						)
					),
					// TODO: Drop everything else, all other dispatchers?
					response.sendError.FORBIDDEN // TODO: Use message overload
				),
				// TODO: method, dispatcher, and stuff like in Documents/TODO/ao-servlet-firewall.xml
				// TODO: Support per servlet-space "policy", which will be the rules applied when no rules match
				// TODO: Support per servlet-space "pre" rules?
				// TODO: Support dynamic addition of rules to servlet-space?
				FirewallComponent.newInstance(
					Prefix.valueOf(PREFIX + Path.SEPARATOR_CHAR + "styles" + Prefix.WILDCARD_SUFFIX),
					request.dispatcherType.isRequest(
						// TODO: *.css matching is overkill here, but this is just testing programming style
						pathMatch.path.endsWith(".css",
							// TODO: To be most technically correct, should we return 404 before this 405 when the resource does not exist?  Make a rule to check if exists?
							// TODO: Would it be worth the overhead?
							request.method.constrain(request.method.GET),
							// TODO: restrict parameters for canonicalization? (this is overkill, but just testing how can use rules)
							chain.doFilter // TODO: Dispatch to LastModified servlet here instead of relying on applications to have registered it?
						),
						// 404 everything else on "REQUEST" dispatcher
						response.sendError.NOT_FOUND
					),
					// TODO: Drop everything else, all other dispatchers?
					response.sendError.FORBIDDEN // TODO: Use message overload
				)
			);
		}

		@Override
		public void contextDestroyed(ServletContextEvent event) {
			// Do nothing
		}
	}

	private BaseTheme() {}

	@Override
	public String getDisplay() {
		return "SemanticCMS Base";
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void doTheme(
		ServletContext servletContext,
		HttpServletRequest request,
		HttpServletResponse response,
		View view,
		Page page
	) throws ServletException, IOException, SkipPageException {
		Map<String, Object> args = new LinkedHashMap<>();
		args.put("view", view);
		args.put("page", page);
		Dispatcher.forward(
			servletContext,
			JSPX_TARGET,
			request,
			response,
			args
		);
	}
}
