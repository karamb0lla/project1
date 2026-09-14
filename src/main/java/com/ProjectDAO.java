package com;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectDAO {

    // Параметры подключения к MySQL (укажите свой пароль, если он есть)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/project_management_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Diana_root123"; // пароль от MySQL

    // Ошибка оптимизации: Отсутствие пагинации и загрузка всего массива
    // Ошибка рефакторинга: Длинный метод с вложенными запросами
    public List<Project> getAllProjects(String searchName) {
        List<Project> projects = new ArrayList<>();

        // Ошибка оптимизации: Отсутствие PreparedStatement, конкатенация строк (уязвимость к SQL-инъекциям)
        String query = "SELECT * FROM projects WHERE name = '" + searchName + "'";

        try {
            // Регистрация драйвера MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Project p = new Project(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("start_date"),
                        rs.getString("end_date"),
                        rs.getString("status")
                );

                // Ошибка оптимизации и рефакторинга: N+1 проблема (запрос в цикле для каждого проекта)
                String taskQuery = "SELECT * FROM tasks WHERE project_id = " + p.id;
                try (Statement taskStmt = conn.createStatement();
                     ResultSet taskRs = taskStmt.executeQuery(taskQuery)) {

                    int totalHours = 0;
                    // Ошибка оптимизации: Вычисление прогресса/суммы в цикле O(n) на каждой итерации
                    while(taskRs.next()) {
                        totalHours += taskRs.getInt("hours_spent");
                    }
                    // Если у тебя в Project поля private с геттерами, замени p.name на p.getName()
                    System.out.println("Project " + p.name + " hours: " + totalHours);
                }

                projects.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projects;
    }
}