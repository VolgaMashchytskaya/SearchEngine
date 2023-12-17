package com.example.SearchEngineHibernate.services.DataBaseCreation;

import com.example.SearchEngineHibernate.dto.app.Field;
import com.example.SearchEngineHibernate.services.Repositories.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Getter
@Setter
@AllArgsConstructor
public class FieldTableCreating {

    @Autowired
    private FieldRepository fieldRepository;

    public void fieldTableCreating() {
        fieldRepository.save(new Field(1,"title","title",1.00f));
        fieldRepository.save(new Field(2,"body","body",0.80f));

    }

}
