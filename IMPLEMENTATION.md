# 実装コード（全ファイル）

設計・起動・H2 Console・テスト手順は [README.md](README.md) を参照してください。保存先は各見出しに記載しています。

## 1. pom.xml



```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <parent><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-parent</artifactId><version>3.5.16</version><relativePath/></parent>
  <groupId>com.example</groupId><artifactId>medication-history</artifactId><version>0.0.1-SNAPSHOT</version>
  <name>Medication History</name>
  <properties><java.version>21</java.version></properties>
  <dependencies>
    <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-web</artifactId></dependency>
    <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-thymeleaf</artifactId></dependency>
    <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-data-jpa</artifactId></dependency>
    <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-validation</artifactId></dependency>
    <dependency><groupId>com.h2database</groupId><artifactId>h2</artifactId><scope>runtime</scope></dependency>
    <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-test</artifactId><scope>test</scope></dependency>
  </dependencies>
  <build><plugins><plugin><groupId>org.springframework.boot</groupId><artifactId>spring-boot-maven-plugin</artifactId></plugin></plugins></build>
</project>

```

## 2. src/main/resources/application.properties



```properties
spring.application.name=medication-history
server.address=127.0.0.1
spring.datasource.url=jdbc:h2:file:./data/medication-history;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.username=sa
spring.datasource.password=${H2_PASSWORD:}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.open-in-view=false
spring.jpa.show-sql=false
spring.h2.console.enabled=false
server.error.include-message=never
server.error.include-stacktrace=never
server.error.include-binding-errors=never
spring.mvc.log-request-details=false
# SQL・バインド値には個人情報が含まれるため出力しません。
logging.level.org.hibernate.SQL=OFF
logging.level.org.hibernate.orm.jdbc.bind=OFF
logging.level.org.hibernate.engine.jdbc.spi.SqlExceptionHelper=OFF
logging.level.org.springframework.web=ERROR
server.servlet.session.tracking-modes=cookie

```

## 3. src/main/resources/application-local.properties



```properties
# 明示的な local プロファイルでのみ Console を有効化。
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.h2.console.settings.web-allow-others=false
spring.h2.console.settings.trace=false

```

## 4. src/main/java/com/example/medication/MedicationHistoryApplication.java



```java
package com.example.medication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MedicationHistoryApplication {
    public static void main(String[] args) {
        SpringApplication.run(MedicationHistoryApplication.class, args);
    }
}

```

## 5. src/main/java/com/example/medication/entity/Patient.java



```java
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

```

## 6. src/main/java/com/example/medication/entity/MedicationHistory.java



```java
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
        this.patient = patient;
        this.prescriptionDate = form.getPrescriptionDate();
        this.medicineName = form.getMedicineName();
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

```

## 7. src/main/java/com/example/medication/repository/PatientRepository.java



```java
package com.example.medication.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.medication.entity.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    // Spring Data がパラメータをバインドするためSQLを文字列連結しません。
    List<Patient> findByNameContainingIgnoreCaseOrderByIdAsc(String name);
}

```

## 8. src/main/java/com/example/medication/repository/MedicationHistoryRepository.java



```java
package com.example.medication.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.medication.entity.MedicationHistory;

public interface MedicationHistoryRepository extends JpaRepository<MedicationHistory, Long> {
    List<MedicationHistory> findByPatientIdOrderByPrescriptionDateDescIdDesc(Long patientId);
}

```

## 9. src/main/java/com/example/medication/service/PatientService.java



```java
package com.example.medication.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.medication.entity.Patient;
import com.example.medication.form.PatientForm;
import com.example.medication.repository.PatientRepository;
import com.example.medication.exception.PatientNotFoundException;

@Service
@Transactional(readOnly = true)
public class PatientService {
    private final PatientRepository repository;
    public PatientService(PatientRepository repository) { this.repository = repository; }
    @Transactional
    public Patient register(PatientForm form) {
        return repository.save(new Patient(form.getName().strip(), form.getBirthDate()));
    }
    public List<Patient> search(String name) {
        return repository.findByNameContainingIgnoreCaseOrderByIdAsc(name.strip());
    }
    public Patient find(Long id) {
        return repository.findById(id).orElseThrow(PatientNotFoundException::new);
    }
}

```

## 10. src/main/java/com/example/medication/service/MedicationHistoryService.java



```java
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
        repository.save(new MedicationHistory(patients.find(patientId), form));
    }
    public List<MedicationHistory> findByPatient(Long patientId) {
        return repository.findByPatientIdOrderByPrescriptionDateDescIdDesc(patientId);
    }
}

```

## 11. src/main/java/com/example/medication/form/PatientForm.java



```java
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

```

## 12. src/main/java/com/example/medication/form/MedicationHistoryForm.java



```java
package com.example.medication.form;

import java.time.LocalDate;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

public class MedicationHistoryForm {
    @NotNull(message = "処方日を入力してください")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate prescriptionDate;
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
    @NotBlank(message = "服薬指導内容を入力してください")
    @Size(max = 5000, message = "服薬指導内容は5000文字以内で入力してください")
    private String guidance;
    @Size(max = 5000, message = "備考は5000文字以内で入力してください")
    private String notes;

    public LocalDate getPrescriptionDate() { return prescriptionDate; }
    public void setPrescriptionDate(LocalDate prescriptionDate) { this.prescriptionDate = prescriptionDate; }
    public String getMedicineName() { return medicineName; }
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

```

## 13. src/main/java/com/example/medication/controller/HomeController.java



