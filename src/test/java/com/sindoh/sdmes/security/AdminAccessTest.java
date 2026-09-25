package com.sindoh.sdmes.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.sindoh.sdmes.controller.AuthApiController;
import com.sindoh.sdmes.controller.SystemApiController;
import com.sindoh.sdmes.security.jwt.AuthEntryPointJwt;
import com.sindoh.sdmes.security.jwt.JwtUtils;
import com.sindoh.sdmes.security.service.UserDetailsImpl;
import com.sindoh.sdmes.security.service.UserDetailsServiceImpl;
import com.sindoh.sdmes.service.AuthService;
import com.sindoh.sdmes.service.SystemService;

/**
 * The screens of the 'Admin' menu (External System, Printing Info, Log, Register User)
 * must only be reachable by ROLE_ADMIN. Other screens keep working for every user.
 */
@WebMvcTest(controllers = { AuthApiController.class, SystemApiController.class })
@Import({ WebSecurityConfig.class, AuthEntryPointJwt.class })
class AdminAccessTest {

	@Autowired
	MockMvc mvc;

	@MockBean
	AuthService authService;
	@MockBean
	SystemService systemService;
	@MockBean
	JwtUtils jwtUtils;
	@MockBean
	UserDetailsServiceImpl userDetailsService;

	private static RequestPostProcessor login(String role, Long userId) {
		UserDetailsImpl principal = mock(UserDetailsImpl.class);
		when(principal.getId()).thenReturn(userId);
		return authentication(new UsernamePasswordAuthenticationToken(principal, null,
				List.of(new SimpleGrantedAuthority(role))));
	}

	private static final RequestPostProcessor ADMIN = login("ROLE_ADMIN", 1L);
	private static final RequestPostProcessor BASIC = login("ROLE_BASIC", 2L);

	private static MockHttpServletRequestBuilder json(MockHttpServletRequestBuilder builder) {
		return builder.contentType(MediaType.APPLICATION_JSON).content("{}");
	}

	/** every request that must be restricted to ROLE_ADMIN */
	private static List<MockHttpServletRequestBuilder> adminOnly() {
		return List.of(
				json(post("/api/auth/register")),
				get("/api/auth/users"),
				get("/api/auth/roles"),
				json(post("/api/system/mesSystems")),
				json(put("/api/system/mesSystems")),
				delete("/api/system/mesSystems/1"),
				get("/api/system/mesPrintingPrograms"),
				json(post("/api/system/mesPrintingPrograms")),
				json(put("/api/system/mesPrintingPrograms")),
				delete("/api/system/mesPrintingPrograms/1"),
				get("/api/system/readLog/20260925"));
	}

	private int status(MockHttpServletRequestBuilder request, RequestPostProcessor user) throws Exception {
		return mvc.perform(user == null ? request : request.with(user)).andReturn().getResponse().getStatus();
	}

	@Test
	@DisplayName("admin-only APIs: not logged in -> 401")
	void anonymousIsUnauthorized() throws Exception {
		for (MockHttpServletRequestBuilder request : adminOnly()) {
			assertThat(status(request, null)).as(request.toString()).isEqualTo(401);
		}
	}

	@Test
	@DisplayName("admin-only APIs: ROLE_BASIC -> 403")
	void basicIsForbidden() throws Exception {
		for (MockHttpServletRequestBuilder request : adminOnly()) {
			assertThat(status(request, BASIC)).as(request.toString()).isEqualTo(403);
		}
	}

	@Test
	@DisplayName("admin-only APIs: ROLE_ADMIN is not blocked")
	void adminIsAllowed() throws Exception {
		for (MockHttpServletRequestBuilder request : adminOnly()) {
			assertThat(status(request, ADMIN)).as(request.toString()).isNotIn(401, 403);
		}
	}

	@Test
	@DisplayName("admin-only APIs cannot be reached with a trailing slash or suffix")
	void noBypassBySlashOrSuffix() throws Exception {
		assertThat(status(get("/api/auth/users/"), null)).isEqualTo(401);
		assertThat(status(get("/api/system/mesPrintingPrograms/"), BASIC)).isEqualTo(403);
		// suffix pattern matching is off: the controller is not mapped to this path at all
		assertThat(status(get("/api/auth/users.json"), null)).isEqualTo(404);
	}

	@Test
	@DisplayName("APIs used by the other screens stay open (also to ROLE_BASIC)")
	void sharedApisStayOpen() throws Exception {
		List<MockHttpServletRequestBuilder> shared = List.of(
				get("/api/system/mesSystems/EXECUTABLE"),
				get("/api/system/mesPrintingPrograms/group"),
				get("/api/system/mesPrintingPrograms/list/LABEL"),
				get("/api/system/mesPrintingPrograms/job/1"),
				json(post("/api/system/writeLog")));
		for (MockHttpServletRequestBuilder request : shared) {
			assertThat(status(request, BASIC)).as(request.toString()).isNotIn(401, 403);
		}
	}

	@Test
	@DisplayName("update user: administrator or the user himself only")
	void updateUser() throws Exception {
		String body = "{\"id\":2,\"username\":\"user\",\"email\":\"a@b.com\"}";
		String other = "{\"id\":3,\"username\":\"user\",\"email\":\"a@b.com\"}";
		assertThat(status(json(post("/api/auth/update")).content(body), null)).isEqualTo(401);
		assertThat(status(json(post("/api/auth/update")).content(other), BASIC)).isEqualTo(403);
		assertThat(status(json(post("/api/auth/update")).content(body), BASIC)).isNotIn(401, 403);
		assertThat(status(json(post("/api/auth/update")).content(other), ADMIN)).isNotIn(401, 403);
	}
}
