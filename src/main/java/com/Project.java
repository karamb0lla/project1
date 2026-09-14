package com;

public class Project {
    // Ошибка рефакторинга
    public int id;
    public String name;
    public String description;
    public String startDate;
    public String endDate;
    public String status;

    // Ошибка рефакторинга
    public Project(int id, String name, String description, String startDate, String endDate, String status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }
}
