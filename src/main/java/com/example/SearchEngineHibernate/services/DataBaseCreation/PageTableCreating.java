package com.example.SearchEngineHibernate.services.DataBaseCreation;


import com.example.SearchEngineHibernate.dto.app.Page;
import com.example.SearchEngineHibernate.dto.app.Site;
import com.example.SearchEngineHibernate.services.Repositories.PageRepository;
import com.example.SearchEngineHibernate.services.Repositories.SiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ForkJoinPool;


@Component
public class
PageTableCreating {

    @Autowired
    private SiteParsing siteParsing;
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private SiteRepository siteRepository;

    public void pageTableCreation() throws IOException {

        List<Site> sites = new ArrayList<>();
        Iterable<Site> iterable = siteRepository.findAll();
        iterable.forEach(sites::add);

        System.out.println("Парсинг сайтов начинается. Ждите..");

        for (Site site: sites) {

            String baseUrl = "https://" + site.getUrl();


            SiteMapper siteMapper = new SiteMapper(new SiteNode(baseUrl));
            ForkJoinPool forkJoinPool = new ForkJoinPool();

            forkJoinPool.invoke(siteMapper);

            HashMap <String, String[]> siteContent = siteParsing.siteParsing(siteMapper.getAllSiteUrl());

                for (String key: siteContent.keySet()) {
                    Page page = new Page();
                    page.setCode(Integer.parseInt(siteContent.get(key)[0]));
                    page.setContent(siteContent.get(key)[1]);
                    page.setPath(key.replace(("https://" + site.getUrl()),""));
                    page.setSite(site);
                    pageRepository.save(page);
                }

        }
        System.out.println("Парсинг сайтов закончен.");
    }
}


