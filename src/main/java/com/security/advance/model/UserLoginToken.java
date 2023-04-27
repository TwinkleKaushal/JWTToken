package com.security.advance.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class UserLoginToken {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Integer id;
	
	@Column(name="token",columnDefinition = "text", nullable = true)
	private String token;
	
	@Column(name="login_time",nullable = false)
	@CreationTimestamp
	private LocalDateTime loginTime;
	
	@Column(name="logout_time",nullable = true)
	private LocalDateTime logoutTime;

	private Integer status;
	
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;

}
