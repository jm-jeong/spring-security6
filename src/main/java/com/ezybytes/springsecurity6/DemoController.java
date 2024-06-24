package com.ezybytes.springsecurity6;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

	@GetMapping("/welcome")
	public String welcome() {
		return " to Spring Security";
	}
}