```java
package com.example.medication.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.medication.form.PatientForm;

@Controller
public class HomeController {
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("patientForm", new PatientForm());
        return "index";
    }
}

```

## 14. src/main/java/com/example/medication/controller/PatientController.java



```java
package com.example.medication.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.medication.form.PatientForm;
import com.example.medication.service.PatientService;

@Controller
public class PatientController {
    private final PatientService patients;
    public PatientController(PatientService patients) { this.patients = patients; }
    @PostMapping("/patients")
    public String register(@Valid @ModelAttribute PatientForm patientForm,
                           BindingResult result, RedirectAttributes redirect) {
        if (result.hasErrors()) { return "index"; }
        patients.register(patientForm);
        redirect.addFlashAttribute("success", "患者を登録しました。名前検索から薬歴を登録できます。");
        return "redirect:/";
    }
    @GetMapping("/patients/search")
    public String search(@RequestParam(defaultValue = "") String name,
                         @RequestParam(defaultValue = "search") String purpose, Model model) {
        model.addAttribute("name", name);
        model.addAttribute("purpose", purpose);
        boolean searched = !name.isBlank();
        model.addAttribute("searched", searched);
        if (searched) {
            if (name.length() > 100) {
                model.addAttribute("searchError", "患者名は100文字以内で検索してください");
                model.addAttribute("searched", false);
            } else { model.addAttribute("patients", patients.search(name)); }
        }
        return "patient-search";
    }
}

```

## 15. src/main/java/com/example/medication/controller/MedicationHistoryController.java



```java
package com.example.medication.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.medication.form.MedicationHistoryForm;
import com.example.medication.service.PatientService;
import com.example.medication.service.MedicationHistoryService;

@Controller
@RequestMapping("/patients/{patientId}/medications")
public class MedicationHistoryController {
    private final PatientService patients;
    private final MedicationHistoryService histories;
    public MedicationHistoryController(PatientService patients, MedicationHistoryService histories) {
        this.patients = patients;
        this.histories = histories;
    }
    @GetMapping("/new")
    public String form(@PathVariable Long patientId, Model model) {
        model.addAttribute("patient", patients.find(patientId));
        model.addAttribute("medicationHistoryForm", new MedicationHistoryForm());
        return "medication-register";
    }
    @PostMapping
    public String register(@PathVariable Long patientId,
                           @Valid @ModelAttribute MedicationHistoryForm medicationHistoryForm,
                           BindingResult result, Model model, RedirectAttributes redirect) {
        model.addAttribute("patient", patients.find(patientId));
        if (result.hasErrors()) { return "medication-register"; }
        histories.register(patientId, medicationHistoryForm);
        redirect.addFlashAttribute("success", "薬歴を登録しました。");
        return "redirect:/patients/" + patientId + "/medications";
    }
    @GetMapping
    public String history(@PathVariable Long patientId, Model model) {
        model.addAttribute("patient", patients.find(patientId));
        model.addAttribute("histories", histories.findByPatient(patientId));
        return "medication-history";
    }
}

```

## 16. src/main/java/com/example/medication/exception/PatientNotFoundException.java



```java
package com.example.medication.exception;

public class PatientNotFoundException extends RuntimeException {
    public PatientNotFoundException() { super("患者が見つかりません"); }
}

```

## 17. src/main/java/com/example/medication/exception/GlobalExceptionHandler.java



```java
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

```

## 18. src/main/resources/messages.properties



```properties
typeMismatch.java.time.LocalDate=正しい日付を入力してください
typeMismatch.java.lang.Integer=1以上の整数を入力してください

```

## 19. src/main/resources/templates/fragments.html



```html
<!DOCTYPE html>
<html lang="ja" xmlns:th="http://www.thymeleaf.org">
  <head th:fragment="head(title)">
    <meta charset="UTF-8">
    <link rel="icon" type="image/svg+xml" th:href="@{/favicon.svg}">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title th:text="${title} + ' | 薬歴管理システム'">
      薬歴管理システム
    </title>
    <link rel="stylesheet" th:href="@{/css/style.css}">
    <script defer th:src="@{/js/app.js}">
    </script>
  </head>
  <body>
    <header th:fragment="header">
      <div class="header-inner">
        <a class="brand" th:href="@{/}">
          <span class="brand-icon" aria-hidden="true">
            ＋
          </span>
          薬歴管理システム
        </a>
        <span class="environment">
          学習・開発用
        </span>
      </div>
    </header>
    <nav th:fragment="nav" aria-label="メインナビゲーション">
      <a th:href="@{/}">
        ホーム
      </a>
      <a th:href="@{/patients/search}">
        患者検索
      </a>
      <a th:href="@{/patients/search(purpose='register')}">
        薬歴登録
      </a>
      <a th:href="@{/patients/search(purpose='history')}">
        過去の薬歴確認
      </a>
    </nav>
    <div th:fragment="success" th:if="${success}" class="notice success" role="status" th:text="${success}">
    </div>
    <section th:fragment="patient" class="patient-summary" aria-label="選択した患者">
      <div>
        <span class="muted">
          患者名
        </span>
        <strong th:text="${patient.name}">
        </strong>
      </div>
      <div>
        <span class="muted">
          生年月日
        </span>
        <span th:text="${#temporals.format(patient.birthDate, 'yyyy年MM月dd日')}">
        </span>
      </div>
      <div>
        <span class="muted">
          患者ID
        </span>
        <span th:text="${patient.id}">
        </span>
      </div>
    </section>
    <footer th:fragment="footer">
      <p>
        架空のテストデータのみ使用してください。
      </p>
      <p>
        薬歴管理システム · 学習・開発用
      </p>
    </footer>
  </body>
</html>

```

