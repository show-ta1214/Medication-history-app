package com.example.medication.form;

import java.time.LocalDate;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

/** 画面の入力値と検証ルールを保持します。 */
public class PatientForm {
    @NotBlank(message = "患者名を入力してください")
    @Size(max = 100, message = "患者名は100文字以内で入力してください")
    private String name;
    @NotNull(message = "生年月日を入力してください")
    @PastOrPresent(message = "生年月日に未来の日付は指定できません")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
}
