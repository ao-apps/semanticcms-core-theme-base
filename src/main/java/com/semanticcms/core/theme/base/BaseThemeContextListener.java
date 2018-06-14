/*
 * semanticcms-core-theme-base - Base SemanticCMS theme to simplify the implementation of other themes.
 * Copyright (C) 2016, 2017, 2018  AO Industries, Inc.
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
import static com.aoindustries.servlet.firewall.pathspace.Rules.*;
import static com.aoindustries.servlet.firewall.rules.Rules.*;
import com.semanticcms.core.renderer.html.HtmlRenderer;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener("Registers the \"" + BaseTheme.THEME_NAME + "\" theme in HtmlRenderer and SemanticCMS.")
public class BaseThemeContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext servletContext = event.getServletContext();
		HtmlRenderer.getInstance(servletContext).addTheme(new BaseTheme());
		// TODO: Move to /META-INF/semanticcms-servlet-space.xml?
		// TODO: Allow semanticcms-servlet-space.xml anywhere in the directory structure?
		FirewallPathSpace.getFirewallPathSpace(servletContext).add(
			FirewallComponent.newInstance(
				Prefix.valueOf(BaseTheme.PREFIX + Prefix.WILDCARD_SUFFIX),
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
				Prefix.valueOf(BaseTheme.PREFIX + Path.SEPARATOR_CHAR + "styles" + Prefix.WILDCARD_SUFFIX),
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
