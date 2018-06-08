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
import com.semanticcms.core.controller.SemanticCMS;
import com.semanticcms.core.controller.ServletSpace;
import com.semanticcms.core.renderer.html.HtmlRenderer;
import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebListener("Registers the \"" + BaseTheme.THEME_NAME + "\" theme in HtmlRenderer and SemanticCMS.")
public class BaseThemeContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext servletContext = event.getServletContext();
		HtmlRenderer.getInstance(servletContext).addTheme(new BaseTheme());
		// TODO: Move to /META-INF/semanticcms-servlet-space.xml?
		// TODO: Allow semanticcms-servlet-space.xml anywhere in the directory structure?
		SemanticCMS semanticCMS = SemanticCMS.getInstance(servletContext);
		semanticCMS.addServletSpace(
			new ServletSpace(
				Prefix.valueOf(BaseTheme.PREFIX + Prefix.WILDCARD_SUFFIX),
				ServletSpace.Action.NotFoundAction.getInstance()
			)
		);
		semanticCMS.addServletSpace(
			new ServletSpace(
				Prefix.valueOf(BaseTheme.PREFIX + "/styles" + Prefix.WILDCARD_SUFFIX),
				// TODO: *.css matcher overkill?
				new ServletSpace.Matcher() {
					@Override
					public ServletSpace.Action findAction(HttpServletRequest request, HttpServletResponse response, SemanticCMS semanticCMS, String servletPath, Prefix prefix, Path servletSpace, Path pathInSpace) throws IOException, ServletException {
						if(pathInSpace.toString().endsWith(".css")) {
							return ServletSpace.Action.PassThroughAction.getInstance();
						} else {
							return null;
						}
					}
				}
			)
		);
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		// Do nothing
	}
}
