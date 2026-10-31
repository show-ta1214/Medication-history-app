package com.example.medication.form;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

public class MedicationHistoryForm {
    @NotNull(message = "処方日を入力してください")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate prescriptionDate;
    @NotBlank(message = "薬剤名を入力してください")
    @Size(max = 200, message = "薬剤名は200文字以内で入力してください")
    private String medicineName;
    @jakarta.validation.Valid
    private List<AdditionalMedicineForm> additionalMedicines = new ArrayList<>();
    @NotBlank(message = "用法を入力してください")
    @Size(max = 200, message = "用法は200文字以内で入力してください")
    private String usage;
    @NotBlank(message = "用量を入力してください")
    @Size(max = 100, message = "用量は100文字以内で入力してください")
    private String dosage;
    @NotNull(message = "服用日数を入力してください")
    @Min(value = 1, message = "服用日数は1以上の整数で入力してください")
    private Integer durationDays;
    @NotBlank(message = "服薬指導内容を入力してください")
    @Size(max = 5000, message = "服薬指導内容は5000文字以内で入力してください")
    private String guidance;
    @Size(max = 5000, message = "備考は5000文字以内で入力してください")
    private String notes;

    public LocalDate getPrescriptionDate() { return prescriptionDate; }
    public void setPrescriptionDate(LocalDate prescriptionDate) { this.prescriptionDate = prescriptionDate; }
    public String getMedicineName() { return medicineName; }
    public List<AdditionalMedicineForm> getAdditionalMedicines() { return additionalMedicines; }
    public void setAdditionalMedicines(List<AdditionalMedicineForm> additionalMedicines) { this.additionalMedicines = additionalMedicines; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }
    public String getUsage() { return usage; }
    public void setUsage(String usage) { this.usage = usage; }
    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    public Integer getDurationDays() { return durationDays; }
    public void setDurationDays(Integer durationDays) { this.durationDays = durationDays; }
    public String getGuidance() { return guidance; }
    public void setGuidance(String guidance) { this.guidance = guidance; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
