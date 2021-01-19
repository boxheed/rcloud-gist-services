/*******************************************************************************
* Copyright (c) 2017 AT&T Intellectual Property, [http://www.att.com]
*
* SPDX-License-Identifier:   MIT
*
*******************************************************************************/
package com.mangosolutions.rcloud.rawgist;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementServerProperties;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.util.ReflectionUtils;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(99)
public class ManagementSecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private ManagementServerProperties managementProperties;

	@Autowired
	private SecurityProperties securityProperties;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.antMatcher("/" + managementProperties.getServlet().getContextPath() + "/**")
		.csrf()
		.disable()
		.authorizeRequests()
		.anyRequest()
		.hasRole("ADMIN")
		.and().httpBasic();
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.apply(new DefaultInMemoryUserDetailsManagerConfigurer(
				this.securityProperties));
	}

	private static class DefaultInMemoryUserDetailsManagerConfigurer
			extends InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder> {

		private final SecurityProperties securityProperties;

		private static final Logger logger = LoggerFactory.getLogger(DefaultInMemoryUserDetailsManagerConfigurer.class);

		DefaultInMemoryUserDetailsManagerConfigurer(SecurityProperties securityProperties) {
			this.securityProperties = securityProperties;
		}

		@Override
		public void configure(AuthenticationManagerBuilder auth) throws Exception {
			User user = this.securityProperties.getUser();
			Set<String> roles = new LinkedHashSet<String>(user.getRoles());
			withUser(user.getName()).password(user.getPassword()).roles(roles.toArray(new String[roles.size()]));
			setField(auth, "defaultUserDetailsService", getUserDetailsService());
			super.configure(auth);
		}

		private void setField(Object target, String name, Object value) {
			try {
				Field field = ReflectionUtils.findField(target.getClass(), name);
				ReflectionUtils.makeAccessible(field);
				ReflectionUtils.setField(field, target, value);
			} catch (Exception ex) {
				logger.info("Could not set " + name);
			}
		}

	}
}
