package com.example.SearchEngineHibernate.services.Repositories;


import com.example.SearchEngineHibernate.dto.app.Field;
import org.apache.log4j.Logger;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FieldRepository extends CrudRepository<Field, Integer> {

    final Logger logger = Logger.getLogger(FieldRepository.class);
}
