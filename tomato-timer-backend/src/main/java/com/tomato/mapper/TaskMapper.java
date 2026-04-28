package com.tomato.mapper;

import com.tomato.entity.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface TaskMapper {
    
    int insert(Task task);
    
    int update(Task task);
    
    int deleteById(@Param("id") Long id);
    
    Task selectById(@Param("id") Long id);
    
    List<Task> selectByDate(@Param("date") LocalDate date);
    
    List<Task> selectAll();
}
