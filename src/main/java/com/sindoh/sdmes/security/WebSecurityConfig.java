package com.sindoh.sdmes.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.sindoh.sdmes.security.jwt.AuthEntryPointJwt;
import com.sindoh.sdmes.security.jwt.AuthTokenFilter;
import com.sindoh.sdmes.security.service.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	UserDetailsServiceImpl userDetailsService;

	@Autowired
	private AuthEntryPointJwt unauthorizedHandler;

	@Bean
	public AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter();
	}

	@Override
	public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable()
			.exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
			.authorizeRequests()
			// Screens of the 'Admin' menu (External System, Printing Info, Log, Register User): ROLE_ADMIN only
			.mvcMatchers("/api/auth/register", "/api/auth/users", "/api/auth/roles").hasRole("ADMIN")
			.mvcMatchers(HttpMethod.POST, "/api/system/mesSystems", "/api/system/mesPrintingPrograms").hasRole("ADMIN")
			.mvcMatchers(HttpMethod.PUT, "/api/system/mesSystems", "/api/system/mesPrintingPrograms").hasRole("ADMIN")
			.mvcMatchers(HttpMethod.DELETE, "/api/system/mesSystems/**", "/api/system/mesPrintingPrograms/**").hasRole("ADMIN")
			.mvcMatchers(HttpMethod.GET, "/api/system/mesPrintingPrograms", "/api/system/readLog/**").hasRole("ADMIN")
			// modifying a user requires a login (the controller allows an administrator or the user himself)
			.mvcMatchers("/api/auth/update").authenticated()
			.antMatchers("/api/**").permitAll()
			.antMatchers("/","/error","/js/**","/css/**","/fonts/**","/img/**").permitAll()
			.antMatchers("/favicon.ico").permitAll()
			.anyRequest().authenticated();

		http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**", "/webjars/**", "/swagger/**");
	}
	
}