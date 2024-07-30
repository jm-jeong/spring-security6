package com.ezybytes.springsecurity6.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class ProjectSecurityConfig {

	@Bean
	SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
		http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.csrf((csrf) -> csrf.disable())//authorizeHttpRequests보다 앞에 선언되어야 작동함.
			.authorizeHttpRequests((authorize) -> authorize
			.requestMatchers("/myAccount","/myBalance","/myLoans","/myCards", "/user").authenticated()
			.requestMatchers( "/notices", "/contact", "/register", "/v3/**", "/swagger-ui/**").permitAll())
			.formLogin(Customizer.withDefaults())
			.httpBasic(Customizer.withDefaults());

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
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
