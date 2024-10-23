package com.farmio.api.repository;

import com.farmio.api.beans.Logins;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface LoginsRepository extends JpaRepository<Logins, Long> {

	UserDetails findByLogin(String login);
}
