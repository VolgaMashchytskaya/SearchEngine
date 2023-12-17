package com.example.SearchEngineHibernate.services.DataBaseCreation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.SQLException;

@Component
public class SearchIndexTableCreating {

    @Autowired
    private Indexation indexation;

    public void indexTableCreating() throws SQLException, IOException {

        indexation.indexCreating();
    }
}
