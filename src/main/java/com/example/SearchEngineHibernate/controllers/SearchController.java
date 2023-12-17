package com.example.SearchEngineHibernate.controllers;

import com.example.SearchEngineHibernate.dto.app.Site;
import com.example.SearchEngineHibernate.dto.search.AppError;
import com.example.SearchEngineHibernate.dto.search.SearchResult;
import com.example.SearchEngineHibernate.dto.search.SearchingResult;
import com.example.SearchEngineHibernate.services.Repositories.SiteRepository;
import com.example.SearchEngineHibernate.services.SearchSystem.AroundSearching;
import com.example.SearchEngineHibernate.services.SearchSystem.LocalSearching;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


@RestController
public class SearchController {

    @Autowired
    AppError appError;
    @Autowired
    AroundSearching aroundSearching;

    @Autowired
    LocalSearching localSearching;
    @Autowired
    SearchingResult searchingResult;

    @Autowired
    SearchResult searchResult;

    @Autowired
    private SiteRepository siteRepository;

    private boolean stop = false;

    @GetMapping("/api/search")
    public SearchingResult searchController(@RequestParam (required = false, defaultValue = "null") String query, @RequestParam (required = false) String site,
                                            @RequestParam (required = false, defaultValue = "0") int offset,
                                            @RequestParam (required = false, defaultValue = "20") int limit ) throws SQLException, IOException {

        ArrayList<SearchResult> searchResults = new ArrayList<>();
        System.out.println("Запрос " + query);
        System.out.println("Сайт " + site);
        System.out.println("Отступ " + offset);
        System.out.println("Лимит " + limit);

        searchingResult.setAppError(appError);
        searchingResult.setResult(searchResults);

        List<Site> sites = new ArrayList<>();
        Iterable<Site> iterable = siteRepository.findAll();
        iterable.forEach(sites::add);

        ArrayList<String> statuses = new ArrayList<>(sites.stream().map(a->a.getStatus()).toList());

        if (query.equals("null")) {
            appError.setResult(false);
            appError.setErrorMess("Задан пустой поисковый запрос");
        } else {
            if (site != null) {

                Site thisSite = sites.stream().filter(x -> x.getUrl().equals(site.replace("https://", ""))).toList().get(0);
                System.out.println("САЙТ ПОИСКА "+ thisSite.getUrl());
                System.out.println("САЙТ СТАТУС "+ thisSite.getStatus());

                if (thisSite.getStatus().equals("INDEXING")) {
                    appError.setResult(false);
                    appError.setErrorMess("Запущена индексация сайта. Повторите запрос через 10 минут");
                } else if (thisSite.getStatus().equals("FAILED")) {
                    appError.setResult(false);
                    appError.setErrorMess("Ошибка индексации. Запустите индексацию.");
                } else if (thisSite.getStatus().equals("INDEXED")) {

                    searchResults = localSearching.searchSystem(query, site);
                    searchingResult.setResult(searchResults);
                    //System.out.println("Результат поиска -" + searchResults.get(0).getSnipper());
                    //System.out.println("Запрос -" + query);
                    //System.out.println("Сайт - " + site);

                }
            } else {

                if (statuses.contains("INDEXING")) {
                    appError.setResult(false);
                    appError.setErrorMess("Запущена индексация. Повторите запрос через 10 минут");
                } else if (statuses.contains("FAILED")) {
                    appError.setResult(false);
                    appError.setErrorMess("Ошибка индексации. Запустите индексацию.");
                } else if (!statuses.contains("INDEXING")&& !statuses.contains("FAILED")) {
                    searchResults = aroundSearching.searchSystem(query);
                    searchingResult.setResult(searchResults);
                }
            }
        }

        System.out.println("ИТОГОВЫЙ " + searchingResult.getAppError().getErrorMess());
        //System.out.println("ИТОГ2 " + searchingResult.getResult());
        return searchingResult;

    }
}