## 20. src/main/resources/templates/index.html



```html
<!DOCTYPE html>
<html lang="ja" xmlns:th="http://www.thymeleaf.org">
  <head th:replace="~{fragments :: head('ホーム')}">
  </head>
  <body>
    <header th:replace="~{fragments :: header}">
    </header>
    <nav th:replace="~{fragments :: nav}">
    </nav>
    <main>
      <div class="page-heading">
        <h1>
          患者情報と薬歴を、ひとつに。
        </h1>
        <p>
          患者の登録から、日々の薬歴の記録・確認まで。
        </p>
      </div>
      <div th:replace="~{fragments :: success}">
      </div>
      <div class="home-layout">
        <section class="panel">
          <div class="section-title">
            <span class="step">
              01
            </span>
            <div>
              <h2>
                患者新規登録
              </h2>
              <p>
                患者の基本情報を登録します。
              </p>
            </div>
          </div>
          <form th:action="@{/patients}" th:object="${patientForm}" method="post">
            <div class="field">
              <label for="name">
                患者名
                <span class="required">
                  必須
                </span>
              </label>
              <input id="name" type="text" th:field="*{name}" maxlength="100" autocomplete="name" placeholder="例：山田 花子" required>
              <p class="field-error" th:if="${#fields.hasErrors('name')}" th:errors="*{name}">
              </p>
            </div>
            <div class="field">
              <label for="birthDate">
                生年月日
                <span class="required">
                  必須
                </span>
              </label>
              <input id="birthDate" type="date" th:field="*{birthDate}" data-birth-date required>
              <p class="field-error" th:if="${#fields.hasErrors('birthDate')}" th:errors="*{birthDate}">
              </p>
            </div>
            <button class="button full" type="submit">
              患者を登録する
            </button>
          </form>
        </section>
        <aside class="workflow">
          <h2>
            登録済みの患者をお探しですか？
          </h2>
          <p>
            患者を検索し、薬歴の登録や確認へ進めます。
          </p>
          <a class="workflow-link" th:href="@{/patients/search}">
            <span>
              <strong>
                患者を名前で検索
              </strong>
              <small>
                名前の一部から検索できます
              </small>
            </span>
            <span aria-hidden="true">
              →
            </span>
          </a>
          <a class="workflow-link" th:href="@{/patients/search(purpose='register')}">
            <span>
              <strong>
                薬歴を登録
              </strong>
              <small>
                患者を選んで新しい記録を追加
              </small>
            </span>
            <span aria-hidden="true">
              →
            </span>
          </a>
          <a class="workflow-link" th:href="@{/patients/search(purpose='history')}">
            <span>
              <strong>
                過去の薬歴を確認
              </strong>
              <small>
                患者ごとの記録を時系列で確認
              </small>
            </span>
            <span aria-hidden="true">
              →
            </span>
          </a>
        </aside>
      </div>
    </main>
    <footer th:replace="~{fragments :: footer}">
    </footer>
  </body>
</html>

```

## 21. src/main/resources/templates/patient-search.html



```html
<!DOCTYPE html>
<html lang="ja" xmlns:th="http://www.thymeleaf.org">
  <head th:replace="~{fragments :: head('患者検索')}">
  </head>
  <body>
    <header th:replace="~{fragments :: header}">
    </header>
    <nav th:replace="~{fragments :: nav}">
    </nav>
    <main>
      <div class="page-heading">
        <h1>
          患者検索
        </h1>
        <p th:text="${purpose == 'register' ? '薬歴を登録する患者を選択してください。' : (purpose == 'history' ? '薬歴を確認する患者を選択してください。' : '患者名の一部を入力して検索してください。')}">
        </p>
      </div>
      <section class="panel">
        <form method="get" th:action="@{/patients/search}">
          <input type="hidden" name="purpose" th:value="${purpose}">
          <label for="search-name">
            患者名
          </label>
          <div class="search-row">
            <input id="search-name" type="search" name="name" th:value="${name}" maxlength="100" required placeholder="例：山田">
            <button class="button" type="submit">
              検索する
            </button>
          </div>
          <p class="field-error" th:if="${searchError}" th:text="${searchError}">
          </p>
        </form>
      </section>
      <section th:if="${searched}" class="results">
        <h2>
          検索結果
          <span class="count" th:text="${#lists.size(patients)} + '件'">
          </span>
        </h2>
        <div class="empty" th:if="${#lists.isEmpty(patients)}">
          該当する患者が見つかりません。検索する名前を変更してください。
        </div>
        <div class="table-wrap" th:unless="${#lists.isEmpty(patients)}">
          <table>
            <thead>
              <tr>
                <th scope="col">
                  患者ID
                </th>
                <th scope="col">
                  患者名
                </th>
                <th scope="col">
                  生年月日
                </th>
                <th scope="col">
                  操作
                </th>
              </tr>
            </thead>
            <tbody>
              <tr th:each="patient : ${patients}">
                <td th:text="${patient.id}">
                </td>
                <td class="patient-name" th:text="${patient.name}">
                </td>
                <td th:text="${patient.birthDate}">
                </td>
                <td>
                  <div class="actions">
                    <a class="button small" th:href="@{/patients/{id}/medications/new(id=${patient.id})}">
                      薬歴登録
                    </a>
                    <a class="button secondary small" th:href="@{/patients/{id}/medications(id=${patient.id})}">
                      薬歴確認
                    </a>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>
      <a class="back" th:href="@{/}">
        ← ホームへ戻る
      </a>
    </main>
    <footer th:replace="~{fragments :: footer}">
    </footer>
  </body>
</html>

```

