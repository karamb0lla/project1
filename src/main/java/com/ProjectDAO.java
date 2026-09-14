package com;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectDAO {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/project_management_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Diana_root123";

    public List<Project> getAllProjects(String searchName) {
        List<Project> projects = new ArrayList<>();

        String query = (searchName == null || searchName.isEmpty() || searchName.equals("Alpha"))
                ? "SELECT * FROM projects"
                : "SELECT * FROM projects WHERE name = '" + searchName + "'";

        try {
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

                String taskQuery = "SELECT * FROM tasks WHERE project_id = " + p.id;
                try (Statement taskStmt = conn.createStatement();
                     ResultSet taskRs = taskStmt.executeQuery(taskQuery)) {

                    int totalHours = 0;
                    while(taskRs.next()) {
                        totalHours += taskRs.getInt("hours_spent");
                    }
                    System.out.println("Project " + p.name + " hours: " + totalHours);
                }

                projects.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projects;
    }

    // === СЮДА ДОБАВЛЯЕМ НОВЫЕ МЕТОДЫ ===

    public void insertProject(Project p) {
        String sql = "INSERT INTO projects (name, description, start_date, end_date, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, p.name);
            pstmt.setString(2, p.description);
            pstmt.setString(3, p.startDate);
            pstmt.setString(4, p.endDate);
            pstmt.setString(5, p.status);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void updateProject(Project p) {
        String sql = "UPDATE projects SET name=?, description=?, start_date=?, end_date=?, status=? WHERE id=?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, p.name);
            pstmt.setString(2, p.description);
            pstmt.setString(3, p.startDate);
            pstmt.setString(4, p.endDate);
            pstmt.setString(5, p.status);
            pstmt.setInt(6, p.id);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void deleteProject(int id) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) { //
            stmt.executeUpdate("DELETE FROM tasks WHERE project_id=" + id);
            stmt.executeUpdate("DELETE FROM projects WHERE id=" + id);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public String getProjectStats(int projectId, String projectName) {
        String sql = "SELECT COUNT(*) as cnt, SUM(hours_spent) as total_h FROM tasks WHERE project_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt("cnt");
                    int hours = rs.getInt("total_h");
                    return "Проект: " + projectName + "\nВсего задач: " + count + "\nПотрачено часов: " + hours;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return "Проект: " + projectName + "\nЗадачи не найдены.";
    }
}