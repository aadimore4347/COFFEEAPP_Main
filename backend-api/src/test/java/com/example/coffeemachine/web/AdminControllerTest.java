package com.example.coffeemachine.web;

import com.example.coffeemachine.domain.Facility;
import com.example.coffeemachine.service.FacilityService;
import com.example.coffeemachine.service.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@ActiveProfiles("test")
@WithMockUser(roles = "ADMIN")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacilityService facilityService;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateFacilityRequest createFacilityRequest;
    private Facility facility;
    private FacilityDto facilityDto;

    @BeforeEach
    void setUp() {
        createFacilityRequest = new CreateFacilityRequest();
        createFacilityRequest.setName("Test Facility");
        createFacilityRequest.setLocation("Test Location");

        facility = new Facility();
        facility.setId(1L);
        facility.setName("Test Facility");
        facility.setLocation("Test Location");
        facility.setActive(true);

        facilityDto = new FacilityDto();
        facilityDto.setId(1L);
        facilityDto.setName("Test Facility");
        facilityDto.setLocation("Test Location");
        facilityDto.setActive(true);
    }

    @Test
    void createFacility_ValidRequest_ReturnsCreated() throws Exception {
        when(facilityService.createFacility(any(Facility.class))).thenReturn(facility);

        mockMvc.perform(post("/api/admin/facility")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createFacilityRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Test Facility"));
    }

    @Test
    void getAllFacilities_ReturnsFacilitiesList() throws Exception {
        List<Facility> facilities = Arrays.asList(facility);
        when(facilityService.findAllActive()).thenReturn(facilities);

        mockMvc.perform(get("/api/admin/facilities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("Test Facility"));
    }

    @Test
    void getFacility_ValidId_ReturnsFacility() throws Exception {
        when(facilityService.findById(1L)).thenReturn(Optional.of(facility));

        mockMvc.perform(get("/api/admin/facility/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Test Facility"));
    }

    @Test
    void getFacility_InvalidId_ReturnsNotFound() throws Exception {
        when(facilityService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/admin/facility/999"))
                .andExpect(status().isNotFound());
    }
}