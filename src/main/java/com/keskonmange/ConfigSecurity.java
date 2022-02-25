package com.keskonmange;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.keskonmange.security.jwt.AuthEntryPointJwt;
import com.keskonmange.security.jwt.AuthTokenFilter;
import com.keskonmange.security.services.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ConfigSecurity extends WebSecurityConfigurerAdapter {

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

//TODO: Définir quelles sont les url auxquelles peut accéder l'USER une fois connecté.
		/**
		 * (HttpMethod.GET,"/api/utilisateurs/all").hasAnyAuthority("ADMIN","USER") -> autorise à avoir la liste de tous les utilisateur en GET
		 * (HttpMethod.POST,"/api/groupes/**").hasAuthority("USER") --> autorise à ajouter un nouveau groupe
		 */

		http.cors().and().csrf().disable()
				.exceptionHandling()
				.authenticationEntryPoint(unauthorizedHandler).and()
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
				.authorizeRequests()
				.antMatchers("/api/utilisateurs/signin").permitAll()
				.antMatchers("/api/utilisateurs/signup").permitAll()
				.antMatchers("/api/utilisateurs/login").permitAll()
				.antMatchers("/api/utilisateurs/logout").permitAll()
				.antMatchers("/api/utilisateurs/connected").permitAll()
//				.antMatchers("/api/personnes/**").permitAll()
				.antMatchers("/api/personnes/**").hasAuthority("USER")
				.antMatchers(HttpMethod.GET,"/api/utilisateurs/all").hasAuthority("USER")
				.antMatchers(HttpMethod.DELETE,"/api/utilisateurs/**").hasAuthority("USER")
				.antMatchers("/api/groupes/**").hasAuthority("USER")
				.anyRequest().authenticated();
		http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
		
	
//		http.csrf().disable().authorizeRequests().anyRequest().permitAll();
		
//    http.csrf().disable()
//		.formLogin().loginProcessingUrl("/login").and()
//		.logout().logoutUrl("/logout").invalidateHttpSession(true)
//		.and().authorizeRequests()
//		.antMatchers("/login").permitAll()
//		.antMatchers("/logout").permitAll()
//		.anyRequest().authenticated()
//		.and().httpBasic();	// pour autoriser les API REST avec leur propres sécurités
	}
}
