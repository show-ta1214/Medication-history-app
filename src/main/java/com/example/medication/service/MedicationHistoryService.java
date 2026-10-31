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
        var patient = patients.find(patientId);
        repository.save(new MedicationHistory(patient, form));
        for (var medicine : form.getAdditionalMedicines()) {
            var history = new MedicationHistoryForm();
            history.setPrescriptionDate(form.getPrescriptionDate());
            history.setMedicineName(medicine.getMedicineName());
            history.setUsage(medicine.getUsage());
            history.setDosage(medicine.getDosage());
            history.setDurationDays(medicine.getDurationDays());
            history.setGuidance(form.getGuidance());
            history.setNotes(form.getNotes());
            repository.save(new MedicationHistory(patient, history));
        }
    }
    public List<MedicationHistory> findByPatient(Long patientId) {
        return repository.findByPatientIdOrderByPrescriptionDateDescIdDesc(patientId);
    }
}
