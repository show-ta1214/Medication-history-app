package com.example.medication.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.medication.entity.MedicationHistory;

public interface MedicationHistoryRepository extends JpaRepository<MedicationHistory, Long> {
    List<MedicationHistory> findByPatientIdOrderByPrescriptionDateDescIdDesc(Long patientId);
}
