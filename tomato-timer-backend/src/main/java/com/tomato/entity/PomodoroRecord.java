package com.tomato.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PomodoroRecord {
    private Long id;
    private Long taskId;
    private String taskTitle;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer duration;
    private Boolean isCompleted;
    private String type;
    private LocalDateTime createdTime;
}