## 22. src/main/resources/templates/medication-register.html



```html
<!DOCTYPE html>
<html lang="ja" xmlns:th="http://www.thymeleaf.org">
  <head th:replace="~{fragments :: head('薬歴登録')}">
  </head>
  <body>
    <header th:replace="~{fragments :: header}">
    </header>
    <nav th:replace="~{fragments :: nav}">
    </nav>
    <main>
      <div class="page-heading">
        <h1>
          薬歴登録
        </h1>
        <p>
          選択した患者の処方内容と服薬指導を記録します。
        </p>
      </div>
      <div th:replace="~{fragments :: patient}">
      </div>
      <section class="panel form-panel">
        <h2>
          処方・服薬情報
        </h2>
        <p class="muted">
          備考以外の項目はすべて必須です。
        </p>
        <form th:action="@{/patients/{id}/medications(id=${patient.id})}" th:object="${medicationHistoryForm}" method="post">
          <div class="form-grid">
            <div class="field">
              <label for="prescriptionDate">
                処方日
                <span class="required">
                  必須
                </span>
              </label>
              <input id="prescriptionDate" type="date" th:field="*{prescriptionDate}"  required>
              <p class="field-error" th:if="${#fields.hasErrors('prescriptionDate')}" th:errors="*{prescriptionDate}">
              </p>
            </div>
            <div class="field">
              <label for="medicineName">
                薬剤名
                <span class="required">
                  必須
                </span>
              </label>
              <input id="medicineName" type="text" th:field="*{medicineName}" maxlength="200" required>
              <p class="field-error" th:if="${#fields.hasErrors('medicineName')}" th:errors="*{medicineName}">
              </p>
            </div>
            <div class="field">
              <label for="usage">
                用法
                <span class="required">
                  必須
                </span>
              </label>
              <input id="usage" type="text" th:field="*{usage}" maxlength="200" placeholder="例：1日3回 毎食後" required>
              <p class="field-error" th:if="${#fields.hasErrors('usage')}" th:errors="*{usage}">
              </p>
            </div>
            <div class="field">
              <label for="dosage">
                用量
                <span class="required">
                  必須
                </span>
              </label>
              <input id="dosage" type="text" th:field="*{dosage}" maxlength="100" placeholder="例：1回1錠" required>
              <p class="field-error" th:if="${#fields.hasErrors('dosage')}" th:errors="*{dosage}">
              </p>
            </div>
            <div class="field">
              <label for="durationDays">
                服用日数
                <span class="required">
                  必須
                </span>
              </label>
              <input id="durationDays" type="number" th:field="*{durationDays}" min="1" step="1" max="2147483647" required>
              <p class="field-error" th:if="${#fields.hasErrors('durationDays')}" th:errors="*{durationDays}">
              </p>
            </div>
          </div>
          <div class="field">
            <label for="guidance">
              服薬指導内容
              <span class="required">
                必須
              </span>
            </label>
            <textarea id="guidance" th:field="*{guidance}" rows="4" maxlength="5000" required>
            </textarea>
            <p class="field-error" th:if="${#fields.hasErrors('guidance')}" th:errors="*{guidance}">
            </p>
          </div>
          <div class="field">
            <label for="notes">
              備考
              <span class="muted">
                任意
              </span>
            </label>
            <textarea id="notes" th:field="*{notes}" rows="4" maxlength="5000" >
            </textarea>
            <p class="field-error" th:if="${#fields.hasErrors('notes')}" th:errors="*{notes}">
            </p>
          </div>
          <div class="actions">
            <button class="button" type="submit">
              薬歴を登録する
            </button>
            <a class="button secondary" th:href="@{/patients/{id}/medications(id=${patient.id})}">
              過去の薬歴を確認
            </a>
          </div>
        </form>
      </section>
      <a class="back" th:href="@{/}">
        ← ホームへ戻る
      </a>
    </main>
    <footer th:replace="~{fragments :: footer}">
    </footer>
  </body>
</html>

```

## 23. src/main/resources/templates/medication-history.html



