package com.ezybytes.springsecurity6.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
			savedCustomer = customerRepository.save(customer);
			if (savedCustomer.getId() > 0) {
				response = ResponseEntity
					.status(HttpStatus.CREATED)
					.body("Customer with id " + savedCustomer.getId() + " registered successfully");
			}
		} catch (Exception ex) {
			response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
		return response;
	}
}
