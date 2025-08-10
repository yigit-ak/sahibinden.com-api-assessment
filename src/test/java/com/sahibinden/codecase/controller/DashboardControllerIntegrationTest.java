package com.sahibinden.codecase.controller;

import com.sahibinden.codecase.entity.Category;
import com.sahibinden.codecase.entity.Classified;
import com.sahibinden.codecase.entity.Status;
import com.sahibinden.codecase.repository.ClassifiedRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DashboardControllerIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ClassifiedRepository classifiedRepository;

    private void addDummyClassifiedInDb(Status status) {
        classifiedRepository.save(Classified.builder()
                .title("Dummy Classified")
                .detail("This is a dummy classified for testing purposes.")
                .category(Category.SHOPPING)
                .status(status)
                .build());
    }

    @BeforeEach
    void setup() {
        classifiedRepository.deleteAll();

        addDummyClassifiedInDb(Status.ACTIVE);
        addDummyClassifiedInDb(Status.ACTIVE);
        addDummyClassifiedInDb(Status.DEACTIVATED);
        addDummyClassifiedInDb(Status.DUPLICATE);
        addDummyClassifiedInDb(Status.DUPLICATE);
        addDummyClassifiedInDb(Status.DUPLICATE);
        addDummyClassifiedInDb(Status.PENDING_APPROVAL);
    }

    @Test
    void getClassifiedsStatistics_returnsAggregatedCounts() throws Exception {
        mockMvc.perform(get("/dashboard/classifieds/statistics")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.active").value(2))
                .andExpect(jsonPath("$.deactivated").value(1))
                .andExpect(jsonPath("$.duplicate").value(3))
                .andExpect(jsonPath("$.pending").value(1));
    }

    @Test
    void getClassifiedsStatistics_returnsZerosWhenEmpty() throws Exception {
        classifiedRepository.deleteAll();

        mockMvc.perform(get("/dashboard/classifieds/statistics")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(0))
                .andExpect(jsonPath("$.deactivated").value(0))
                .andExpect(jsonPath("$.duplicate").value(0))
                .andExpect(jsonPath("$.pending").value(0));
    }
}
