/*
 * semanticcms-core-theme-base - Base SemanticCMS theme to simplify the implementation of other themes.
 * Copyright (C) 2021, 2022  AO Industries, Inc.
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
 * along with semanticcms-core-theme-base.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.semanticcms.core.theme.base.book;

import com.aoapps.net.URIParametersUtils;
import com.aoapps.servlet.http.HttpServletUtil;
import java.io.IOException;
import java.util.Objects;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/core/theme-base/apidocs/com/*")
public class ModularApidocsRedirect extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpServletUtil.sendRedirect(
			HttpServletResponse.SC_MOVED_PERMANENTLY, req, resp,
			"/core/theme-base/apidocs/com.semanticcms.core.theme.base/com" + Objects.toString(req.getPathInfo(), ""),
			URIParametersUtils.of(req.getQueryString()), true, false
		);
	}
}
