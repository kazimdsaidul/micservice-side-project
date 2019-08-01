package com.eureka.auth.security.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.eureka.auth.model.AppUser;

public interface AppUserRepository extends CrudRepository<AppUser, Long> {

	List<AppUser> findByPassword(String password);

}
