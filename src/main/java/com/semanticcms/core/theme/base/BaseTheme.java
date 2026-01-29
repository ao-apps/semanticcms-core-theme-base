/*
 * semanticcms-core-theme-base - Base SemanticCMS theme to simplify the implementation of other themes.
 * Copyright (C) 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2025, 2026  AO Industries, Inc.
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

package com.semanticcms.core.theme.base;

import com.aoapps.servlet.http.Dispatcher;
import com.semanticcms.core.model.Page;
import com.semanticcms.core.servlet.SemanticCMS;
import com.semanticcms.core.servlet.Theme;
import com.semanticcms.core.servlet.View;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.SkipPageException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An absolutely minimal base theme, meets the technical requirements but offers
 * nothing in the way of navigation or style.
 */
public final class BaseTheme extends Theme {

  private static final String NAME = SemanticCMS.DEFAULT_THEME_NAME;

  private static final String PREFIX = "/semanticcms-core-theme-base";

  private static final String JSPX_TARGET = PREFIX + "/theme.inc.jspx";

  /**
   * Registers the "{@link BaseTheme#NAME}" theme in {@link SemanticCMS}.
   */
  @WebListener("Registers the \"" + NAME + "\" theme in SemanticCMS.")
  public static class Initializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
      SemanticCMS.getInstance(event.getServletContext()).addTheme(new BaseTheme());
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
      // Do nothing
    }
  }

  private BaseTheme() {
    // Do nothing
  }

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
