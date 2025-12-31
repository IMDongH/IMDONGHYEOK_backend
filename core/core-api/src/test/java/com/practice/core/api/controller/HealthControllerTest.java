package com.practice.core.api.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.practice.core.support.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HealthControllerTest extends RestDocsSupport {

    @Override
    protected Object initController() {
        return new HealthController();
    }

    @Test
    @DisplayName("Health Check API")
    void healthCheck() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andDo(document("health-check",
                        resource(ResourceSnippetParameters.builder()
                                .description("Health Check API")
                                .summary("Health Check")
                                .tag("System")
                                .build())));
    }
}
