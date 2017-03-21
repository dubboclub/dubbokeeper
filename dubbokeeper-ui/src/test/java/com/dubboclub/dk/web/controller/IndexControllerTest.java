package com.dubboclub.dk.web.controller;


import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
 * @date: 2015/12/17.
 * @author:bieber.
 * @project:dubbokeeper.
 * @package:com.dubboclub.dk.web.controller.
 * @version:1.0.0
 * @fix:
 * @description: 描述功能
 */
@Ignore
public class IndexControllerTest extends SetUp{

    @Test
    public void testIndex() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/index.htm")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_XHTML_XML)).andReturn();
    }
}