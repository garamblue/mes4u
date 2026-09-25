package com.sindoh.sdmes.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.sindoh.sdmes.controller.AuthApiController;
import com.sindoh.sdmes.security.jwt.AuthEntryPointJwt;
import com.sindoh.sdmes.security.jwt.JwtUtils;
import com.sindoh.sdmes.security.service.UserDetailsImpl;
import com.sindoh.sdmes.security.service.UserDetailsServiceImpl;
import com.sindoh.sdmes.service.AuthService;

/**
 * The whole chain the web client relies on: a real JWT sent as 'Authorization: Bearer <token>'
 * is turned into the user's roles, which then decide access to the admin-only APIs.
 */
@WebMvcTest(controllers = AuthApiController.class)
@Import({ WebSecurityConfig.class, AuthEntryPointJwt.class, JwtUtils.class })
@TestPropertySource(properties = { "sdmes.app.jwtSecret=test-secret-for-unit-tests", "sdmes.app.jwtExpirationMs=60000" })
class JwtBearerAuthTest {

	@Autowired
	MockMvc mvc;
	@Autowired
	JwtUtils jwtUtils;

	@MockBean
	AuthService authService;
	@MockBean
	UserDetailsServiceImpl userDetailsService;

	private UserDetailsImpl user(String username, String role) {
		UserDetailsImpl user = mock(UserDetailsImpl.class);
		when(user.getUsername()).thenReturn(username);
		doReturn(List.<GrantedAuthority>of(new SimpleGrantedAuthority(role))).when(user).getAuthorities();
		return user;
	}

	/** issue a token like the sign-in does, and make the user loadable by name */
	private String tokenFor(String username, String role) {
		UserDetailsImpl user = user(username, role);
		when(userDetailsService.loadUserByUsername(username)).thenReturn(user);
		return jwtUtils.generateJwtToken(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
	}

	private int status(String header, String value) throws Exception {
		return mvc.perform(get("/api/auth/users").header(header, value)).andReturn().getResponse().getStatus();
	}

	@Test
	@DisplayName("Bearer token of ROLE_ADMIN -> allowed")
	void adminToken() throws Exception {
		assertThat(status("Authorization", "Bearer " + tokenFor("admin1", "ROLE_ADMIN"))).isNotIn(401, 403);
	}

	@Test
	@DisplayName("Bearer token of ROLE_BASIC -> 403")
	void basicToken() throws Exception {
		assertThat(status("Authorization", "Bearer " + tokenFor("basic1", "ROLE_BASIC"))).isEqualTo(403);
	}

	@Test
	@DisplayName("token in the old 'X-Token' header is not accepted -> 401")
	void oldHeaderIsIgnored() throws Exception {
		assertThat(status("X-Token", tokenFor("admin2", "ROLE_ADMIN"))).isEqualTo(401);
	}

	@Test
	@DisplayName("tampered / invalid token -> 401")
	void invalidToken() throws Exception {
		assertThat(status("Authorization", "Bearer " + tokenFor("admin3", "ROLE_ADMIN") + "x")).isEqualTo(401);
		assertThat(status("Authorization", "Bearer not-a-token")).isEqualTo(401);
	}
}
