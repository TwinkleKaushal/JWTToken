package com.security.advance.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtil {

	private String secret = "HereIsTheSecretKeyForThisPerticularExampleAndCanBeChangeAbleATAnyTime";

	// extracting username from token
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	//
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	// Get token body
	private Claims extractAllClaims(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}

	// checks token expire time
	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	// To generate Token for username
	public String generateToken(String username) {
		Map<String, Object> claims = new HashMap<>();

		// Called the createToken with perticular username and empty amp
		return createToken(claims, username);
	}

	// while creating the token -
	// 1. Define claims of the token, like Issuer, Expiration, Subject, and the ID
	// 2. Sign the JWT using the HS512 algorithm and secret key.
	// 3. According to JWS Compact
	// Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
	// compaction of the JWT to a URL-safe string

	private String createToken(Map<String, Object> claims, String subject) {

//Create the token using jwts while setting the empty map . subject==username,and by setting expire time and choosing the 
		// encryption algo
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
				.signWith(SignatureAlgorithm.HS256, secret).compact();
	}

	// validating token withe xpir time
	public Boolean validateWithToken(String token, UserDetails userDetails) {

		// Getting username from token
		final String username = extractUsername(token);

		// comparing token and expire time if true then validate token
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	// validate token without expiration validation
	public Boolean validateTokenWOExpirationValidation(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()));
	}
}