package com.example.SearchEngineHibernate.services.DataBaseCreation;

import com.example.SearchEngineHibernate.dto.app.*;
import com.example.SearchEngineHibernate.services.Repositories.FieldRepository;
import com.example.SearchEngineHibernate.services.Repositories.SearchIndexRepository;
import com.example.SearchEngineHibernate.services.Repositories.SiteRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class Indexation {
    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private SearchIndexRepository searchIndexRepository;


    @Autowired
    private LemmaServices lemmaServices;

    @Autowired
    private FieldRepository fieldRepository;

    static final Logger logger = LoggerFactory.getLogger(SiteParsing.class);

    public void indexCreating() {

        List<Site> sites = new ArrayList<>();
        Iterable<Site> iterable1 = siteRepository.findAll();
        iterable1.forEach(sites::add);

        List<Field> fields = new ArrayList<>();
        Iterable<Field> iterable2 = fieldRepository.findAll();
        iterable2.forEach(fields::add);

        Map<String, Float> fieldWeight = fields.stream().collect(Collectors.toMap(Field::getSelector, Field::getWeight));



        for (Site site : sites) {

            try {

            site.setStatus("INDEXING");

                HashMap<Integer, String> lemmaMap = new HashMap<>(
                        site.getLemmas().stream().collect(Collectors.toMap(Lemma::getId,Lemma::getLemmaName)));

                List<Page> pageList = site.getPages().stream().filter(a->a.getCode()==200).toList();

                for (Page page: pageList) {
                    int idPage = page.getId();
                    String textPage = page.getContent();
                    Document document = Jsoup.parse(textPage);
                    String title = document.title();
                    String body = textPage.replaceAll(title, "");

                    for (Lemma lemma : site.getLemmas()) {
                        String lemmaItem = lemma.getLemmaName();

                        int countLemmaInTitle = (int) lemmaServices.textModify(title).stream().filter(x -> x.equals(lemmaItem)).count();
                        int countLemmaInBody = (int) lemmaServices.textModify(body).stream().filter(x -> x.equals(lemmaItem)).count();

                        float rank = (float) (countLemmaInTitle * fieldWeight.get("title") + countLemmaInBody * fieldWeight.get("body"));
                        if (rank != 0) {
                            SearchIndex searchIndex = new SearchIndex();
                            searchIndex.setLemma(lemma);
                            searchIndex.setPage(page);
                            searchIndex.setRating(rank);

                            searchIndexRepository.save(searchIndex);

                        }
                    }

                    System.out.println("Индексация страницы закончена");

                    System.out.println("Индексация продолжается...");

                     }
            }catch (java.lang.Throwable e){
                site.setStatus("FAILED");
            }

            site.setStatus("INDEXED");
            System.out.println("Индексация сайта окончена...");
    }
    }
}

