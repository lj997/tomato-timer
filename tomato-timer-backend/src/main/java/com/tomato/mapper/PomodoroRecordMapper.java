package com.tomato.mapper;

import com.tomato.entity.PomodoroRecord;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

public interface PomodoroRecordMapper {
    
    int insert(PomodoroRecord record);
    
    int update(PomodoroRecord record);
    
    PomodoroRecord selectById(@Param("id") Long id);
    
    List<PomodoroRecord> selectByTaskId(@Param("taskId") Long taskId);
    
    List<PomodoroRecord> selectByDate(@Param("date") LocalDate date);
    
    List<PomodoroRecord> selectByDateRange(@Param("startDate") LocalDate startDate, 
                                            @Param("endDate") LocalDate endDate);
    
    Integer countCompletedByDate(@Param("date") LocalDate date);
    
    Long sumDurationByDateRange(@Param("startDate") LocalDate startDate, 
                                 @Param("endDate") LocalDate endDate);
}
