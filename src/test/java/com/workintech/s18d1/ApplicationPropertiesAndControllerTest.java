package com.workintech.s18d1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workintech.s18d1.controller.BurgerController;
import com.workintech.s18d1.dao.BurgerDao;
import com.workintech.s18d1.entity.BreadType;
import com.workintech.s18d1.entity.Burger;
import com.workintech.s18d1.exceptions.BurgerException;
import com.workintech.s18d1.exceptions.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest(controllers = {BurgerController.class, GlobalExceptionHandler.class,ApplicationPropertiesAndControllerTest.class})
@ExtendWith(ResultAnalyzer.class)
class ApplicationPropertiesAndControllerTest {

    @Autowired
    private Environment env;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BurgerDao burgerDao;

    private Burger sampleBurger;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        sampleBurger = new Burger();
        sampleBurger.setId(1L);
        sampleBurger.setName("Classic Burger");
        sampleBurger.setPrice(7.99);
        sampleBurger.setIsVegan(false);
        sampleBurger.setBreadType(BreadType.BURGER);
        sampleBurger.setContents("Beef, Lettuce, Tomato, Cheese");
    }


    @Test
    @DisplayName("application properties istenilenler eklendi mi?")
    void serverPortIsSetTo8585() {

        String serverPort = env.getProperty("server.port");
        assertThat(serverPort).isEqualTo("9000");



        String datasourceUrl = env.getProperty("spring.datasource.url");
        assertNotNull(datasourceUrl);

        String datasourceUsername = env.getProperty("spring.datasource.username");
        assertNotNull(datasourceUsername);

        String datasourcePassword = env.getProperty("spring.datasource.password");
        assertNotNull(datasourcePassword);

        String hibernateDdlAuto = env.getProperty("spring.jpa.hibernate.ddl-auto");
        assertNotNull(hibernateDdlAuto);

        String hibernateSql = env.getProperty("logging.level.org.hibernate.SQL");
        assertNotNull(hibernateSql);

        String hibernateJdbcBind = env.getProperty("logging.level.org.hibernate.jdbc.bind");
        assertNotNull(hibernateJdbcBind);

    }

    @Test
    @DisplayName("Burger not found exception test")
    void testBurgerNotFoundException() throws Exception {
        given(burgerDao.findById(anyLong())).willThrow(new BurgerException("Burger not found", HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/burger/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Burger not found"));
    }

    @Test
    @DisplayName("Generic exception test")
    void testGenericException() throws Exception {
        given(burgerDao.findById(anyLong())).willThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/burger/{id}", 2L))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Unexpected error"));
    }

    @Test
    @DisplayName("Save burger test")
    void testSaveBurger() throws Exception {
        given(burgerDao.save(any())).willReturn(sampleBurger);

        mockMvc.perform(post("/burger")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleBurger)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(sampleBurger.getName())));
    }

    @Test
    @DisplayName("Find all burgers test")
    void testFindAllBurgers() throws Exception {
        List<Burger> burgers = Arrays.asList(sampleBurger);
        given(burgerDao.findAll()).willReturn(burgers);

        mockMvc.perform(get("/burger"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(sampleBurger.getName())));
    }

    @Test
    @DisplayName("Find burger by id test")
    void testGetBurgerById() throws Exception {
        given(burgerDao.findById(sampleBurger.getId())).willReturn(sampleBurger);

        mockMvc.perform(get("/burger/{id}", sampleBurger.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(sampleBurger.getName())));
    }

    @Test
    @DisplayName("Update burger test")
    void testUpdateBurger() throws Exception {
        Burger updatedBurger = new Burger();
        updatedBurger.setId(1L);
        updatedBurger.setName("Updated Classic Burger");
        given(burgerDao.update(any())).willReturn(updatedBurger);

        mockMvc.perform(put("/burger")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBurger)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(updatedBurger.getName())));
    }

    @Test
    @DisplayName("Remove burger test")
    void testRemoveBurger() throws Exception {

        given(burgerDao.remove(sampleBurger.getId())).willReturn(sampleBurger);

        mockMvc.perform(delete("/burger/{id}", sampleBurger.getId()))
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("Find by bread type test")
    void testFindByBreadType() throws Exception {
        List<Burger> burgers = Arrays.asList(sampleBurger);
        given(burgerDao.findByBreadType(sampleBurger.getBreadType())).willReturn(burgers);

        mockMvc.perform(get("/burger/breadType/{breadType}", sampleBurger.getBreadType()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(sampleBurger.getName())));
    }

    @Test
    @DisplayName("Find by price test")
    void testFindByPrice() throws Exception {
        List<Burger> burgers = Arrays.asList(sampleBurger);
        given(burgerDao.findByPrice((double) sampleBurger.getPrice().intValue())).willReturn(burgers);

        mockMvc.perform(get("/burger/price/{price}", sampleBurger.getPrice().intValue()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(sampleBurger.getName())));
    }

    @Test
    @DisplayName("Find by content test")
    void testFindByContent() throws Exception {
        List<Burger> burgers = Arrays.asList(sampleBurger);
        given(burgerDao.findByContent("Cheese")).willReturn(burgers);

        mockMvc.perform(get("/burger/content/{content}", "Cheese"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].contents", containsString("Cheese")));
    }
}


