package com.sahibinden.codecase.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sahibinden.codecase.dto.NewClassifiedDto;
import com.sahibinden.codecase.dto.StatusUpdateDto;
import com.sahibinden.codecase.entity.Category;
import com.sahibinden.codecase.entity.Classified;
import com.sahibinden.codecase.entity.Status;
import com.sahibinden.codecase.repository.ClassifiedRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
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
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ClassifiedControllerIntegrationTest {

    private final String BASE_URL = "/api/v1/classifieds";
    private final String DUMMY_TITLE = "Dummy Classified Title";
    private final String DUMMY_DETAIL = "This is a dummy classified detail for testing purposes.";
    private final String DUMMY_CATEGORY = "SHOPPING";
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    ClassifiedRepository classifiedRepository;

    @BeforeEach
    void setUp() {
        classifiedRepository.deleteAll();
    }

    @Test
    void createClassified_returns201_and_persists_entity() throws Exception {

        NewClassifiedDto dto = NewClassifiedDto.builder()
                .title(DUMMY_TITLE)
                .detail(DUMMY_DETAIL)
                .category("SHOPPING")
                .build();

        String body = objectMapper.writeValueAsString(dto);

        MvcResult result = mockMvc
                .perform(
                        post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location",
                        Matchers.matchesPattern(".+" + BASE_URL + "/\\d+$")))
                .andReturn();

        // Extract the created ID from Location header and assert DB state
        String location = extractLocation(result);
        assertNotNull(location);

        long id = extractId(result);

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
                .title(DUMMY_TITLE)
                .detail(DUMMY_DETAIL)
                .category(DUMMY_CATEGORY)
                .build();

        String body = objectMapper.writeValueAsString(duplicateDto);

        long id1 = extractId(mockMvc
                .perform(
                        post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                .andExpect(status().isCreated())
                .andReturn());

        long id2 = extractId(mockMvc
                .perform(
                        post(BASE_URL)
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
                .title("short") // too short title
                .detail(DUMMY_DETAIL)
                .category(DUMMY_CATEGORY)
                .build());

        mockMvc.perform(
                        post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void change_status_to_valid_value_returns_ok() throws Exception {
        String body = objectMapper.writeValueAsString(new StatusUpdateDto(Status.ACTIVE));
        long classifiedId = classifiedRepository
                .save(Classified.builder()
                        .title(DUMMY_TITLE)
                        .detail(DUMMY_DETAIL)
                        .category(Category.valueOf(DUMMY_CATEGORY))
                        .status(Status.PENDING_APPROVAL)
                        .build())
                .getId();

        mockMvc.perform(
                        put(String.format("%s/%d/status", BASE_URL, classifiedId))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                .andExpect(status().isOk());

        assertEquals(Status.ACTIVE, classifiedRepository.findById(classifiedId)
                .orElseThrow().getStatus(), "Status should be updated to ACTIVE");
    }

    @Test
    void change_status_to_invalid_value_returns_bad_request() throws Exception {
        long classifiedId = classifiedRepository.save(
                Classified.builder()
                        .title(DUMMY_TITLE)
                        .detail(DUMMY_DETAIL)
                        .category(Category.valueOf(DUMMY_CATEGORY))
                        .status(Status.DUPLICATE) // assume DUPLICATE -> ACTIVE is illegal
                        .build()
        ).getId();

        String body = objectMapper.writeValueAsString(new StatusUpdateDto(Status.ACTIVE));

        mockMvc.perform(
                put(String.format("%s/%d/status", BASE_URL, classifiedId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
        ).andExpect(status().isBadRequest());

        assertEquals(Status.DUPLICATE,
                classifiedRepository.findById(classifiedId).orElseThrow().getStatus());
    }

    @Test
    void get_classified_status_history_returns_ok() throws Exception {
        // create
        var createBody = objectMapper.writeValueAsString(
                NewClassifiedDto.builder()
                        .title(DUMMY_TITLE)
                        .detail(DUMMY_DETAIL)
                        .category(Category.REAL_ESTATE.name())
                        .build());

        long classifiedId = extractId(
                mockMvc.perform(post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(createBody))
                        .andExpect(status().isCreated())
                        .andReturn());

        // transition 1: -> ACTIVE
        mockMvc.perform(put(String.format("%s/%d/status", BASE_URL, classifiedId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new StatusUpdateDto(Status.ACTIVE))))
                .andExpect(status().isOk());

        // transition 2: -> DEACTIVATED
        mockMvc.perform(put(String.format("%s/%d/status", BASE_URL, classifiedId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new StatusUpdateDto(Status.DEACTIVATED))))
                .andExpect(status().isOk());

        // fetch history
        mockMvc.perform(get(String.format("%s/%d/history", BASE_URL, classifiedId)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].status").value("PENDING_APPROVAL"))
                .andExpect(jsonPath("$[1].status").value("ACTIVE"))
                .andExpect(jsonPath("$[2].status").value("DEACTIVATED"))
                .andExpect(jsonPath("$[0].createdAt").exists())
                .andExpect(jsonPath("$[1].createdAt").exists())
                .andExpect(jsonPath("$[2].createdAt").exists());

    }
}
