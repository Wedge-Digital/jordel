package com.bloodbowlclub.auth;

import com.bloodbowlclub.auth.io.services.JwtService;
import com.bloodbowlclub.lib.config.MessageSourceConfig;
import com.bloodbowlclub.lib.services.DateService;
import com.bloodbowlclub.lib.tests.TestCase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MessageSourceConfig.class)
class JwtServiceTests extends TestCase {

	private JwtService jwtService;

    @Autowired
    private MessageSource messageSource;

    private final DateService dateService = new DateService();

    private void initJwtService() {
        this.jwtService = new JwtService(
                "96eee8811a493fac99a85c1fa12095ba56ca533aebafd2f194aeb96ff5b1b1f775893edcf9671d8565d4c8d37d6dc8fd203dd05867778cfcb22b21c35c56e1f4f7939b7c7d886a8df9780665095d1052d4c7f1dd68332073ca61a08f5b69305c3acdf44229f408eac3798b3892683093617df9fdc1220db03f406c87b4c107215e37eba0a6db92faa4defb632f570c02c0001c519391acf1a73062a21ee7d3cd0d7e7b218b1df1c74253dcc265dc6640fc7bf948a3c84b38d4fc9c40398846264c75c4d315d20d6845872e1518c3e8f19b16fb592b2d14d152ad0831d2e5be8b0b7393255dcc44c45b047ff491ca91cbd4d5b1dc9f919904cd9bffe4f1b0d0aa",
                900000,
                2592000000L);
        this.jwtService.init();
    }

	private String buildToken(Date issuedAt, Date expiresAt) {
		String username = "Roberto Baggio";
        this.initJwtService();
		return this.jwtService.generateToken(username, issuedAt, expiresAt);
	}

	private String buildExpiredToken() {
		Date issuedAt = dateService.dateTimeFromMysql("2023-10-01 11:12:00").getValue();
        return this.buildToken(issuedAt, issuedAt);
	}

	private String buildValidToken() {
		Date issuedAt = dateService.dateTimeFromMysql("2028-10-01 11:12:00").getValue();
        return this.buildToken(issuedAt, issuedAt);
	}

	@Test
	void TestJwtGeneration() {
		/*
		- a generated token shall be reproductible
		 */
		String token = this.buildExpiredToken();
        Assertions.assertThat(token).isNotBlank();
        String expectedToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJSb2JlcnRvIEJhZ2dpbyIsImlhdCI6MTY5NjE1MTUyMCwiZXhwIjoxNjk2MTUxNTIwfQ.rWbdNR3hX_LXo3PiRgk--Y10Q8qgotvm3NegyP5iXj5F6qiZ9lCyyC8VHTQzDUf3N8OV_YY-v_N3er9_yDjoLA";
        Assertions.assertThat(token).isEqualTo(expectedToken);
	}

	@Test
	void TestExpiredTokenIsReallyExpired() {
		String token = this.buildExpiredToken();
		Assertions.assertThat(this.jwtService.validateJwtToken(token).isSuccess()).isFalse();
		Assertions.assertThat(this.jwtService.validateJwtToken(token).getTranslatedError(messageSource, Locale.getDefault())).isEqualTo("Token JWT expiré");
	}

	@Test
	void TestValidTokenIsReallyValid() {
		String token = this.buildValidToken();
		Assertions.assertThat(this.jwtService.validateJwtToken(token).isSuccess()).isTrue();
	}

	@Test
	void TestRandomStringIsNotValid() {
		String badToken = "badToken";
        this.initJwtService();
		Assertions.assertThat(this.jwtService.validateJwtToken(badToken).isSuccess()).isFalse();
		Assertions.assertThat(this.jwtService.validateJwtToken(badToken).getTranslatedError(messageSource, Locale.getDefault())).isEqualTo("Token JWT non valide");
	}

	@Test
	void TestComputeAccessExpirationDate() {
		/*
		- Expiration date shall be accurate
		- Expiration date shall be after issuedAt date
		- Expiration date shall be always equal to issuedAt + expiration delay
		 */
        this.initJwtService();
		Date issuedAt = dateService.dateTimeFromMysql("2023-10-01 11:12:00").getValue();
		Date expectedDate = dateService.dateTimeFromMysql("2023-10-01 11:27:00").getValue();
		Date expirationDate = this.jwtService.computeAccessExpirationDate(issuedAt);
		assertEquals(expectedDate, expirationDate);
		assertTrue(expirationDate.after(issuedAt));
		assertEquals(expirationDate.getTime() - issuedAt.getTime(), jwtService.getAccessExpiration());
	}

	@Test
	void TestComputeRefreshExpirationDate() {
		// refersh token shall be valid one month
        this.initJwtService();
		Date issuedAt = dateService.dateTimeFromMysql("2023-10-01 11:12:00").getValue();
		Date expectedDate = dateService.dateTimeFromMysql("2023-10-31 10:12:00").getValue();
		Date expirationDate = this.jwtService.computeRefreshExpirationDate(issuedAt);
        assertEquals(expectedDate, expirationDate);
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
        this.initJwtService();
		String validToken = this.buildValidToken();
		Assertions.assertThat(this.jwtService.validateJwtToken(validToken).isSuccess()).isTrue();

		//refresh token and check is still valid
		String refreshedToken = this.jwtService.renewAccessToken(validToken);
		Assertions.assertThat(this.jwtService.validateJwtToken(refreshedToken).isSuccess()).isTrue();

		checkBothTokenContainsTheSamePayload(validToken, refreshedToken);
	}

}
