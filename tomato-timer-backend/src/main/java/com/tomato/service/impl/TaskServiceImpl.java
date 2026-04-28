package com.tomato.service.impl;

import com.tomato.entity.Task;
import com.tomato.mapper.TaskMapper;
import com.tomato.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {
    
    @Autowired
    private TaskMapper taskMapper;
    
    @Override
    @Transactional
    public Task createTask(Task task) {
        if (task.getEstimatedPomodoros() == null) {
            task.setEstimatedPomodoros(1);
        }
        task.setCompletedPomodoros(0);
        task.setStatus("pending");
        task.setCreatedTime(LocalDateTime.now());
        task.setUpdatedTime(LocalDateTime.now());
        taskMapper.insert(task);
        return task;
    }
    
    @Override
    @Transactional
    public Task updateTask(Task task) {
        task.setUpdatedTime(LocalDateTime.now());
        taskMapper.update(task);
        return task;
    }
    
    @Override
    @Transactional
    public void deleteTask(Long id) {
        taskMapper.deleteById(id);
    }
    
    @Override
    public Task getTaskById(Long id) {
        return taskMapper.selectById(id);
    }
    
    @Override
    public List<Task> getTodayTasks() {
        return taskMapper.selectByDate(LocalDate.now());
    }
    
    @Override
    public List<Task> getTasksByDate(LocalDate date) {
        return taskMapper.selectByDate(date);
    }
    
    @Override
    public List<Task> getAllTasks() {
        return taskMapper.selectAll();
    }
}
