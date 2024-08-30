package model;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Task {
    private Long id;
    private String title;
    @Getter
    private String description;
    private LocalDate date;
    private boolean completed;

    public Task(String title, String description, LocalDate date) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.completed = false;
    }

    public String getTask() {
        return title;
    }

}