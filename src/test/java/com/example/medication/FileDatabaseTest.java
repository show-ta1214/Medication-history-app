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
