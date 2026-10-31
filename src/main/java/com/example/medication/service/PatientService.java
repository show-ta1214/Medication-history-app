package com.example.medication.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.medication.entity.Patient;
import com.example.medication.form.PatientForm;
import com.example.medication.repository.PatientRepository;
import com.example.medication.exception.PatientNotFoundException;

@Service
@Transactional(readOnly = true)
public class PatientService {
    private final PatientRepository repository;
    public PatientService(PatientRepository repository) { this.repository = repository; }
    @Transactional
    public Patient register(PatientForm form) {
        return repository.save(new Patient(form.getName().strip(), form.getBirthDate()));
    }
    public List<Patient> search(String name) {
        return repository.findByNameContainingIgnoreCaseOrderByIdAsc(name.strip());
    }
    public Patient find(Long id) {
        return repository.findById(id).orElseThrow(PatientNotFoundException::new);
    }
}
