package com.tomato.controller;

import com.tomato.dto.StatisticsDTO;
import com.tomato.entity.PomodoroRecord;
import com.tomato.service.PomodoroRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/records")
public class PomodoroRecordController {
    
    @Autowired
    private PomodoroRecordService pomodoroRecordService;
    
    @PostMapping
    public ResponseEntity<PomodoroRecord> createRecord(@RequestBody PomodoroRecord record) {
        PomodoroRecord created = pomodoroRecordService.createRecord(record);
        return ResponseEntity.ok(created);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PomodoroRecord> getRecordById(@PathVariable Long id) {
        PomodoroRecord record = pomodoroRecordService.getRecordById(id);
        return record != null ? ResponseEntity.ok(record) : ResponseEntity.notFound().build();
    }
    
    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<PomodoroRecord>> getRecordsByTaskId(@PathVariable Long taskId) {
        List<PomodoroRecord> records = pomodoroRecordService.getRecordsByTaskId(taskId);
        return ResponseEntity.ok(records);
    }
    
    @GetMapping("/today")
    public ResponseEntity<List<PomodoroRecord>> getTodayRecords() {
        List<PomodoroRecord> records = pomodoroRecordService.getTodayRecords();
        return ResponseEntity.ok(records);
    }
    
    @GetMapping("/statistics")
    public ResponseEntity<StatisticsDTO> getStatistics() {
        StatisticsDTO statistics = pomodoroRecordService.getStatistics();
        return ResponseEntity.ok(statistics);
    }
}
