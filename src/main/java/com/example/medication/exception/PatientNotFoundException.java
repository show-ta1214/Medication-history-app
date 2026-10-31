package com.example.medication.exception;

public class PatientNotFoundException extends RuntimeException {
    public PatientNotFoundException() { super("患者が見つかりません"); }
}