```html
<!DOCTYPE html>
<html lang="ja" xmlns:th="http://www.thymeleaf.org">
  <head th:replace="~{fragments :: head('過去の薬歴')}">
  </head>
  <body>
    <header th:replace="~{fragments :: header}">
    </header>
    <nav th:replace="~{fragments :: nav}">
    </nav>
    <main>
      <div class="page-heading heading-row">
        <div>
          <h1>
            過去の薬歴
          </h1>
          <p>
            処方日の新しい順に表示しています。
          </p>
        </div>
        <a class="button" th:href="@{/patients/{id}/medications/new(id=${patient.id})}">
          ＋ 新規薬歴登録
        </a>
      </div>
      <div th:replace="~{fragments :: success}">
      </div>
      <div th:replace="~{fragments :: patient}">
      </div>
      <section class="results">
        <h2>
          薬歴一覧
          <span class="count" th:text="${#lists.size(histories)} + '件'">
          </span>
        </h2>
        <div class="empty" th:if="${#lists.isEmpty(histories)}">
          薬歴が登録されていません
        </div>
        <div class="table-wrap" th:unless="${#lists.isEmpty(histories)}">
          <table class="history-table">
            <caption class="sr-only">
              患者ごとの薬歴一覧。詳細を開くと全文を確認できます。
            </caption>
            <thead>
              <tr>
                <th scope="col">
                  処方日
                </th>
                <th scope="col">
                  薬剤名
                </th>
                <th scope="col">
                  用法
                </th>
                <th scope="col">
                  用量
                </th>
                <th scope="col">
                  服用日数
                </th>
                <th scope="col">
                  服薬指導内容
                </th>
                <th scope="col">
                  備考
                </th>
                <th scope="col">
                  詳細
                </th>
              </tr>
            </thead>
            <tbody th:each="history : ${histories}">
              <tr>
                <td th:text="${history.prescriptionDate}">
                </td>
                <td class="patient-name" th:text="${history.medicineName}">
                </td>
                <td class="wrap" th:text="${history.usage}">
                </td>
                <td class="wrap" th:text="${history.dosage}">
                </td>
                <td th:text="${history.durationDays} + '日'">
                </td>
                <td class="wrap preserve" th:text="${history.guidance}">
                </td>
                <td class="wrap preserve" th:text="${#strings.isEmpty(history.notes) ? '—' : history.notes}">
                </td>
                <td>
                  <details>
                    <summary>
                      詳細
                    </summary>
                    <dl class="detail">
                      <dt>
                        処方日
                      </dt>
                      <dd th:text="${history.prescriptionDate}">
                      </dd>
                      <dt>
                        薬剤名
                      </dt>
                      <dd th:text="${history.medicineName}">
                      </dd>
                      <dt>
                        用法・用量
                      </dt>
                      <dd th:text="${history.usage} + ' / ' + ${history.dosage}">
                      </dd>
                      <dt>
                        服用日数
                      </dt>
                      <dd th:text="${history.durationDays} + '日'">
                      </dd>
                      <dt>
                        服薬指導内容
                      </dt>
                      <dd class="preserve" th:text="${history.guidance}">
                      </dd>
                      <dt>
                        備考
                      </dt>
                      <dd class="preserve" th:text="${history.notes}">
                      </dd>
                    </dl>
                  </details>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>
      <a class="back" th:href="@{/}">
        ← ホームへ戻る
      </a>
    </main>
    <footer th:replace="~{fragments :: footer}">
    </footer>
  </body>
</html>

```

## 24. src/main/resources/templates/error.html



```html
<!DOCTYPE html>
<html lang="ja" xmlns:th="http://www.thymeleaf.org">
  <head th:replace="~{fragments :: head('エラー')}">
  </head>
  <body>
    <header th:replace="~{fragments :: header}">
    </header>
    <nav th:replace="~{fragments :: nav}">
    </nav>
    <main>
      <section class="panel error-panel">
        <h1>
          処理を完了できませんでした
        </h1>
        <p th:text="${message ?: 'ページが見つからないか、処理中にエラーが発生しました。'}">
        </p>
        <div class="actions">
          <a class="button" th:href="@{/}">
            ホームへ戻る
          </a>
          <a class="button secondary" th:href="@{/patients/search}">
            患者検索へ
          </a>
        </div>
      </section>
    </main>
    <footer th:replace="~{fragments :: footer}">
    </footer>
  </body>
</html>

```

## 25. src/main/resources/static/css/style.css



