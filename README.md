# 薬歴管理システム

Java 21 / Spring Boot 3.5.16 / Maven / Thymeleaf / Spring MVC / Spring Data JPA / H2 / Spring Validation を使用する学習用Webアプリです。フロントエンドフレームワークは使用していません。

## 1. プロジェクト構成と作成方法

このディレクトリがMavenプロジェクトのルートです。生成済みのため、そのまま起動できます。

新規に作る場合はSpring InitializrでMaven・Java・Jar・Java 21を選び、Spring Web、Thymeleaf、Spring Data JPA、Validation、H2 Databaseを追加します。このプロジェクトではpom.xmlの親バージョンで依存関係を揃えています。Lombokは使わず、getter/setterを明記しています。

```text
pom.xml
README.md
IMPLEMENTATION.md               全ファイルのコードと保存先（省略なし）
.gitignore
src/main/java/com/example/medication/
  MedicationHistoryApplication.java
  entity/Patient.java
  entity/MedicationHistory.java
  repository/PatientRepository.java
  repository/MedicationHistoryRepository.java
  service/PatientService.java
  service/MedicationHistoryService.java
  controller/HomeController.java
  controller/PatientController.java
  controller/MedicationHistoryController.java
  form/PatientForm.java
  form/MedicationHistoryForm.java
  exception/PatientNotFoundException.java
  exception/GlobalExceptionHandler.java
src/main/resources/
  application.properties
  application-local.properties
  messages.properties
  templates/fragments.html
  templates/index.html
  templates/patient-search.html
  templates/medication-register.html
  templates/medication-history.html
  templates/error.html
  static/css/style.css
  static/js/app.js
src/test/java/com/example/medication/
  ApplicationIntegrationTest.java
  DatabaseErrorTest.java
  FileDatabaseTest.java
data/                            通常起動時に自動生成・Git管理対象外
```

## 2. データベース設計

patients の1行に対して medication_histories を複数行関連付けます。JPAの @OneToMany / @ManyToOne と外部キーで関連付けます。患者名に一意制約はありません。

| テーブル | カラム | 型 | 制約・用途 |
|---|---|---|---|
| patients | id | BIGINT | 主キー・自動採番 |
| patients | name | VARCHAR(100) | NOT NULL・患者名 |
| patients | birth_date | DATE | NOT NULL・生年月日 |
| medication_histories | id | BIGINT | 主キー・自動採番 |
| medication_histories | patient_id | BIGINT | NOT NULL・patients.idへの外部キー |
| medication_histories | prescription_date | DATE | NOT NULL・処方日 |
| medication_histories | medicine_name | VARCHAR(200) | NOT NULL・薬剤名 |
| medication_histories | usage | VARCHAR(200) | NOT NULL・用法・引用符付き識別子 |
| medication_histories | dosage | VARCHAR(100) | NOT NULL・用量 |
| medication_histories | duration_days | INTEGER | NOT NULL・服用日数 |
| medication_histories | guidance | TEXT | NOT NULL・服薬指導内容 |
| medication_histories | notes | TEXT | 任意・備考 |

patient_id と prescription_date の複合インデックスを設けています。ddl-auto=update により初回起動時にテーブルを作成し、終了時には削除しません。DBは起動ディレクトリを基準とする data/medication-history.mv.db に保存します。同じディレクトリから起動してください。

## 3. 実装を読む順番

[IMPLEMENTATION.md](IMPLEMENTATION.md) に保存先と全コードを次の順序で掲載しています。

1. pom.xml：Spring Bootの親POMでライブラリのバージョンを管理します。
2. application.properties：ファイルDB、JPA、エラー表示・ログ設定。local用ファイルでConsoleだけを有効化します。
3. Entity：DBのテーブルをJavaクラスで表現します。
4. Repository：Spring Data JPAがCRUD・部分一致検索・並び替えを実装します。
5. Service：保存処理とトランザクション、存在確認を担います。
6. Form DTO：画面から受け取る値だけを保持し、Validationアノテーションで検証します。
7. Controller：URLと画面を結び、入力結果を確認してServiceを呼びます。
8. 例外処理：存在しない患者は404、不正なIDは400、DB処理失敗は500のエラー画面です。
9. Thymeleaf：共通部品と4画面、エラー画面です。
10. CSS：青・白・薄いグレー、中央のフォーム、横スクロール可能な薬歴テーブルです。
11. JavaScript：生年月日の上限日、ナビゲーションの現在位置、空白のみの入力を補助的に検証します。
12. テスト：MVC・検証・DB・例外・永続化を確認します。

## 4. アプリケーション起動

前提：JDK 21、Maven 3.6.3以上。初回は依存関係を取得するためネットワーク接続が必要です。

プロジェクトのルートで実行します。

```powershell
mvn clean test
mvn spring-boot:run
```

ブラウザで http://127.0.0.1:8080/ を開きます。終了は Ctrl+C です。標準ではH2 Consoleは無効です。

H2 Consoleも利用するローカル開発時：

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=local"
```

JARにして起動する場合：

```powershell
mvn clean package
java -jar target/medication-history-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
```

Mavenの既存設定が C:/.m2/repository を指し、書込権限がない場合、この環境では次の指定を使用できます。

```powershell
mvn "-Dmaven.repo.local=C:/Users/hiras/.m2/repository" test
mvn "-Dmaven.repo.local=C:/Users/hiras/.m2/repository" spring-boot:run "-Dspring-boot.run.profiles=local"
```

## 5. H2 Consoleでデータベースを確認

localプロファイルで起動中に http://127.0.0.1:8080/h2-console/ を開きます。

| 接続項目 | 入力値 |
|---|---|
| Driver Class | org.h2.Driver |
| JDBC URL | jdbc:h2:file:./data/medication-history;DB_CLOSE_ON_EXIT=FALSE |
| User Name | sa |
| Password | 空欄（H2_PASSWORD環境変数を設定した場合はその値） |

```sql
SELECT id, name, birth_date FROM patients ORDER BY id;
SELECT id, patient_id, prescription_date, medicine_name,
       "usage", dosage, duration_days, guidance, notes
