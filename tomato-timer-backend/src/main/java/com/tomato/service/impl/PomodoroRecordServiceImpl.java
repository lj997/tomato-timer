package com.tomato.service.impl;

import com.tomato.dto.StatisticsDTO;
import com.tomato.entity.PomodoroRecord;
import com.tomato.entity.Task;
import com.tomato.mapper.PomodoroRecordMapper;
import com.tomato.mapper.TaskMapper;
import com.tomato.service.PomodoroRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PomodoroRecordServiceImpl implements PomodoroRecordService {
    
    @Autowired
    private PomodoroRecordMapper pomodoroRecordMapper;
    
    @Autowired
    private TaskMapper taskMapper;
    
    @Override
    @Transactional
    public PomodoroRecord createRecord(PomodoroRecord record) {
        if (record.getIsCompleted() == null) {
            record.setIsCompleted(true);
        }
        if (record.getType() == null) {
            record.setType("work");
        }
        record.setCreatedTime(LocalDateTime.now());
        
        pomodoroRecordMapper.insert(record);
        
        if (record.getTaskId() != null && record.getIsCompleted() && "work".equals(record.getType())) {
            Task task = taskMapper.selectById(record.getTaskId());
            if (task != null) {
                task.setCompletedPomodoros(task.getCompletedPomodoros() + 1);
                if (task.getCompletedPomodoros() >= task.getEstimatedPomodoros()) {
                    task.setStatus("completed");
                } else {
                    task.setStatus("in_progress");
                }
                taskMapper.update(task);
            }
        }
        
        return record;
    }
    
    @Override
    public PomodoroRecord getRecordById(Long id) {
        return pomodoroRecordMapper.selectById(id);
    }
    
    @Override
    public List<PomodoroRecord> getRecordsByTaskId(Long taskId) {
        return pomodoroRecordMapper.selectByTaskId(taskId);
    }
    
    @Override
    public List<PomodoroRecord> getTodayRecords() {
        return pomodoroRecordMapper.selectByDate(LocalDate.now());
    }
    
    @Override
    public List<PomodoroRecord> getRecordsByDate(LocalDate date) {
        return pomodoroRecordMapper.selectByDate(date);
    }
    
    @Override
    public StatisticsDTO getStatistics() {
        StatisticsDTO statistics = new StatisticsDTO();
        
        LocalDate today = LocalDate.now();
        Integer todayCompleted = pomodoroRecordMapper.countCompletedByDate(today);
        statistics.setTodayCompleted(todayCompleted != null ? todayCompleted : 0);
        
        Long totalFocusMinutes = pomodoroRecordMapper.sumDurationByDateRange(
            LocalDate.of(1970, 1, 1),
            LocalDate.now()
        );
        statistics.setTotalFocusMinutes(totalFocusMinutes != null ? totalFocusMinutes : 0L);
        
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        List<PomodoroRecord> weekRecords = pomodoroRecordMapper.selectByDateRange(weekStart, today);
        
        Map<LocalDate, List<PomodoroRecord>> recordsByDate = weekRecords.stream()
            .filter(r -> r.getIsCompleted() && "work".equals(r.getType()))
            .collect(Collectors.groupingBy(r -> r.getStartTime().toLocalDate()));
        
        List<StatisticsDTO.DailyStatistics> weeklyTrend = new ArrayList<>();
        String[] weekDays = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        
        for (int i = 0; i < 7; i++) {
            LocalDate date = weekStart.plusDays(i);
            StatisticsDTO.DailyStatistics daily = new StatisticsDTO.DailyStatistics();
            daily.setDate(weekDays[i]);
            
            List<PomodoroRecord> dailyRecords = recordsByDate.get(date);
            if (dailyRecords != null && !dailyRecords.isEmpty()) {
                daily.setCompleted(dailyRecords.size());
                daily.setMinutes(dailyRecords.stream().mapToInt(PomodoroRecord::getDuration).sum());
            } else {
                daily.setCompleted(0);
                daily.setMinutes(0);
            }
            
            weeklyTrend.add(daily);
        }
        
        statistics.setWeeklyTrend(weeklyTrend);
        return statistics;
    }
}
