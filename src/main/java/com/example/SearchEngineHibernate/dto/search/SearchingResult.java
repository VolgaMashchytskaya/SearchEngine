package com.example.SearchEngineHibernate.dto.search;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Getter
@Setter
@Component
public class SearchingResult {

    ArrayList<SearchResult> result ;
    AppError appError;

}
