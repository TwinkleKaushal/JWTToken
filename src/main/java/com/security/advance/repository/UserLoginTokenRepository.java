package com.security.advance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.security.advance.common.UserToken;
import com.security.advance.model.UserLoginToken;

@Repository
public interface UserLoginTokenRepository extends JpaRepository<UserLoginToken, Integer>  {

}
