package com.example.medication.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.medication.entity.MedicationHistory;
import com.example.medication.form.MedicationHistoryForm;
import com.example.medication.repository.MedicationHistoryRepository;

@Service
@Transactional(readOnly = true)
public class MedicationHistoryService {
    private final MedicationHistoryRepository repository;
    private final PatientService patients;
    public MedicationHistoryService(MedicationHistoryRepository repository, PatientService patients) {
        this.repository = repository;
        this.patients = patients;
    }
    @Transactional
    public void register(Long patientId, MedicationHistoryForm form) {
        repository.save(new MedicationHistory(patients.find(patientId), form));
    }
    public List<MedicationHistory> findByPatient(Long patientId) {
        return repository.findByPatientIdOrderByPrescriptionDateDescIdDesc(patientId);
    }
}
