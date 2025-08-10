package com.sahibinden.codecase.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sahibinden.codecase.dto.NewClassifiedDto;
import com.sahibinden.codecase.entity.Classified;
import com.sahibinden.codecase.entity.Status;
import com.sahibinden.codecase.repository.ClassifiedRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static com.sahibinden.codecase.test_helper.ResponseHeaderExtractor.extractId;
import static com.sahibinden.codecase.test_helper.ResponseHeaderExtractor.extractLocation;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ClassifiedControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ClassifiedRepository classifiedRepository;

    @Test
    void createClassified_returns201_and_persists_entity() throws Exception {

        NewClassifiedDto dto = NewClassifiedDto.builder()
                .title("Sunny 2+1 flat near metro")
                .detail("Bright, renovated 2+1 apartment close to metro and shops.")
                .category("SHOPPING")
                .build();

        String body = objectMapper.writeValueAsString(dto);

        MvcResult result = mockMvc
                .perform(
                        post("/api/v1/classifieds")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location",
                        Matchers.matchesPattern(".*/api/v1/classifieds/\\d+$")))
                .andReturn();

        // Extract the created ID from Location header and assert DB state
        String location = extractLocation(result);
        assertNotNull(location);

        long id = Long.parseLong(location.replaceAll(".*/", ""));

        Classified saved = classifiedRepository.findById(id).orElseThrow();

        assertEquals(dto.getTitle(), saved.getTitle());
        assertEquals(dto.getDetail(), saved.getDetail());
        assertEquals(dto.getCategory(), saved.getCategory().name());
        // For SHOPPING, default status should become ACTIVE
        assertEquals(Status.ACTIVE, saved.getStatus());
    }

    @Test
    void posting_same_content_twice_marks_second_as_DUPLICATE() throws Exception {

        NewClassifiedDto duplicateDto = NewClassifiedDto.builder()
                .title("Cozy studio in city center")
                .detail("Furnished, walk to metro and markets. Ideal for singles.")
                .category("SHOPPING")
                .build();

        String body = objectMapper.writeValueAsString(duplicateDto);

        long id1 = extractId(mockMvc
                .perform(
                        post("/api/v1/classifieds")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                .andExpect(status().isCreated())
                .andReturn());

        long id2 = extractId(mockMvc
                .perform(
                        post("/api/v1/classifieds")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                .andExpect(status().isCreated())
                .andReturn());


        Classified first = classifiedRepository.findById(id1).orElseThrow();
        Classified second = classifiedRepository.findById(id2).orElseThrow();

        assertEquals(Status.ACTIVE, first.getStatus(), "First should be ACTIVE");
        assertEquals(Status.DUPLICATE, second.getStatus(), "Second identical ad should be DUPLICATE");
    }

    @Test
    void validation_failure_returns400() throws Exception {
        String body = objectMapper.writeValueAsString(NewClassifiedDto.builder()
                .title("shrt-ttle")
                .detail("Bright, renovated 2+1 apartment close to metro and shops.")
                .category("SHOPPING")
                .build());

        mockMvc.perform(
                        post("/api/v1/classifieds")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                .andExpect(status().isBadRequest());
    }

}
