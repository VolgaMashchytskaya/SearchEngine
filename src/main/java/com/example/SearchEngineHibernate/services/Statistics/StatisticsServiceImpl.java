package com.example.SearchEngineHibernate.services.Statistics;

import com.example.SearchEngineHibernate.dto.app.Site;
import com.example.SearchEngineHibernate.dto.statistics.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Getter
@Setter
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private SitesList sites;

    @Override
    public StatisticsResponse getStatistics() throws SQLException, ParseException {
        String[] statuses = { "INDEXED", "FAILED", "INDEXING" };
        String[] errors = {
                "Ошибка индексации: главная страница сайта не доступна",
                "Ошибка индексации: сайт не доступен",
                ""
        };

        List<Site> sitesList = sites.sitesList();

        TotalStatistics total = new TotalStatistics();
        total.setSites(sitesList.size());
        total.setIndexing(true);

        List<DetailedStatisticsItem> detailed = new ArrayList<>();
        for(int i = 0; i < sitesList.size(); i++) {
            Site site = sitesList.get(i);
            DetailedStatisticsItem item = new DetailedStatisticsItem();
            item.setName(site.getName());
            item.setUrl(site.getUrl());
            int pages = site.getPages().size();
            int lemmas = site.getLemmas().size();
            String status = site.getStatus();
            LocalDateTime statusTime = site.getStatusTime();
            String lastError = site.getLastError();
            item.setPages(pages);
            item.setLemmas(lemmas);
            item.setStatus(status);
            item.setError(lastError);
            item.setStatusTime(statusTime);
            total.setPages(total.getPages() + pages);
            total.setLemmas(total.getLemmas() + lemmas);
            detailed.add(item);
        }

        StatisticsResponse response = new StatisticsResponse();
        StatisticsData data = new StatisticsData();
        data.setTotal(total);
        data.setDetailed(detailed);
        response.setStatistics(data);
        response.setResult(true);
        return response;
    }
}