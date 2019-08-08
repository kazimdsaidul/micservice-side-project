package com.eureka.auth.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.eureka.auth.model.AppUser;
import com.eureka.auth.security.repository.AppUserRepository;

@Service // It has to be annotated with @Service.
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private BCryptPasswordEncoder encoder;

	@Autowired
	AppUserRepository mAppUserRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		Iterable<AppUser> findAll = mAppUserRepository.findAll();

		for (AppUser appUser : findAll) {
			System.err.println(appUser.toString());

			if (appUser.getUsername().equals(username)) {

				// System.err.println("user found");

				// Remember that Spring needs roles to be in this format: "ROLE_" + userRole
				// (i.e. "ROLE_ADMIN")
				// So, we need to set it to that format, so we can verify and compare roles
				// (i.e. hasRole("ADMIN")).
				List<GrantedAuthority> grantedAuthorities = AuthorityUtils
						.commaSeparatedStringToAuthorityList("ROLE_" + appUser.getRole());

				// The "User" class is provided by Spring and represents a model class for user
				// to be returned by UserDetailsService
				// And used by auth manager to verify and check user authentication.

				return new User(appUser.getUsername(), encoder.encode(appUser.getPassword()), grantedAuthorities);
			}

		}
		// System.err.println("user not found found");

		throw new UsernameNotFoundException("Username: " + username + " not found");

	}

}