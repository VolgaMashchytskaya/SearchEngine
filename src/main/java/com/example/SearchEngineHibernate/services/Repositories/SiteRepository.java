package com.example.SearchEngineHibernate.services.Repositories;

import com.example.SearchEngineHibernate.dto.app.Site;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface SiteRepository extends CrudRepository<Site, Integer> {

    final Logger logger = Logger.getLogger(SiteRepository.class);

}