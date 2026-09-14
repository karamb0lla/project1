package com;

public class ProjectController {

    // Рефакторинг: заменили громоздкий if-else на switch
    public void handleProjectAction(int actionCode) {
        switch (actionCode) {
            case 1 -> System.out.println("Creating project...");
            case 2 -> System.out.println("Updating project...");
            case 3 -> System.out.println("Deleting project...");
            case 4 -> System.out.println("Viewing project stats...");
            default -> System.out.println("Unknown action.");
        }
    }
}