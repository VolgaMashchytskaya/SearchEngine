package com.example.SearchEngineHibernate.services.SearchSystem;

import com.example.SearchEngineHibernate.dto.search.SearchResult;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;

@Component
public class SearchingArrayCreating {

    public ArrayList<SearchResult> sortedFinalSearchResult(ArrayList<SearchResult> finalSearchResult) {

        float rankSumm = finalSearchResult.stream().map(a -> a.getRelevance()).reduce((float) 0.0, (a, b) -> a + b);

        Collections.sort(finalSearchResult, (o1, o2) -> Float.compare(o1.getRelevance() / rankSumm, o2.getRelevance() / rankSumm));
        return finalSearchResult;
    }
}

