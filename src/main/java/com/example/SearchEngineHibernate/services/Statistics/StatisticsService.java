package com.example.SearchEngineHibernate.services.Statistics;

import com.example.SearchEngineHibernate.dto.statistics.StatisticsResponse;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.text.ParseException;

public interface StatisticsService {
    StatisticsResponse getStatistics() throws SQLException, ParseException;
}
