package com.ezybytes.springsecurity6.controller;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ezybytes.springsecurity6.model.Customer;
import com.ezybytes.springsecurity6.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class LoginController {

	private final CustomerRepository customerRepository;

	private final PasswordEncoder passwordEncoder;

	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody Customer customer) {
		Customer savedCustomer = null;
		ResponseEntity<String> response = null;

		try {
			String hashPw = passwordEncoder.encode(customer.getPwd());
			customer.setPwd(hashPw);
			customer.setCreateDt(LocalDate.now());
			savedCustomer = customerRepository.save(customer);
			if (savedCustomer.getId() > 0) {
				response = ResponseEntity
					.status(HttpStatus.CREATED)
					.body("Customer with id " + savedCustomer.getId() + " registered successfully");
			}
		} catch (Exception ex) {
			response = ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body("An Exception occured due to" + ex.getMessage());
		}
		return response;
	}

	@RequestMapping("/user")
	public Customer getUserDetailsAfterLogin(Authentication authentication) {
		List<Customer> customers = customerRepository.findByEmail(authentication.getName());
		if (customers.size() > 0) {
			return customers.get(0);
		} else {
			return null;
		}
	}
}
