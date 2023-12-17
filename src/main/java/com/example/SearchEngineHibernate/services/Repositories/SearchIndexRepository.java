package com.example.SearchEngineHibernate.services.Repositories;

import com.example.SearchEngineHibernate.dto.app.SearchIndex;
import org.apache.log4j.Logger;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface SearchIndexRepository extends CrudRepository<SearchIndex, Integer> {

    final Logger logger = Logger.getLogger(SearchIndexRepository.class);

}
