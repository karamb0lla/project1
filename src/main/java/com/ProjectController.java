package com;

public class ProjectController {

    public void handleProjectAction(int actionCode) {
        if (actionCode == 1) {
            System.out.println("Creating project...");
        } else if (actionCode == 2) {
            System.out.println("Updating project...");
        } else if (actionCode == 3) {
            System.out.println("Deleting project...");
        } else if (actionCode == 4) {
            System.out.println("Viewing project stats...");
        } else {
            System.out.println("Unknown action.");
        }
    }
}