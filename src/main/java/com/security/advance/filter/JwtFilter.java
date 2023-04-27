package com.security.advance.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.advance.common.ErrorResponse;
import com.security.advance.common.UserToken;
import com.security.advance.model.User;
import com.security.advance.model.UserLoginToken;
import com.security.advance.repository.UserLoginTokenRepository;
import com.security.advance.repository.UserRepository;
import com.security.advance.service.impl.CustomUserDetailService;
import com.security.advance.util.JwtUtil;

import io.jsonwebtoken.ExpiredJwtException;

@Component
public class JwtFilter extends OncePerRequestFilter {

	@Autowired
	JwtUtil jwtUtils;

	@Autowired
	CustomUserDetailService userDetailsService;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	UserLoginTokenRepository userLoginTokenRepository;

	@Autowired
	UserRepository userRepo;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			filterChain.doFilter(request, response);
			return;
		}
		final String requestTokenHeader = request.getHeader("Authorization");

		// verify token
		if ((requestTokenHeader != null && requestTokenHeader.startsWith("Bearer "))) {
			verifyAuthenticationTokenAndFillUpContext(requestTokenHeader.substring(7), request, response, filterChain);
		} else {
			invalidRequestResponse(response, "No token found");
		}

	}

	private void invalidRequestResponse(HttpServletResponse response, String message) throws IOException {
		ErrorResponse responseBody = new ErrorResponse();

		responseBody.setMessage(message);

		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.getWriter().write(objectMapper.writeValueAsString(responseBody));

	}

	private void verifyAuthenticationTokenAndFillUpContext(String jwtToken, HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String username;

		// Extract Username from token
		try {
			username = jwtUtils.extractUsername(jwtToken);
		} catch (IllegalArgumentException e) {
			invalidRequestResponse(response, "Unable to parse token");
			return;
		} catch (ExpiredJwtException e) {
			invalidRequestResponse(response, "Token has been expired");
			return;
		}

		Map<String, Object> responseMap = isTokenActiveAndUserActive(username, jwtToken);

		boolean isTokenActiveAndUserActiveBool = Boolean
				.parseBoolean(responseMap.get("isTokenActiveAndUserActive").toString());

		Object principle = responseMap.get("principle");
		UserDetails userDetails = (UserDetails) responseMap.get("userDetails");

		if (isTokenActiveAndUserActiveBool) {
			UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
					principle, null, userDetails.getAuthorities());
			
			System.out.println(principle +"+++++++++++++++++++++++++++++++++++++++++++");
			usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

			// After setting the Authentication in the context, we specify
			// that the current user is authenticated. So it passes the
			// Spring Security Configurations successfully.
			SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

			System.out.println(usernamePasswordAuthenticationToken);

			filterChain.doFilter(request, response);
		}

	}

	private Map<String, Object> isTokenActiveAndUserActive(String username, String token) {
		UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

		User user = userRepo.findByUsername(username);

		Map<String, Object> responseMap = new HashMap<>();

		Optional<UserLoginToken> userLoginToken = userLoginTokenRepository.findById(user.getId());

		boolean isTokenActive = !Objects.isNull(userLoginToken);

		boolean isTokenValidForActiveUsers = jwtUtils.validateTokenWOExpirationValidation(token, userDetails);
//	            && existedContactDetails.getParty().getStatus().equals(activeInactiveStatus.ACTIVE);

		UserToken principleForUser = getPrinciple(user);

		responseMap.put("isTokenActiveAndUserActive", isTokenActive && isTokenValidForActiveUsers);
		responseMap.put("principle", principleForUser);
		responseMap.put("userDetails", userDetails);

		return responseMap;
	}

	private UserToken getPrinciple(User userD) {

		UserToken principal = new UserToken();
		principal.setId(userD.getId());
		principal.setEmail(userD.getEmail());
		return principal;
	}

}
