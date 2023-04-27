package com.security.advance.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.security.advance.filter.JwtFilter;
import com.security.advance.service.impl.CustomUserDetailService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	JwtFilter filter;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.csrf().disable()
		
		//To send CSRF token and acces POST and PUT method
		//.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
		//.and()
		.authorizeRequests()
		.antMatchers("/user/token").permitAll()
		//.antMatchers("/users/new").hasRole("ADMIN")
		.anyRequest()
		.authenticated()
		.and()
		.exceptionHandling().and() 
		.sessionManagement()
		//Session creation policy(Stateless)
		.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.addFilterAfter(filter,UsernamePasswordAuthenticationFilter.class);
		
		
		//For form based authentication
		//.formLogin();
	}
	
	@Autowired
	CustomUserDetailService userDetailsService;
	
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		
		//To use the password as it
		return NoOpPasswordEncoder.getInstance();
		
		//To encode the password
		//return new BCryptPasswordEncoder(10);

	}
	
	@Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
