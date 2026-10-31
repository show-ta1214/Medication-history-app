package com.example.medication;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.example.medication.service.PatientService;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:failure", "spring.jpa.hibernate.ddl-auto=create-drop"})
@AutoConfigureMockMvc
class DatabaseErrorTest {
    @Autowired MockMvc mvc;
    @MockitoBean PatientService patients;
    @Test void databaseFailureDoesNotExposeDetails() throws Exception {
        when(patients.register(any())).thenThrow(new DataAccessResourceFailureException("SECRET SQL AND PATIENT DATA"));
        mvc.perform(post("/patients").param("name", "架空").param("birthDate", "1990-01-01"))
            .andExpect(status().isInternalServerError()).andExpect(view().name("error"))
            .andExpect(content().string(containsString("データベース処理に失敗しました")))
            .andExpect(content().string(not(containsString("SECRET"))));
    }
}
