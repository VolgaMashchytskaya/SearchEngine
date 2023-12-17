package com.example.SearchEngineHibernate.services.DataBaseCreation;
import com.example.SearchEngineHibernate.dto.app.Lemma;
import com.example.SearchEngineHibernate.dto.app.Site;
import com.example.SearchEngineHibernate.services.Repositories.LemmaRepository;
import com.example.SearchEngineHibernate.services.Repositories.SiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Component
public class LemmaTableCreating {

    @Autowired
    private LemmaRepository lemmaRepository;

    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private LemmaServices lemmaServices;

    public void lemmaTableCreating() {

        List<Site> sites = new ArrayList<>();
        Iterable<Site> iterable = siteRepository.findAll();
        iterable.forEach(sites::add);

        System.out.println("База лемм формируется. Ждите..");

        for (Site site : sites) {

            HashMap<String, Integer> lemmasMap = lemmaServices.lemmaCreating(site);

            for (String key : lemmasMap.keySet()) {
                Lemma lemma = new Lemma();
                lemma.setSite(site);
                lemma.setLemmaName(key);
                lemma.setFrequency(lemmasMap.get(key));
                lemmaRepository.save(lemma);
            }


        }
        System.out.println("База лемм сформирована!");
    }
}

