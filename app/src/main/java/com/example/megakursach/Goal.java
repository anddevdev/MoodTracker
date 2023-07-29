
package com.example.megakursach;
public class Goal {
    private int id;
    private String title;
    private String description;
    private int targetValue;
    private String targetDate;

    public Goal(int id, String title, String description, int targetValue, String targetDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.targetValue = targetValue;
        this.targetDate = targetDate;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getTargetValue() {
        return targetValue;
    }

    public String getTargetDate() {
        return targetDate;
    }
}
