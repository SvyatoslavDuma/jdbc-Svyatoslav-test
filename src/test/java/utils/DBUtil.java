package utils;

import model.Category;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBUtil {
//    public static Connection getConnection() throws SQLException {
//        return DriverManager
//                .getConnection("jdbc:postgresql://localhost:5432/test", "postgres", "root");
//    }
    public static Connection getConnection() throws SQLException {
        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USERNAME");
        String password = System.getenv("DB_PASSWORD");
        if (url == null || user == null || password == null) {
            throw new SQLException("Database connection details not provided in environment variables.");
        }
        return DriverManager.getConnection(url, user, password);
    }

    public static int executeStatement(String query) throws SQLException {
        Statement st = getConnection().createStatement();
        return st.executeUpdate(query);
    }

    public static int totalCount(String tableName) throws SQLException {
        String query = "SELECT count(*) FROM " + tableName;
        Statement statement = getConnection().createStatement();
        ResultSet rs = statement.executeQuery(query);
        rs.next();
        return rs.getInt(1);
    }

    public void executeFile(String path) throws IOException, SQLException {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(path);


        assert inputStream != null;
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

             Statement statement = getConnection().createStatement()) {

            StringBuilder builder = new StringBuilder();

            String line;
            int lineNumber = 0;
            int count = 0;

            while ((line = bufferedReader.readLine()) != null) {
                lineNumber += 1;
                line = line.trim();

                if (line.isEmpty() || line.startsWith("--"))
                    continue;

                builder.append(line);
                if (line.endsWith(";"))
                    try {
                        statement.execute(builder.toString());
                        System.err.println(
                                ++count
                                        + " Command successfully executed : "
                                        + builder.substring(
                                        0,
                                        Math.min(builder.length(), 15))
                                        + "...");
                        builder.setLength(0);
                    } catch (SQLException e) {
                        System.err.println(
                                "At line " + lineNumber + " : "
                                        + e.getMessage() + "\n");
                        return;
                    }
            }

        }
    }
}
