package com.eureka.auth.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.eureka.auth.model.AuthenticationResponse;

@RestController
@ControllerAdvice
public class ExceptionHandlerController {

	@ExceptionHandler(UsernameNotFoundException.class)
//	@ResponseStatus(HttpStatus.SC_UNAUTHORIZED)
	@ResponseBody
	AuthenticationResponse UnauthorizeExceptionInfo(HttpServletRequest req, Exception ex) {

		AuthenticationResponse auth1 = new AuthenticationResponse();
		auth1.setStatus(401L);
		auth1.setMessage("invalid username or password");

		return auth1;
	}
}
