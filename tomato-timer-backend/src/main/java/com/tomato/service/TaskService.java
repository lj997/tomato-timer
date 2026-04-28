package com.tomato.service;

import com.tomato.entity.Task;
import java.time.LocalDate;
import java.util.List;

public interface TaskService {
    
    Task createTask(Task task);
    
    Task updateTask(Task task);
    
    void deleteTask(Long id);
    
    Task getTaskById(Long id);
    
    List<Task> getTodayTasks();
    
    List<Task> getTasksByDate(LocalDate date);
    
    List<Task> getAllTasks();
}
