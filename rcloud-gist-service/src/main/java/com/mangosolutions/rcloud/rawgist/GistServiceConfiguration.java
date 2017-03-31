/*******************************************************************************
* Copyright (c) 2017 AT&T Intellectual Property, [http://www.att.com]
*
* SPDX-License-Identifier:   MIT
*
*******************************************************************************/
package com.mangosolutions.rcloud.rawgist;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import com.mangosolutions.rcloud.rawgist.repository.GistIdGenerator;
import com.mangosolutions.rcloud.rawgist.repository.GistRepositoryService;
import com.mangosolutions.rcloud.rawgist.repository.GitGistRepositoryService;
import com.mangosolutions.rcloud.rawgist.repository.UUIDGistIdGenerator;

/**
 * Main Spring configuration
 *
 */
@Configuration()
@EnableConfigurationProperties(GistServiceProperties.class)
public class GistServiceConfiguration {

	@Autowired
	private GistServiceProperties serviceProperties;

	@Autowired
	private HazelcastInstance hazelcastInstance;

	@Autowired
	private ObjectMapper objectMapper;

	@Bean
	public GistRepositoryService getGistRepository() throws IOException {
		GitGistRepositoryService repo = new GitGistRepositoryService(serviceProperties.getRoot(),
				this.getGistIdGenerator(), hazelcastInstance, objectMapper);
		repo.setLockTimeout(serviceProperties.getLockTimeout());
		return repo;
	}

	@Bean
	public GistIdGenerator getGistIdGenerator() {
		return new UUIDGistIdGenerator();
	}

	@Bean
	public Config getHazelCastConfig() {
		Config config = new Config(serviceProperties.getCache());
		return config;
	}

	@Bean
    public CacheManager cacheManager() {
        return new HazelcastCacheManager(hazelcastInstance); // (3)
    }
	
}
