package com.ezybytes.springsecurity6.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.ezybytes.springsecurity6.model.Authority;
import com.ezybytes.springsecurity6.model.Customer;
import com.ezybytes.springsecurity6.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

/*
* AuthenticationProvider 사용하려면 UserDetails 삭제해야 함. 영향 있을 수 있음.
* 사용자 인증에 대해서 사용자가 인증 조건이 특정 나라 또는 19세 이상 등 조건을 넣어야 하는 경우 AuthenticationProvider를 구현해서 사용할 수 있음*/
@RequiredArgsConstructor
@Component
public class EazyBankUsernamePwdAuthenticationProvider implements AuthenticationProvider {
	private final CustomerRepository customerRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName();
		String password = authentication.getCredentials().toString();

		List<Customer> customer = customerRepository.findByEmail(username);
		if (customer.size() > 0) {
			if (passwordEncoder.matches(password, customer.get(0).getPwd())) {
				return new UsernamePasswordAuthenticationToken(username, password, getAuthorities(customer.get(0).getAuthorities()));
			} else {
				throw new BadCredentialsException("Invalid password");
			}
		} else {
			throw new BadCredentialsException("no user registered with this details");
		}
	}

	private List<GrantedAuthority> getAuthorities(Set<Authority> authorities) {
		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		for (Authority authority : authorities) {
			grantedAuthorities.add(new SimpleGrantedAuthority(authority.getName()));
		}
		return grantedAuthorities;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
	}
}
