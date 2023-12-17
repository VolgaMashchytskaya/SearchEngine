package com.example.SearchEngineHibernate.controllers;

import com.example.SearchEngineHibernate.dto.app.App;
import com.example.SearchEngineHibernate.dto.app.Page;
import com.example.SearchEngineHibernate.dto.app.Site;
import com.example.SearchEngineHibernate.dto.search.AppError;
import com.example.SearchEngineHibernate.services.Repositories.PageRepository;
import com.example.SearchEngineHibernate.services.Repositories.SiteRepository;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


@RestController
public class PostController {

    @Autowired
    private AppError appError;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private PageRepository pageRepository;

    public PostController() throws SQLException {
    }

    @PostMapping("/api/indexPage")
    public AppError indexPageIndexingController(@RequestBody String url) throws SQLException, IOException {
        url = url.replace("url=", "").replace("%2F","/").replace("%3A",":");


        if (!siteVerification(url)) {
            appError.setResult(false);
            appError.setErrorMess("Данная страница находится за пределами сайтов, " +
                    "указанных в конфигурационном файле");
        } else {
            if (pageVerification(url)) {
                System.out.println("Обновление страницы начинается");
                System.out.println(url);
                System.out.println(pageVerification(url));
                org.jsoup.Connection.Response response = Jsoup.connect(url).userAgent("Chrome/4.0.249.0 Safari/532.5")
                        .referrer("http://www.google.com").execute();
                System.out.println("Обновление страницы продолжается");
                int statusCode = response.statusCode();
                System.out.println(statusCode);

                if (statusCode == 200) {
                    String html = Jsoup.connect(url).userAgent("Chrome/4.0.249.0 Safari/532.5")
                            .referrer("http://www.google.com").get().html().replaceAll("\\s+", " ").
                            replaceAll("'", "");

                    List<Page> pages = new ArrayList<>();
                    Iterable<Page> iterablePage = pageRepository.findAll();
                    iterablePage.forEach(pages::add);

                    String page = pageUrlExtract(url);

                    Page thisPage = pages.stream().filter(x-> x.getPath().equals(page)).toList().get(0);
                    thisPage.setContent(html);
                    pageRepository.save(thisPage);

                    System.out.println("Обновление страницы завершено успешно");

                } else {
                    appError.setResult(false);
                    appError.setErrorMess("Ошибка подключения. Попробуйте еще раз.");
                }
            } else {
                System.out.println("Индексация страницы начинается");
                org.jsoup.Connection.Response response = Jsoup.connect(url).userAgent("Chrome/4.0.249.0 Safari/532.5")
                        .referrer("http://www.google.com").execute();
                int statusCode = response.statusCode();

                if (statusCode == 200) {
                    String html = Jsoup.connect(url).userAgent("Chrome/4.0.249.0 Safari/532.5")
                            .referrer("http://www.google.com").get().html().replaceAll("\\s+", " ").
                            replaceAll("'", "");

                    String site = siteUrlExtract(url);

                    List<Site> sites = new ArrayList<>();
                    Iterable<Site> iterableSite = siteRepository.findAll();
                    iterableSite.forEach(sites::add);


                    Site thisSite = sites.stream().filter(x-> x.getName().equals(site)).toList().get(0);


                    Page page = new Page(thisSite,pageUrlExtract(url),200,html);

                    pageRepository.save(page);

                    System.out.println("Индексация страницы окончена");

                } else {
                    appError.setResult(false);
                    appError.setErrorMess("Ошибка подключения. Попробуйте еще раз.");
                }
            }

        }
        return appError;
    }


    public boolean siteVerification(String url) throws SQLException {

        List<Site> sites = new ArrayList<>();
        Iterable<Site> iterable = siteRepository.findAll();
        iterable.forEach(sites::add);

        String site = siteUrlExtract(url);
        return sites.stream().map(a->a.getName()).toList().contains(site);

    }

    public boolean pageVerification(String url) throws SQLException {

        List<Site> sites = new ArrayList<>();
        Iterable<Site> iterableSite = siteRepository.findAll();
        iterableSite.forEach(sites::add);

        List<Page> pages = new ArrayList<>();
        Iterable<Page> iterablePage = pageRepository.findAll();
        iterablePage.forEach(pages::add);

        String site = siteUrlExtract(url);
        String page = pageUrlExtract(url);

        Site thisSite = sites.stream().filter(x-> x.getName().equals(site)).toList().get(0);
        Page thisPage = pages.stream().filter(x-> x.getPath().equals(page)).toList().get(0);

        return thisSite.getPages().contains(thisPage);
    }

    public String siteUrlExtract (String url) throws SQLException {
        String siteUrl = url.replace("http://","").replace("https://","").
                replace("http://www.","").replace("https://www.","").
                replace("www.","").split("/")[0];
        return siteUrl;
    }

    public String pageUrlExtract (String url) throws SQLException {
        String siteUrl = url.replace("http://","").replace("https://","").
                replace("http:// www.","").replace("https:// www.","").
                replace("www.","").split("/")[0];

        String pageUrl = url.replace("http://","").replace("http:// www.","").
                replace("www.","").replace(siteUrl,"");

        return pageUrl;
    }
}