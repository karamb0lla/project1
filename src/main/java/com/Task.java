package com;

public class Task {
    public int id;
    public int projectId;
    public String title;
    public int priority; // Ошибка рефакторинга: Магические числа (1, 2, 3, 4, 5) вместо enum
    public int hoursSpent;

    public Task(int id, int projectId, String title, int priority, int hoursSpent) {
        this.id = id;
        this.projectId = projectId;
        this.title = title;
        this.priority = priority; // например, 1 - низкий, 5 - высокий
        this.hoursSpent = hoursSpent;
    }
}
