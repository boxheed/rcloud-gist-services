/*******************************************************************************
* Copyright (c) 2017 AT&T Intellectual Property, [http://www.att.com]
*
* SPDX-License-Identifier:   MIT
*
*******************************************************************************/
package com.mangosolutions.rcloud.rawgist.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.CoreMatchers.is;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.mangosolutions.rcloud.rawgist.Application;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@WebAppConfiguration
@ActiveProfiles({"test", "default"})
public class UserRestControllerTest {

	public static MediaType GITHUB_BETA_MEDIA_TYPE = MediaType.parseMediaType("application/vnd.github.beta+json");
	public static MediaType GITHUB_V3_MEDIA_TYPE = MediaType.parseMediaType("application/vnd.github.v3+json");

	private MockMvc mvc;

	@Autowired
    private WebApplicationContext webApplicationContext;

	@Before
    public void setup() throws Exception {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

	@Test
	@WithMockUser("mock_user")
    public void testGetUserWithApplicationJsonMediaType() throws Exception {
		ResultActions resultActions = mvc.perform(get("/user")
	            .accept(MediaType.APPLICATION_JSON_UTF8))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.login", is("mock_user")));
    }

	@Test
	@WithMockUser("mock_user")
    public void testGetUserWithGithubBetaMediaType() throws Exception {
		ResultActions resultActions = mvc.perform(get("/user")
	            .accept(GITHUB_BETA_MEDIA_TYPE))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.login", is("mock_user")));
    }

	@Test
	@WithMockUser("mock_user")
    public void testGetUserWithGithubV3MediaType() throws Exception {
		ResultActions resultActions = mvc.perform(get("/user")
	            .accept(GITHUB_V3_MEDIA_TYPE))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.login", is("mock_user")));
    }
}