```css
:root {
    --blue:#1765bd;
    --dark:#18324f;
    --muted:#61748a;
    --line:#dce5ee;
    --bg:#f3f6fa
}

* {
    box-sizing:border-box
}

body {
    margin:0;
    background:var(--bg);
    color:var(--dark);
    font-family:"Noto Sans JP","Yu Gothic",Meiryo,sans-serif;
    font-size:15px;
    line-height:1.75
}

a {
    color:var(--blue)
}

header {
    background:white;
    border-bottom:1px solid var(--line)
}

.header-inner {
    max-width:1140px;
    margin:auto;
    padding:22px 30px;
    display:flex;
    align-items:center;
    justify-content:space-between;
    gap:15px
}

.brand {
    display:flex;
    align-items:center;
    gap:12px;
    text-decoration:none;
    color:var(--dark);
    font-size:21px;
    font-weight:700
}

.brand-icon {
    background:var(--blue);
    color:white;
    width:36px;
    height:36px;
    display:grid;
    place-items:center;
    border-radius:9px;
    font-size:29px;
    line-height:1
}

.environment {
    color:var(--muted);
    font-size:12px
}

nav {
    display:flex;
    gap:28px;
    max-width:1140px;
    margin:auto;
    padding:16px 30px;
    border-bottom:1px solid var(--line)
}

nav a {
    font-size:14px;
    text-decoration:none;
    color:var(--muted);
    white-space:nowrap
}

nav a:hover,nav a[aria-current=page] {
    color:var(--blue)
}

main {
    max-width:1140px;
    margin:auto;
    padding:40px 30px 60px;
    min-height:70vh
}

h1,h2,p {
    margin-top:0
}

h1 {
    font-size:28px;
    line-height:1.5;
    margin-bottom:8px;
    letter-spacing:.02em
}

h2 {
    font-size:19px;
    margin-bottom:12px
}

.page-heading {
    margin-bottom:30px
}

.page-heading p,.section-title p,.workflow>p {
    color:var(--muted);
    margin-bottom:0
}

.home-layout {
    display:grid;
    grid-template-columns:1.1fr 1fr;
    gap:42px;
    align-items:start
}

.panel {
    background:white;
    border:1px solid var(--line);
    border-radius:12px;
    padding:30px;
    box-shadow:0 3px 14px #18324f04
}

.section-title {
    display:flex;
    gap:16px;
    margin-bottom:28px
}

.section-title h2 {
    margin-bottom:3px
}

.section-title p {
    font-size:13px
}

.step {
    font-size:13px;
    color:var(--blue);
    background:#eaf3ff;
    border-radius:8px;
    width:38px;
    height:38px;
    display:grid;
    place-items:center;
    flex-shrink:0
}

.workflow {
    padding:20px 0
}

.workflow>p {
    font-size:14px;
    margin-bottom:22px
}

.workflow-link {
    display:flex;
    justify-content:space-between;
    align-items:center;
    padding:22px 0;
    border-bottom:1px solid var(--line);
    text-decoration:none;
    gap:16px
}

.workflow-link strong {
    display:block;
    font-size:16px
}

.workflow-link small {
    color:var(--muted);
    display:block;
    margin-top:5px
}

.workflow-link:hover strong {
    text-decoration:underline
}

label {
    font-size:14px;
    font-weight:600;
    display:block;
    margin-bottom:8px
}

.field {
    margin-bottom:22px
}

.required,.muted {
    font-weight:400;
    color:var(--muted)
}

.required {
    font-size:11px;
    color:#536c87;
    margin-left:6px
}

.field label .muted {
    font-size:11px;
    margin-left:6px
}

input,textarea {
    font:inherit;
    width:100%;
    border:1px solid #c8d5e3;
    background:white;
    border-radius:6px;
    padding:11px 13px;
    color:var(--dark);
    min-height:46px
}

input::placeholder {
    color:#8595a7
}

textarea {
    resize:vertical
}

input:focus,textarea:focus {
    outline:3px solid #d6e8ff;
    border-color:var(--blue)
}

:focus-visible {
    outline:3px solid #8cbcf0;
    outline-offset:3px
}

.button {
    display:inline-flex;
    align-items:center;
    justify-content:center;
    padding:11px 22px;
    min-height:46px;
    border:1px solid var(--blue);
    border-radius:6px;
    background:var(--blue);
    color:white;
    text-decoration:none;
    font:600 14px/1.5 "Yu Gothic",Meiryo,sans-serif;
    cursor:pointer;
    white-space:nowrap
}

.button:hover {
    background:#10539d;
    border-color:#10539d
}

.button.secondary {
    background:white;
    color:var(--blue)
}

.button.secondary:hover {
    background:#edf5ff
}

.button.small {
    padding:7px 12px;
    min-height:36px;
    font-size:12px
}

.full {
    width:100%;
    margin-top:6px
}

.notice {
    padding:14px 18px;
    border-radius:7px;
    margin-bottom:24px
}

.success {
    background:#e9f6ef;
    color:#235c41;
    border:1px solid #b8decb
}

.field-error {
    color:#b42332;
    font-size:13px;
    margin:7px 0 0
}

.search-row {
    display:flex;
    gap:12px
}

.search-row input {
    flex:1;
    min-width:0
}

.results {
    margin-top:32px
}

.count {
    font-size:13px;
    font-weight:400;
    color:var(--muted);
    margin-left:10px
}

.table-wrap {
    overflow-x:auto;
    background:white;
    border:1px solid var(--line);
    border-radius:8px
}

table {
    width:100%;
    border-collapse:collapse;
    text-align:left;
    font-size:13px
}

th {
    background:#edf3f9;
    color:#526980;
    font-weight:600
}

td,th {
    padding:16px;
    border-bottom:1px solid var(--line);
    vertical-align:top
}

tbody:last-child tr:last-child td {
    border-bottom:0
}

.patient-name {
    font-weight:600
}

.actions {
    display:flex;
    gap:10px;
    flex-wrap:wrap
}

.back {
    display:inline-block;
    margin-top:28px;
    text-decoration:none;
    font-size:14px
}

.empty {
    background:white;
    border:1px dashed #c6d5e4;
    padding:46px 24px;
    text-align:center;
    border-radius:8px;
    color:var(--muted)
}

.patient-summary {
    display:flex;
    gap:42px;
    padding:20px 24px;
    background:#eaf2fb;
    border-left:4px solid var(--blue);
    margin-bottom:28px;
    border-radius:5px
}

.patient-summary>div {
    display:flex;
    flex-direction:column;
    gap:4px
}

.patient-summary .muted {
    font-size:12px
}

.patient-summary strong {
    font-size:19px
}

.form-panel {
    max-width:800px;
    margin:auto
}

.form-grid {
    display:grid;
    grid-template-columns:1fr 1fr;
    gap:0 24px
}

.heading-row {
    display:flex;
    justify-content:space-between;
    align-items:center;
    gap:20px
}

.history-table {
    min-width:1080px
}

.wrap {
    min-width:120px;
    max-width:210px;
    overflow-wrap:anywhere
}

.preserve {
    white-space:pre-wrap;
    overflow-wrap:anywhere
}

details summary {
    cursor:pointer;
    color:var(--blue);
    white-space:nowrap
}

.detail {
    width:240px;
    max-width:100%;
    white-space:normal;
    overflow-wrap:anywhere
}

.detail dt {
    font-weight:600;
    margin-top:10px
}

.detail dd {
    margin-left:0
}

.error-panel {
    max-width:760px;
    margin:40px auto
}

.sr-only {
    position:absolute;
    width:1px;
    height:1px;
    overflow:hidden;
    clip:rect(0,0,0,0)
}

footer {
    max-width:1080px;
    margin:auto;
    border-top:1px solid var(--line);
    padding:22px 0;
    color:var(--muted);
    display:flex;
    justify-content:space-between;
    gap:16px;
    font-size:12px
}

footer p {
    margin:0
}

@media(max-width:700px) {
    .header-inner {
    padding:18px 20px
}

.brand {
    font-size:17px
}

.environment {
    font-size:10px
}

nav {
    padding:14px 20px;
    gap:22px;
    overflow-x:auto
}

nav a {
    font-size:12px
}

main {
    padding:28px 20px 40px
}

h1 {
    font-size:23px
}

.home-layout {
    grid-template-columns:1fr;
    gap:15px
}

.panel {
    padding:24px 20px
}

.workflow {
    padding-top:12px
}

.form-grid {
    grid-template-columns:1fr
}

.heading-row {
    align-items:flex-start;
    flex-direction:column
}

.patient-summary {
    gap:18px;
    flex-wrap:wrap;
    padding:18px
}

.patient-summary strong {
    font-size:17px
}

.search-row {
    flex-wrap:wrap
}

.search-row .button {
    padding:10px 16px
}

td,th {
    padding:12px
}

footer {
    margin:0 20px;
    flex-direction:column;
    gap:5px
}

.error-panel h1 {
    font-size:22px
}


}



.history-table th, .history-table td:first-child  {
     white-space: nowrap;
}



```

