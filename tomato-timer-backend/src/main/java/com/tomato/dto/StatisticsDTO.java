package com.tomato.dto;

import lombok.Data;
import java.util.List;

@Data
public class StatisticsDTO {
    private Integer todayCompleted;
    private Long totalFocusMinutes;
    private List<DailyStatistics> weeklyTrend;
    
    @Data
    public static class DailyStatistics {
        private String date;
        private Integer completed;
        private Integer minutes;
    }
}
