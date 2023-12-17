package com.example.SearchEngineHibernate.controllers;


import com.example.SearchEngineHibernate.dto.statistics.StatisticsResponse;
import com.example.SearchEngineHibernate.services.Statistics.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.text.ParseException;

@Component
@RestController
public class ApiController {
   @Autowired
    private StatisticsService statisticsService;

    public ApiController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/api/statistics")
    public ResponseEntity<StatisticsResponse> statistics() throws ParseException, SQLException {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }
}
