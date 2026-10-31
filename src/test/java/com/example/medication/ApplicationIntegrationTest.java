package com.example.medication;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import com.example.medication.entity.Patient;
import com.example.medication.entity.MedicationHistory;
import com.example.medication.form.MedicationHistoryForm;
import com.example.medication.repository.PatientRepository;
import com.example.medication.repository.MedicationHistoryRepository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:integration;DB_CLOSE_DELAY=-1",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
@Transactional
class ApplicationIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired PatientRepository patients;
    @Autowired MedicationHistoryRepository histories;

    Patient patient(String name) {
        return patients.saveAndFlush(new Patient(name, LocalDate.of(1990, 1, 2)));
    }
    MedicationHistoryForm medication(String date, String medicine) {
        MedicationHistoryForm form = new MedicationHistoryForm();
        form.setPrescriptionDate(LocalDate.parse(date));
        form.setMedicineName(medicine);
        form.setUsage("1日3回 毎食後");
        form.setDosage("1回1錠");
        form.setDurationDays(7);
        form.setGuidance("飲み忘れ時は確認してください。");
        form.setNotes("架空のテスト記録");
        return form;
    }
    @Test void homeAndRegistration() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk()).andExpect(view().name("index"));
        mvc.perform(post("/patients").param("name", "架空 花子").param("birthDate", "1990-01-02"))
            .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/"))
            .andExpect(flash().attributeExists("success"));
        assertThat(patients.findByNameContainingIgnoreCaseOrderByIdAsc("架空")).hasSize(1);
    }
    @Test void invalidPatientIsNotSaved() throws Exception {
        long count = patients.count();
        mvc.perform(post("/patients").param("name", "  ").param("birthDate", ""))
            .andExpect(status().isOk()).andExpect(model().attributeHasFieldErrors("patientForm", "name", "birthDate"));
        mvc.perform(post("/patients").param("name", "架空").param("birthDate", LocalDate.now().plusDays(1).toString()))
            .andExpect(model().attributeHasFieldErrors("patientForm", "birthDate"));
        mvc.perform(post("/patients").param("name", "架空").param("birthDate", "invalid"))
            .andExpect(model().attributeHasFieldErrors("patientForm", "birthDate"));
        assertThat(patients.count()).isEqualTo(count);
    }
    @Test void partialSearchIncludesDuplicateNamesAndNoResults() throws Exception {
        patient("架空 同名"); patient("架空 同名"); patient("別の患者");
        mvc.perform(get("/patients/search").param("name", "架空"))
            .andExpect(status().isOk()).andExpect(model().attribute("patients", hasSize(2)));
        mvc.perform(get("/patients/search").param("name", "該当なし"))
            .andExpect(content().string(containsString("該当する患者が見つかりません")));
        mvc.perform(get("/patients/search")).andExpect(status().isOk());
    }
    @Test void registerMedicationForSelectedPatient() throws Exception {
        Patient p = patient("架空 花子");
        String url = "/patients/" + p.getId() + "/medications";
        mvc.perform(get(url + "/new")).andExpect(status().isOk());
        mvc.perform(post(url).param("prescriptionDate", "2026-01-02").param("medicineName", "テスト薬")
            .param("usage", "毎食後").param("dosage", "1錠").param("durationDays", "7")
            .param("guidance", "架空の服薬指導").param("notes", ""))
            .andExpect(redirectedUrl(url)).andExpect(flash().attributeExists("success"));
        assertThat(histories.findByPatientIdOrderByPrescriptionDateDescIdDesc(p.getId())).hasSize(1);
        mvc.perform(get(url)).andExpect(status().isOk()).andExpect(content().string(containsString("テスト薬")));
    }
    @Test void historyIsIsolatedSortedAndEscapesHtml() throws Exception {
        Patient first = patient("架空 花子"); Patient other = patient("架空 太郎");
        histories.save(new MedicationHistory(first, medication("2026-01-01", "古い薬")));
        histories.save(new MedicationHistory(first, medication("2026-02-01", "<script>alert(1)</script>")));
        histories.save(new MedicationHistory(other, medication("2026-03-01", "他の患者の薬")));
        mvc.perform(get("/patients/" + first.getId() + "/medications"))
            .andExpect(status().isOk()).andExpect(model().attribute("histories", hasSize(2)))
            .andExpect(content().string(containsString("&lt;script&gt;")))
            .andExpect(content().string(not(containsString("<script>alert(1)</script>"))))
            .andExpect(content().string(not(containsString("他の患者の薬"))));
        assertThat(histories.findByPatientIdOrderByPrescriptionDateDescIdDesc(first.getId()))
            .extracting(MedicationHistory::getPrescriptionDate)
            .containsExactly(LocalDate.of(2026, 2, 1), LocalDate.of(2026, 1, 1));
    }
    @Test void emptyHistoryAndInvalidIds() throws Exception {
        Patient p = patient("架空");
        mvc.perform(get("/patients/" + p.getId() + "/medications"))
            .andExpect(content().string(containsString("薬歴が登録されていません")));
        mvc.perform(get("/patients/999999/medications")).andExpect(status().isNotFound()).andExpect(view().name("error"));
        mvc.perform(get("/patients/999999/medications/new")).andExpect(status().isNotFound());
        mvc.perform(post("/patients/999999/medications")).andExpect(status().isNotFound());
        mvc.perform(get("/patients/abc/medications")).andExpect(status().isBadRequest());
    }
    @Test void invalidMedicationsAreRejected() throws Exception {
        String url = "/patients/" + patient("架空").getId() + "/medications";
        mvc.perform(post(url)).andExpect(model().attributeHasFieldErrors("medicationHistoryForm",
            "prescriptionDate", "medicineName", "usage", "dosage", "durationDays", "guidance"));
        for (String value : new String[]{"0", "-1", "1.5", "abc", "2147483648"}) {
            mvc.perform(post(url).param("durationDays", value))
                .andExpect(status().isOk()).andExpect(model().attributeHasFieldErrors("medicationHistoryForm", "durationDays"));
        }
        assertThat(histories.count()).isZero();
    }
    @Test void consoleIsDisabledByDefault() throws Exception {
        mvc.perform(get("/h2-console/")).andExpect(status().isNotFound());
    }
}
