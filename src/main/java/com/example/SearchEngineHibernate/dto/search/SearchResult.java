package com.example.SearchEngineHibernate.dto.search;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class SearchResult {

    private String url;
    private String title;
    private String snipper;
    private Float relevance;
}
