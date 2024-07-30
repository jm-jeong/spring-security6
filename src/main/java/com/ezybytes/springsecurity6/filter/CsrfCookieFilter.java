package com.ezybytes.springsecurity6.filter;

import java.io.IOException;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
/*
* 필터 통해서 request에서 토큰 확인하고 response header에 token 정보 저장*/
public class CsrfCookieFilter extends OncePerRequestFilter {
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
		if (csrfToken.getHeaderName() != null) {
			response.setHeader(csrfToken.getHeaderName(), csrfToken.getToken());
		}
		filterChain.doFilter(request, response);
	}
}
