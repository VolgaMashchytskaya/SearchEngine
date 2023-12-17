package com.example.SearchEngineHibernate.services.DataBaseCreation;

import com.example.SearchEngineHibernate.dto.app.Site;
import com.example.SearchEngineHibernate.services.Repositories.SiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ComboIndexingMethods {

    @Autowired
    SearchIndexTableCreating searchIndexTableCreating;

    @Autowired
    LemmaTableCreating lemmaTableCreating;

    @Autowired
    PageTableCreating pageTableCreating;

    @Autowired
    SiteRepository siteRepository;


    public void allPagesIndexing() throws SQLException, IOException {

        pageTableCreating.pageTableCreation();
        lemmaTableCreating.lemmaTableCreating();
        searchIndexTableCreating.indexTableCreating();
    }

    public void soloPageIndexing() throws SQLException, IOException {

    }

}