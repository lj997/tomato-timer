package com.tomato.service;

import com.tomato.dto.StatisticsDTO;
import com.tomato.entity.PomodoroRecord;
import java.time.LocalDate;
import java.util.List;

public interface PomodoroRecordService {
    
    PomodoroRecord createRecord(PomodoroRecord record);
    
    PomodoroRecord getRecordById(Long id);
    
    List<PomodoroRecord> getRecordsByTaskId(Long taskId);
    
    List<PomodoroRecord> getTodayRecords();
    
    List<PomodoroRecord> getRecordsByDate(LocalDate date);
    
    StatisticsDTO getStatistics();
}