## 26. src/main/resources/static/js/app.js



```js
'use strict';
// クライアントの補助機能。最終的な検証はSpring Validationが行います。
document.addEventListener('DOMContentLoaded', () => {
    const date = new Date();
    const today = [date.getFullYear(), String(date.getMonth() + 1).padStart(2, '0'), String(date.getDate()).padStart(2, '0')].join('-');
    document.querySelectorAll('[data-birth-date]').forEach(input => { input.max = today; });
    document.querySelectorAll('nav a').forEach(link => {
        if (link.pathname === location.pathname && link.search === location.search) {
            link.setAttribute('aria-current', 'page');
        }
    });
    // 空白のみの必須文字列を送信する前に通知します。
    document.querySelectorAll('input[required][type="text"], textarea[required]').forEach(input => {
        const validate = () => input.setCustomValidity(input.value.trim() ? '' : '空白以外の文字を入力してください');
        input.addEventListener('input', validate);
        input.addEventListener('invalid', validate);
    });
});

```

## 27. src/main/resources/static/favicon.svg



```svg
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 32 32"><rect width="32" height="32" rx="7" fill="#1765bd"/><path d="M16 7v18M7 16h18" stroke="white" stroke-width="3"/></svg>

```

## 28. src/test/java/com/example/medication/ApplicationIntegrationTest.java