FROM medication_histories
ORDER BY patient_id, prescription_date DESC, id DESC;
```

usageは予約語との衝突を防ぐため、小文字・二重引用符付きで指定します。別のJDBC URLを入力すると別のDBへ接続するため、URLは上記を正確に入力してください。

## 6. 画面遷移

| HTTP | URL | 処理 |
|---|---|---|
| GET | / | ホーム・患者登録フォーム |
| POST | /patients | 患者登録、成功時ホームへリダイレクト |
| GET | /patients/search?name=山田 | 名前の部分一致検索 |
| GET | /patients/{patientId}/medications/new | 選択患者の薬歴登録フォーム |
| POST | /patients/{patientId}/medications | 薬歴保存、成功時一覧へリダイレクト |
| GET | /patients/{patientId}/medications | 選択患者の薬歴一覧 |

ホームの薬歴登録・確認ボタンからは、検索画面を経由して患者を選びます。薬歴一覧の「詳細」を開くと全文を確認できます。

## 7. 入力チェック

- 患者名：必須、空白だけは不可、100文字以内。
- 生年月日：必須、有効な日付、今日以前。
- 処方日：必須、有効な日付。
- 薬剤名・用法：必須、200文字以内。
- 用量：必須、100文字以内。
- 服用日数：必須、1〜2147483647の整数。小数は型変換エラー。
- 服薬指導内容：必須、5000文字以内。
- 備考：任意、5000文字以内。

@NotBlank / @NotNull / @PastOrPresent / @Min / @Size を @Valid で検証します。日付・数値の型変換エラーは messages.properties の日本語メッセージを表示します。エラー時は入力内容を保ったまま同じ画面へ戻ります。

## 8. 動作確認とテスト

```powershell
mvn test
```

自動テストは本番用の data/ を変更しません。MVC結合テストはメモリH2とロールバックを使い、永続化テストは一時ディレクトリのファイルH2を閉じて再接続します。

手動での確認手順：

1. ホームで「架空 花子」「1990-01-02」を登録し成功表示を確認する。
2. 「架空」で検索し、ID・名前・生年月日を確認する。同姓同名でも追加できることを確認する。
3. 対象患者の「薬歴登録」を押し、処方日、架空の薬剤名、用法、用量、日数、服薬指導を入力する。
4. 登録すると薬歴一覧へ移り、成功表示と保存した項目が見えることを確認する。
5. さらに異なる処方日の薬歴を追加し、新しい順に表示されることを確認する。
6. 別患者の薬歴一覧に混ざらないこと、未登録患者では「薬歴が登録されていません」と出ることを確認する。
7. 空欄・未来の生年月日・日数0を入力し、登録できないことを確認する。
8. /patients/999999/medications を開き、エラー画面を確認する。
9. localプロファイルでH2 Consoleへ接続し、上記SQLで2テーブルを確認する。
10. Ctrl+Cで停止後、同じディレクトリから同じ設定で再起動し、検索・薬歴を再確認する。
11. local指定を外して起動すると /h2-console/ にアクセスできないことを確認する。
12. スマートフォン幅でフォームとボタンを確認する。横長のテーブルは左右にスクロールできる。

## 9. セキュリティ・制限

- Repositoryのパラメータバインドを使い、入力値をSQLに連結しません。
- 表示は th:text でエスケープし、th:utext や innerHTML は使用しません。
- URLの患者IDで存在確認し、その患者IDに一致する薬歴のみ検索します。
- 患者情報、薬歴、入力DTO、SQL・バインド値やDB例外の詳細をログに出力しません。
- サーバーは127.0.0.1にのみバインドし、H2 Consoleはlocal時のみ有効・リモート接続不可です。
- data/はGitに含めません。架空のテストデータのみを使用してください。
- 認証・利用者ごとのアクセス権限・暗号化・監査ログは実装範囲外です。患者IDの絞り込みはログイン認可の代替ではありません。実際の医療現場向けのアプリではありません。

## 10. この環境での検証結果（2026-10-30）

Java 21.0.12 / Maven 3.9.16 / Chromiumを使用しました。

| 確認内容 | 結果 |
|---|---|
| Mavenテスト10件・実行可能JAR生成 | 成功 |
| ホーム表示・患者登録・成功表示 | 成功 |
| 名前検索・患者選択・薬歴保存・一覧と詳細 | 成功 |
| 画面識別・内容表示・エラー画面混入なし | 成功 |
| 1440×1000 / 390×844 での画面確認 | 成功 |
| JavaScriptエラー・HTTPリソースエラー | 最終確認で0件 |
| H2 Consoleへの接続と両テーブルのSELECT | 成功 |
| Javaプロセス停止・再起動後の登録済み薬歴表示 | 成功 |

Browserプラグインが利用できないため、既存のPlaywrightを使いました。検証用DBは target/qa-db/ に分離し、通常の data/ は作成・変更していません。日付・テーブル見出しの折返し、モバイルフォームの幅、ナビゲーション、成功表示、詳細開閉を確認しました。ブラウザ確認で発見した初回登録後のURLセッションID問題は、Cookie方式に固定して修正しています。検証用サーバーは確認後に停止しています。
