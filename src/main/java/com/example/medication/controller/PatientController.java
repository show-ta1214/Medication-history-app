package com.example.medication.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.medication.form.PatientForm;
import com.example.medication.service.PatientService;

@Controller
public class PatientController {
    private final PatientService patients;
    public PatientController(PatientService patients) { this.patients = patients; }
    @PostMapping("/patients")
    public String register(@Valid @ModelAttribute PatientForm patientForm,
                           BindingResult result, RedirectAttributes redirect) {
        if (result.hasErrors()) { return "index"; }
        patients.register(patientForm);
        redirect.addFlashAttribute("success", "患者を登録しました。名前検索から薬歴を登録できます。");
        return "redirect:/";
    }
    @GetMapping("/patients/search")
    public String search(@RequestParam(defaultValue = "") String name,
                         @RequestParam(defaultValue = "search") String purpose, Model model) {
        model.addAttribute("name", name);
        model.addAttribute("purpose", purpose);
        boolean searched = !name.isBlank();
        model.addAttribute("searched", searched);
        if (searched) {
            if (name.length() > 100) {
                model.addAttribute("searchError", "患者名は100文字以内で検索してください");
                model.addAttribute("searched", false);
            } else { model.addAttribute("patients", patients.search(name)); }
        }
        return "patient-search";
    }
}
