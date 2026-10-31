package com.example.medication.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.medication.entity.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    // Spring Data がパラメータをバインドするためSQLを文字列連結しません。
    List<Patient> findByNameContainingIgnoreCaseOrderByIdAsc(String name);
}
