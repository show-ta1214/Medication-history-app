package com.example.medication.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.medication.form.MedicationHistoryForm;
import com.example.medication.service.PatientService;
import com.example.medication.service.MedicationHistoryService;

@Controller
@RequestMapping("/patients/{patientId}/medications")
public class MedicationHistoryController {
    private final PatientService patients;
    private final MedicationHistoryService histories;
    public MedicationHistoryController(PatientService patients, MedicationHistoryService histories) {
        this.patients = patients;
        this.histories = histories;
    }
    @GetMapping("/new")
    public String form(@PathVariable Long patientId, Model model) {
        model.addAttribute("patient", patients.find(patientId));
        model.addAttribute("medicationHistoryForm", new MedicationHistoryForm());
        return "medication-register";
    }
    @PostMapping
    public String register(@PathVariable Long patientId,
                           @Valid @ModelAttribute MedicationHistoryForm medicationHistoryForm,
                           BindingResult result, Model model, RedirectAttributes redirect) {
        model.addAttribute("patient", patients.find(patientId));
        if (result.hasErrors()) { return "medication-register"; }
        histories.register(patientId, medicationHistoryForm);
        redirect.addFlashAttribute("success", "薬歴を登録しました。");
        return "redirect:/patients/" + patientId + "/medications";
    }
    @GetMapping
    public String history(@PathVariable Long patientId, Model model) {
        model.addAttribute("patient", patients.find(patientId));
        model.addAttribute("histories", histories.findByPatient(patientId));
        return "medication-history";
    }
}
