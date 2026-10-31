package com.example.medication.entity;

import java.time.LocalDate;
import jakarta.persistence.*;
import com.example.medication.form.MedicationHistoryForm;

@Entity
@Table(name = "medication_histories", indexes = @Index(name = "idx_history_patient_date", columnList = "patient_id,prescription_date"))
public class MedicationHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false, foreignKey = @ForeignKey(name = "fk_history_patient"))
    private Patient patient;
    @Column(name = "prescription_date", nullable = false)
    private LocalDate prescriptionDate;
    @Column(name = "medicine_name", nullable = false, length = 200)
    private String medicineName;
    @Column(name = "\"usage\"", nullable = false, length = 200)
    private String usage;
    @Column(nullable = false, length = 100)
    private String dosage;
    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String guidance;
    @Column(columnDefinition = "TEXT")
    private String notes;

    protected MedicationHistory() { }
    public MedicationHistory(Patient patient, MedicationHistoryForm form) {
        this(patient, form, form.getMedicineName());
    }
    public MedicationHistory(Patient patient, MedicationHistoryForm form, String medicineName) {
        this.patient = patient;
        this.prescriptionDate = form.getPrescriptionDate();
        this.medicineName = medicineName;
        this.usage = form.getUsage();
        this.dosage = form.getDosage();
        this.durationDays = form.getDurationDays();
        this.guidance = form.getGuidance();
        this.notes = form.getNotes();
    }
    public Long getId() { return id; }
    public Patient getPatient() { return patient; }
    public LocalDate getPrescriptionDate() { return prescriptionDate; }
    public String getMedicineName() { return medicineName; }
    public String getUsage() { return usage; }
    public String getDosage() { return dosage; }
    public Integer getDurationDays() { return durationDays; }
    public String getGuidance() { return guidance; }
    public String getNotes() { return notes; }
}
