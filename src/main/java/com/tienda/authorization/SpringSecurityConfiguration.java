package com.tienda.authorization;

import java.util.Arrays;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.tienda.authorization.filter.JwtAuthenticationFilter;
import com.tienda.authorization.filter.JwtValidationFilter;

@Configuration
public class SpringSecurityConfiguration {

	private AuthenticationConfiguration authenticationConfiguration;

	public SpringSecurityConfiguration(AuthenticationConfiguration authenticationConfiguration) {
		this.authenticationConfiguration = authenticationConfiguration;
	}

	@Bean
	AuthenticationManager authenticationManager() throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		return http
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(HttpMethod.GET, "/api/home/**", "/tienda/images/img/**",
								"/api/mantenimiento/productos/img/{foto:.+}")
						.permitAll().requestMatchers(HttpMethod.POST, "/api/home/**").permitAll()
						.requestMatchers(HttpMethod.DELETE, "/api/home/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/mantenimiento/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.POST, "/api/mantenimiento/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.PUT, "/api/mantenimiento/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/api/mantenimiento/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.GET, "/api/ordenes/**").hasRole("PICKER")
						.requestMatchers(HttpMethod.POST, "/api/usuario/**").hasRole("USER").anyRequest()
						.authenticated())
				.cors(cors -> cors.configurationSource(configurationSource()))
				.addFilter(new JwtAuthenticationFilter(authenticationManager()))
				.addFilter(new JwtValidationFilter(authenticationManager())).csrf(config -> config.disable())
				.sessionManagement(mng -> mng.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).build();
	}

	@Bean
	CorsConfigurationSource configurationSource() {

		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOriginPatterns(Arrays.asList("*"));
		config.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE"));
		config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
		config.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

	@Bean
	FilterRegistrationBean<CorsFilter> corsfilter() {
		FilterRegistrationBean<CorsFilter> corsBean = new FilterRegistrationBean<CorsFilter>(
				new CorsFilter(this.configurationSource()));
		corsBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return corsBean;
	}

}