```java
package com.example.medication;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import com.example.medication.entity.Patient;
import com.example.medication.entity.MedicationHistory;
import com.example.medication.form.MedicationHistoryForm;
import com.example.medication.repository.PatientRepository;
import com.example.medication.repository.MedicationHistoryRepository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:integration;DB_CLOSE_DELAY=-1",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
@Transactional
class ApplicationIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired PatientRepository patients;
    @Autowired MedicationHistoryRepository histories;

    Patient patient(String name) {
        return patients.saveAndFlush(new Patient(name, LocalDate.of(1990, 1, 2)));
    }
    MedicationHistoryForm medication(String date, String medicine) {
        MedicationHistoryForm form = new MedicationHistoryForm();
        form.setPrescriptionDate(LocalDate.parse(date));
        form.setMedicineName(medicine);
        form.setUsage("1日3回 毎食後");
        form.setDosage("1回1錠");
        form.setDurationDays(7);
        form.setGuidance("飲み忘れ時は確認してください。");
        form.setNotes("架空のテスト記録");
        return form;
    }
    @Test void homeAndRegistration() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk()).andExpect(view().name("index"));
        mvc.perform(post("/patients").param("name", "架空 花子").param("birthDate", "1990-01-02"))
            .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/"))
            .andExpect(flash().attributeExists("success"));
        assertThat(patients.findByNameContainingIgnoreCaseOrderByIdAsc("架空")).hasSize(1);
    }
    @Test void invalidPatientIsNotSaved() throws Exception {
        long count = patients.count();
        mvc.perform(post("/patients").param("name", "  ").param("birthDate", ""))
            .andExpect(status().isOk()).andExpect(model().attributeHasFieldErrors("patientForm", "name", "birthDate"));
        mvc.perform(post("/patients").param("name", "架空").param("birthDate", LocalDate.now().plusDays(1).toString()))
            .andExpect(model().attributeHasFieldErrors("patientForm", "birthDate"));
        mvc.perform(post("/patients").param("name", "架空").param("birthDate", "invalid"))
            .andExpect(model().attributeHasFieldErrors("patientForm", "birthDate"));
        assertThat(patients.count()).isEqualTo(count);
    }
    @Test void partialSearchIncludesDuplicateNamesAndNoResults() throws Exception {
        patient("架空 同名"); patient("架空 同名"); patient("別の患者");
        mvc.perform(get("/patients/search").param("name", "架空"))
            .andExpect(status().isOk()).andExpect(model().attribute("patients", hasSize(2)));
        mvc.perform(get("/patients/search").param("name", "該当なし"))
            .andExpect(content().string(containsString("該当する患者が見つかりません")));
        mvc.perform(get("/patients/search")).andExpect(status().isOk());
    }
    @Test void registerMedicationForSelectedPatient() throws Exception {
        Patient p = patient("架空 花子");
        String url = "/patients/" + p.getId() + "/medications";
        mvc.perform(get(url + "/new")).andExpect(status().isOk());
        mvc.perform(post(url).param("prescriptionDate", "2026-01-02").param("medicineName", "テスト薬")
            .param("usage", "毎食後").param("dosage", "1錠").param("durationDays", "7")
            .param("guidance", "架空の服薬指導").param("notes", ""))
            .andExpect(redirectedUrl(url)).andExpect(flash().attributeExists("success"));
        assertThat(histories.findByPatientIdOrderByPrescriptionDateDescIdDesc(p.getId())).hasSize(1);
        mvc.perform(get(url)).andExpect(status().isOk()).andExpect(content().string(containsString("テスト薬")));
    }
    @Test void historyIsIsolatedSortedAndEscapesHtml() throws Exception {
        Patient first = patient("架空 花子"); Patient other = patient("架空 太郎");
        histories.save(new MedicationHistory(first, medication("2026-01-01", "古い薬")));
        histories.save(new MedicationHistory(first, medication("2026-02-01", "<script>alert(1)</script>")));
        histories.save(new MedicationHistory(other, medication("2026-03-01", "他の患者の薬")));
        mvc.perform(get("/patients/" + first.getId() + "/medications"))
            .andExpect(status().isOk()).andExpect(model().attribute("histories", hasSize(2)))
            .andExpect(content().string(containsString("&lt;script&gt;")))
            .andExpect(content().string(not(containsString("<script>alert(1)</script>"))))
            .andExpect(content().string(not(containsString("他の患者の薬"))));
        assertThat(histories.findByPatientIdOrderByPrescriptionDateDescIdDesc(first.getId()))
            .extracting(MedicationHistory::getPrescriptionDate)
            .containsExactly(LocalDate.of(2026, 2, 1), LocalDate.of(2026, 1, 1));
    }
    @Test void emptyHistoryAndInvalidIds() throws Exception {
        Patient p = patient("架空");
        mvc.perform(get("/patients/" + p.getId() + "/medications"))
            .andExpect(content().string(containsString("薬歴が登録されていません")));
        mvc.perform(get("/patients/999999/medications")).andExpect(status().isNotFound()).andExpect(view().name("error"));
        mvc.perform(get("/patients/999999/medications/new")).andExpect(status().isNotFound());
        mvc.perform(post("/patients/999999/medications")).andExpect(status().isNotFound());
        mvc.perform(get("/patients/abc/medications")).andExpect(status().isBadRequest());
    }
    @Test void invalidMedicationsAreRejected() throws Exception {
        String url = "/patients/" + patient("架空").getId() + "/medications";
        mvc.perform(post(url)).andExpect(model().attributeHasFieldErrors("medicationHistoryForm",
            "prescriptionDate", "medicineName", "usage", "dosage", "durationDays", "guidance"));
        for (String value : new String[]{"0", "-1", "1.5", "abc", "2147483648"}) {
            mvc.perform(post(url).param("durationDays", value))
                .andExpect(status().isOk()).andExpect(model().attributeHasFieldErrors("medicationHistoryForm", "durationDays"));
        }
        assertThat(histories.count()).isZero();
    }
    @Test void consoleIsDisabledByDefault() throws Exception {
        mvc.perform(get("/h2-console/")).andExpect(status().isNotFound());
    }
}

```

## 29. src/test/java/com/example/medication/DatabaseErrorTest.java



```java
package com.example.medication;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.example.medication.service.PatientService;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:failure", "spring.jpa.hibernate.ddl-auto=create-drop"})
@AutoConfigureMockMvc
class DatabaseErrorTest {
    @Autowired MockMvc mvc;
    @MockitoBean PatientService patients;
    @Test void databaseFailureDoesNotExposeDetails() throws Exception {
        when(patients.register(any())).thenThrow(new DataAccessResourceFailureException("SECRET SQL AND PATIENT DATA"));
        mvc.perform(post("/patients").param("name", "架空").param("birthDate", "1990-01-01"))
            .andExpect(status().isInternalServerError()).andExpect(view().name("error"))
            .andExpect(content().string(containsString("データベース処理に失敗しました")))
            .andExpect(content().string(not(containsString("SECRET"))));
    }
}

```

## 30. src/test/java/com/example/medication/FileDatabaseTest.java



```java
package com.example.medication;

import java.nio.file.Path;
import java.sql.DriverManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.assertj.core.api.Assertions.assertThat;

/** 実データを触らず、一時ディレクトリでH2を完全に閉じて再接続します。 */
class FileDatabaseTest {
    @TempDir Path directory;
    @Test void fileDataSurvivesClosingAllConnections() throws Exception {
        String url = "jdbc:h2:file:" + directory.resolve("persistence").toAbsolutePath().toString().replace('\\', '/') + ";DB_CLOSE_ON_EXIT=FALSE";
        try (var connection = DriverManager.getConnection(url, "sa", ""); var statement = connection.createStatement()) {
            statement.execute("CREATE TABLE patients(id BIGINT PRIMARY KEY, name VARCHAR(100), birth_date DATE)");
            statement.execute("INSERT INTO patients VALUES (1, 'fictional', DATE '1990-01-01')");
        }
        try (var connection = DriverManager.getConnection(url, "sa", ""); var statement = connection.createStatement();
             var result = statement.executeQuery("SELECT name FROM patients WHERE id = 1")) {
            assertThat(result.next()).isTrue();
            assertThat(result.getString(1)).isEqualTo("fictional");
        }
    }
}

```

## 31. .gitignore



```text
/target/
/data/
*.log
.idea/
.vscode/

```
