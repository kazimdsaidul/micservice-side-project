package com.eureka.auth.security;

import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.eureka.auth.model.AuthenticationResponse;
import com.eureka.auth.model.Token;
import com.eureka.common.security.JwtConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	// We use auth manager to validate the user credentials
	private AuthenticationManager authManager;

	private final JwtConfig jwtConfig;

	public JwtUsernameAndPasswordAuthenticationFilter(AuthenticationManager authManager, JwtConfig jwtConfig) {
		this.authManager = authManager;
		this.jwtConfig = jwtConfig;

		// By default, UsernamePasswordAuthenticationFilter listens to "/login" path.
		// In our case, we use "/auth". So, we need to override the defaults.
		this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(jwtConfig.getUri(), "POST"));
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {

		try {

			// 1. Get credentials from request
			UserCredentials creds = new ObjectMapper().readValue(request.getInputStream(), UserCredentials.class);

			// 2. Create auth object (contains credentials) which will be used by auth
			// manager
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(creds.getUsername(),
					creds.getPassword(), Collections.emptyList());

			// 3. Authentication manager authenticate the user, and use
			// UserDetialsServiceImpl::loadUserByUsername() method to load the user.
			return authManager.authenticate(authToken);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	// Upon successful authentication, generate a token.
	// The 'auth' passed to successfulAuthentication() is the current authenticated
	// user.
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication auth) throws IOException, ServletException {

		Long now = System.currentTimeMillis();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		String ackTimestamp = dateFormat.format(cal.getTime());

		String token = Jwts.builder().setSubject(auth.getName())
				// Convert to list of strings.
				// This is important because it affects the way we get them back in the Gateway.
				.claim("authorities",
						auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.setIssuedAt(new Date(now)).setExpiration(new Date(now + jwtConfig.getExpiration() * 1000)) // in
																											// milliseconds
				.signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret().getBytes()).compact();

		// retrun respose message

		AuthenticationResponse auth1 = new AuthenticationResponse();
		auth1.setStatus(200L);
		auth1.setMessage("Successful");

		Token token2 = new Token();
		token2.setAckTimestamp(ackTimestamp);
		token2.setSecurityToken(token);
		token2.setTokenExpTime("" + jwtConfig.getExpiration() * 1000);
		token2.setTokenType("normal");

		auth1.setToken(token2);

		String json = new Gson().toJson(auth1);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);

	}

	// A (temporary) class just to represent the user credentials
	private static class UserCredentials {
		private String username, password;

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}
}