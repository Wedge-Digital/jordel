package com.auth.tests;

import com.auth.jwt.JwtError;
import com.auth.jwt.JwtService;
import com.auth.services.DateService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class JwtServiceTests {

	@Autowired
	private JwtService jwtService;

	@Test
	void contextLoads() {
	}

	private String buildToken(Date issuedAt, Date expiresAt) {
		String username = "Roberto Baggio";
		return this.jwtService.generateToken(username, issuedAt, expiresAt);
	}

	private String buildExpiredToken() {
		Date issuedAt = DateService.dateTimeFromMysql("2023-10-01 11:12:00").getValue();
        return this.buildToken(issuedAt, issuedAt);
	}

	private String buildValidToken() {
		Date issuedAt = DateService.dateTimeFromMysql("2028-10-01 11:12:00").getValue();
        return this.buildToken(issuedAt, issuedAt);
	}

	@Test
	void TestJwtGeneration() {
		/*
		- a generated token shall be reproductible
		 */
		String token = this.buildExpiredToken();
        Assertions.assertThat(token).isNotBlank();
        String expectedToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJSb2JlcnRvIEJhZ2dpbyIsImlhdCI6MTY5NjE1MTUyMCwiZXhwIjoxNjk2MTUxNTIwfQ.tR-L5dz2YdgkXXPUwNBHo1KjXQ30Gzd3Wj9Ad97TfqU";
        Assertions.assertThat(token).isEqualTo(expectedToken);
	}

	@Test
	void TestExpiredTokenIsReallyExpired() {
		String token = this.buildExpiredToken();
		Assertions.assertThat(this.jwtService.validateJwtToken(token).isSuccess()).isFalse();
		Assertions.assertThat(this.jwtService.validateJwtToken(token).getError()).isEqualTo(JwtError.EXPIRED_TOKEN);
	}

	@Test
	void TestValidTokenIsReallyValid() {
		String token = this.buildValidToken();
		Assertions.assertThat(this.jwtService.validateJwtToken(token).isSuccess()).isTrue();
	}

	@Test
	void TestRandomStringIsNotValid() {
		String badToken = "badToken";
		Assertions.assertThat(this.jwtService.validateJwtToken(badToken).isSuccess()).isFalse();
		Assertions.assertThat(this.jwtService.validateJwtToken(badToken).getError()).isEqualTo(JwtError.INVALID_TOKEN);
	}

	@Test
	void TestComputeAccessExpirationDate() {
		/*
		- Expiration date shall be accurate
		- Expiration date shall be after issuedAt date
		- Expiration date shall be always equal to issuedAt + expiration delay
		 */
		Date issuedAt = DateService.dateTimeFromMysql("2023-10-01 11:12:00").getValue();
		Date expectedDate = DateService.dateTimeFromMysql("2023-10-01 11:27:00").getValue();
		Date expirationDate = this.jwtService.computeAccessExpirationDate(issuedAt);
		assertEquals(expirationDate, expectedDate);
		assertTrue(expirationDate.after(issuedAt));
		assertEquals(expirationDate.getTime() - issuedAt.getTime(), jwtService.getAccessExpiration());
	}

	@Test
	void TestComputeRefreshExpirationDate() {
		// refersh token shall be valid one month
		Date issuedAt = DateService.dateTimeFromMysql("2023-10-01 11:12:00").getValue();
		Date expectedDate = DateService.dateTimeFromMysql("2023-10-31 10:12:00").getValue();
		Date expirationDate = this.jwtService.computeRefreshExpirationDate(issuedAt);
		assertEquals(expirationDate, expectedDate);
		assertTrue(expirationDate.after(issuedAt));
		assertEquals(expirationDate.getTime() - issuedAt.getTime(), jwtService.getRefreshExpiration());
	}

	void checkBothTokenContainsTheSamePayload(String firstToken, String secondToken) {
		// check token are not blanks
		Assertions.assertThat(firstToken).isNotBlank();
		Assertions.assertThat(secondToken).isNotBlank();

		// check usernames are equal
		String fromerUsername = this.jwtService.getUsernameFromToken(firstToken);
		String refreshedTokenUsername = this.jwtService.getUsernameFromToken(secondToken);
		Assertions.assertThat(fromerUsername).isEqualTo(refreshedTokenUsername);
	}

	@Test
	void TestRefreshTokenSucceed() {
		String validToken = this.buildValidToken();
		Assertions.assertThat(this.jwtService.validateJwtToken(validToken).isSuccess()).isTrue();

		//refresh token and check is still valid
		String refreshedToken = this.jwtService.renewAccessToken(validToken);
		Assertions.assertThat(this.jwtService.validateJwtToken(refreshedToken).isSuccess()).isTrue();

		checkBothTokenContainsTheSamePayload(validToken, refreshedToken);
	}

}
