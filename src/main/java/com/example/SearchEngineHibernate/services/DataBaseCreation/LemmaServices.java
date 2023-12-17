package com.example.SearchEngineHibernate.services.DataBaseCreation;

import com.example.SearchEngineHibernate.dto.app.Page;
import com.example.SearchEngineHibernate.dto.app.Site;
import com.example.SearchEngineHibernate.services.Repositories.PageRepository;
import com.example.SearchEngineHibernate.services.Repositories.SiteRepository;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
@Component
public class LemmaServices {

    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private PageRepository pageRepository;

    LuceneMorphology luceneMorphology = new RussianLuceneMorphology();

    static final Logger logger =  LoggerFactory.getLogger(SiteParsing.class);

    public LemmaServices() throws IOException {
    }

    public HashMap<String, Integer> lemmaCreating(Site site) {

        HashMap<String, Integer > lemmasMap = new HashMap<>();

        try {
                    List<Page> pages = site.getPages().stream().filter(p -> p.getCode()==200).collect(Collectors.toList());

                     for (Page page : pages) {
                         String content = page.getContent();

                         List<String> textModify = textModify(content);

                         for (String lemma : textModify) {
                             int lemmaCount = 0;
                             if (!lemmasMap.containsKey(lemma)) {
                                 lemmaCount = 1;
                                 lemmasMap.put(lemma, lemmaCount);
                             } else {
                                 lemmaCount = lemmasMap.get(lemma) + 1;
                                 lemmasMap.put(lemma, lemmaCount);
                             }
                         }
                     }

        } catch (Exception e) {
            e.printStackTrace();
            e.getMessage();
            logger.error("Ошибка чтения лемм");

        }return lemmasMap;
    }
    public List<String> textModify(String textForModyfy) {
        String textModify = Jsoup.parse(textForModyfy).toString().toLowerCase().replaceAll("[\\s<>]", " ").
                replaceAll("[^а-я\\s+\\,\\.]", "").
                replaceAll("\\+","").replaceFirst("^[^а-я]+", "").
                replaceAll("[<>]", ",").replaceAll("\\s+", ",").
                replaceAll("\\.+", ",").replaceAll(",+", ",");
        List<String> textModifyArray = List.of(textModify.split(",")).stream().filter(x -> !isParticles(x))
        .map(x -> luceneMorphology.getNormalForms(x).get(0)).collect(Collectors.toList());

        return textModifyArray;

   }

    public boolean isParticles(String word) {
        boolean isParticle = false;
        List<String> wordBaseForms = luceneMorphology.getMorphInfo(word);
        if (wordBaseForms.toString().contains("МЕЖД") | wordBaseForms.toString().contains("ПРЕДЛ") | wordBaseForms.toString().contains("СОЮЗ")) {
            isParticle = true;
        }
        return isParticle;
    }

}
