package com.example.SearchEngineHibernate.controllers;

import com.example.SearchEngineHibernate.dto.app.Site;
import com.example.SearchEngineHibernate.dto.search.AppError;
import com.example.SearchEngineHibernate.services.DataBaseCreation.ComboIndexingMethods;
import com.example.SearchEngineHibernate.services.Repositories.SiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RestController
public class IndexingController {

    @Autowired
    AppError appError;

    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private ComboIndexingMethods comboIndexingMethods;

    private boolean stop = false;

    public ArrayList<String> statusesReturn (){
        List<Site> sites = new ArrayList<>();
        Iterable<Site> iterable = siteRepository.findAll();
        iterable.forEach(sites::add);

        return new ArrayList (sites.stream().map(a-> a.getStatus()).collect(Collectors.toList()));
    }

    public void indexing() throws IOException, SQLException {
        if (!stop) {
            comboIndexingMethods.allPagesIndexing();
        }
        else return;
    }

    @GetMapping("api/startIndexing")
    public AppError startIndexingController() throws SQLException, IOException {

        if (!statusesReturn().contains("INDEXING")) {
            indexing();
        } else {
        appError.setResult(false);
        appError.setErrorMess("Индексация уже запущена");
    }
        return appError;

   }

    @GetMapping("api/stopIndexing")
    public AppError stopIndexingController() throws SQLException, IOException {

        ArrayList<String> statuses = new ArrayList<>();

        if (statusesReturn().contains("INDEXING")) {
            stop=true;
            indexing();
        } else {
            appError.setResult(false);
            appError.setErrorMess("Индексация не запущена");
        }
        return appError;

    }

}
