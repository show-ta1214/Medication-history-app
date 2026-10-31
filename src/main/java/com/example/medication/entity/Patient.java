package com.example.medication.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

/** 患者の永続化モデル。名前は一意ではありません。 */
@Entity
@Table(name = "patients")
public class Patient {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String name;
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;
    @OneToMany(mappedBy = "patient")
    private List<MedicationHistory> medicationHistories = new ArrayList<>();

    protected Patient() { }
    public Patient(String name, LocalDate birthDate) {
        this.name = name;
        this.birthDate = birthDate;
    }
    public Long getId() { return id; }
    public String getName() { return name; }
    public LocalDate getBirthDate() { return birthDate; }
    public List<MedicationHistory> getMedicationHistories() { return medicationHistories; }
}
