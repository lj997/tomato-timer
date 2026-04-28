package com.tomato.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Task {
    private Long id;
    private String title;
    private Integer estimatedPomodoros;
    private Integer completedPomodoros;
    private String status;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
