package com.ezybytes.springsecurity6.filter;

import java.io.IOException;
import java.util.logging.Logger;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

public class AuthoritiesLoggingAtFilter implements Filter {
	private final Logger LOG =
		Logger.getLogger(AuthoritiesLoggingAtFilter.class.getName());
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws
		IOException,
		ServletException {
		LOG.info("Authentication Validation is in progress");
		filterChain.doFilter(servletRequest, servletResponse);
	}
}
