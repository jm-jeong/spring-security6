package com.ezybytes.springsecurity6.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.ezybytes.springsecurity6.filter.AuthoritiesLoggingAfterFilter;
import com.ezybytes.springsecurity6.filter.AuthoritiesLoggingAtFilter;
import com.ezybytes.springsecurity6.filter.CsrfCookieFilter;
import com.ezybytes.springsecurity6.filter.JWTTokenGeneratorFilter;
import com.ezybytes.springsecurity6.filter.JWTTokenValidatorFilter;
import com.ezybytes.springsecurity6.filter.RequestValidationBeforeFilter;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Configuration
public class ProjectSecurityConfig {

	@Bean
	SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

		CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
		requestHandler.setCsrfRequestAttributeName("_csrf");//원래 이름인데 명시적으로 보여주기 위해서 썼음

		http.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			// .csrf((csrf) -> csrf.ignoringRequestMatchers("/contact", "/register"))//authorizeHttpRequests보다 앞에 선언되어야 작동함. csrf 제외해야 하는 데이터를 보내는 메서드인 post put 등의 request
			.csrf((csrf) -> csrf.csrfTokenRequestHandler(requestHandler)
				.ignoringRequestMatchers("/register")//_csrf 토큰 심기 위해서 선언
				.csrfTokenRepository(
					CookieCsrfTokenRepository.withHttpOnlyFalse()))//쿠키 방식으로 토큰 저장, withHttpOnlyFalse()는 JavaScript에서도 사용하기 위해서
			.addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)//BasicAuthenticationFilter 이후에 CsrfCookieFilter 실행, 즉 로그인 완료후 CSRF 토큰 생성해서 응답 값에 채움
			.addFilterBefore(new RequestValidationBeforeFilter(), BasicAuthenticationFilter.class)//커스텀 필터 적용
			.addFilterAt(new AuthoritiesLoggingAtFilter(), BasicAuthenticationFilter.class)//addFilterAt은 지정한 내부 필터와 같이 실행인데 순서가 보장이 안됨 사용 주의
			.addFilterAfter(new AuthoritiesLoggingAfterFilter(), BasicAuthenticationFilter.class)
			.addFilterAfter(new JWTTokenGeneratorFilter(), BasicAuthenticationFilter.class)//user 검증 후에 JWT Token 생성
			.addFilterBefore(new JWTTokenValidatorFilter(), BasicAuthenticationFilter.class)//user 검증 전에 JWT Token 유효성 검사
			.authorizeHttpRequests((authorize) -> authorize
				// .requestMatchers(("/myAccount")).hasAuthority("VIEWACCOUNT")
				// .requestMatchers(("/myLoans")).hasAuthority("VIEWLOANS")
				// .requestMatchers(("/myCards")).hasAuthority("VIEWCARDS")
				// .requestMatchers(("/myBalance")).hasAnyAuthority("VIEWACCOUNT", "VIEWBALANCE")
				.requestMatchers(("/myAccount")).hasRole("USER")
				.requestMatchers(("/myLoans")).authenticated()// @PreAuthorize 사용위해 hasRole() 삭제
				.requestMatchers(("/myCards")).hasRole("USER")
				.requestMatchers(("/myBalance")).hasAnyRole("USER", "ADMIN")
				.requestMatchers("/user").authenticated()
				.requestMatchers("/notices", "/register", "/v3/**", "/swagger-ui/**").permitAll())
			.formLogin(Customizer.withDefaults())
			.httpBasic(Customizer.withDefaults());

		return http.build();
	}

	/*
	pre-flight: 서버로 바로 요청을 보내는 Simple Request와는 다르게, 지금 보내는 요청이 유효한지를 확인하기 위해 OPTIONS 메서드로 예비 요청을 보내는 것
	최초 요청 보낼 시 Options 메서드가 허용된 상태에서(아래 조건은 모든 메서드 허용) 예비 요청으로 type이 preflight로 가서 cors 상태를 확인함.
	preflight 응답 헤더에서 백엔드에 선언된 cors 정책을 확인할 수 있음.
	* */
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
		config.setAllowedMethods(Collections.singletonList("*"));
		config.setAllowedHeaders(Collections.singletonList("*"));
		config.setMaxAge(3600L);
		config.setExposedHeaders(Collections.singletonList("Authorization"));//JWT Token을 보내기 위해 받을 앱에 헤더 정보를 알려주는 것(bearer 사용시에는 선언 안했는데 이유 모르겠음)

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);

		return source;
	}

	/*	*//**
	 * NoOpPasswordEncoder is not recommended for production usage.
	 * Use only for non-prod.
	 * 해싱안하고 텍스트로 비번 저장
	 * @return PasswordEncoder
	 *//*
	@Bean
	public PasswordEncoder passwordEncoder() {
		return NoOpPasswordEncoder.getInstance();
	}*/

	//임시로 사용자 만드는 방법 1은 PasswordEncoder 안에 설정되어 있고, 2는 위에 처럼 @Bean 선언해야함.
    /*@Bean
    public InMemoryUserDetailsManager userDetailsService() {
        *//*Approach 1 where we use withDefaultPasswordEncoder() method
		while creating the user details*//*
	 *//*UserDetails admin = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("12345")
                .authorities("admin")
                .build();
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("12345")
                .authorities("read")
                .build();
        return new InMemoryUserDetailsManager(admin, user);*//*

	 *//*Approach 2 where we use NoOpPasswordEncoder Bean
		while creating the user details*//*
        UserDetails admin = User.withUsername("admin")
                .password("12345")
                .authorities("admin")
                .build();
        UserDetails user = User.withUsername("user")
                .password("12345")
                .authorities("read")
                .build();
        return new InMemoryUserDetailsManager(admin, user);

    }*/

	/*
	//Jdbc로 자동으로 User 테이블 생성해서 사용자 db에 생성/업데이트/삭제 가능함 간단한 프로젝트에서 사용.
	@Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }*/
}
