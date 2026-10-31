package com.example.medication.form;

import jakarta.validation.constraints.*;

public class AdditionalMedicineForm {
    @NotBlank(message = "薬剤名を入力してください")
    @Size(max = 200, message = "薬剤名は200文字以内で入力してください")
    private String medicineName;
    @NotBlank(message = "用法を入力してください")
    @Size(max = 200, message = "用法は200文字以内で入力してください")
    private String usage;
    @NotBlank(message = "用量を入力してください")
    @Size(max = 100, message = "用量は100文字以内で入力してください")
    private String dosage;
    @NotNull(message = "服用日数を入力してください")
    @Min(value = 1, message = "服用日数は1以上の整数で入力してください")
    private Integer durationDays;

    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }
    public String getUsage() { return usage; }
    public void setUsage(String usage) { this.usage = usage; }
    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    public Integer getDurationDays() { return durationDays; }
    public void setDurationDays(Integer durationDays) { this.durationDays = durationDays; }
}
