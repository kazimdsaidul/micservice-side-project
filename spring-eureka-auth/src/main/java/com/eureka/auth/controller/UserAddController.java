package com.eureka.auth.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserAddController {

	@RequestMapping(value = "/signup")
	public String singpuString() {
		return "test";
	}
}
