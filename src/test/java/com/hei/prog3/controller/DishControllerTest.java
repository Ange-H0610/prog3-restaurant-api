package com.hei.prog3.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hei.prog3.dto.DishCreateRequest;
import com.hei.prog3.entity.DishTypeEnum;
import com.hei.prog3.service.DishService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DishController.class)
class DishControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DishService dishService;

    @Test
    void testCreateDishes_Success() throws Exception {
        List<DishCreateRequest> requests = Arrays.asList(
                new DishCreateRequest("Pizza", DishTypeEnum.MAIN, 15000.00),
                new DishCreateRequest("Tiramisu", DishTypeEnum.DESSERT, 8000.00));

        mockMvc.perform(post("/dishes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requests)))
                .andExpect(status().isCreated());
    }

    @Test
    void testCreateDishes_EmptyBody() throws Exception {
        mockMvc.perform(post("/dishes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[]"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetDishes_WithFilters() throws Exception {
        mockMvc.perform(get("/dishes")
                .param("priceUnder", "10000.00")
                .param("priceOver", "5000.00")
                .param("name", "Pizza"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetDishes_NoFilters() throws Exception {
        mockMvc.perform(get("/dishes"))
                .andExpect(status().isOk());
    }
}