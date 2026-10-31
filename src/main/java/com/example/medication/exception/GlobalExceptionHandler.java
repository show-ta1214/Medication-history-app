package com.example.medication.exception;

import jakarta.persistence.PersistenceException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;

@ControllerAdvice(annotations = Controller.class)
public class GlobalExceptionHandler {
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
    }
    @ExceptionHandler(PatientNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String notFound(Model model) {
        model.addAttribute("message", "患者が見つかりません。患者検索から選び直してください。");
        return "error";
    }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String badRequest(Model model) {
        model.addAttribute("message", "指定されたURLまたは入力値が正しくありません。");
        return "error";
    }
    @ExceptionHandler({DataAccessException.class, TransactionException.class, PersistenceException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String databaseError(Model model) {
        // 例外には入力値やSQLが含まれることがあるため画面やログへ出力しません。
        model.addAttribute("message", "データベース処理に失敗しました。保存状況を確認してから再度お試しください。");
        return "error";
    }
}
