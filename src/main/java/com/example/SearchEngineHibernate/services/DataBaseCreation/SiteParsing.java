package com.example.SearchEngineHibernate.services.DataBaseCreation;

import com.example.SearchEngineHibernate.services.Repositories.PageRepository;
import org.jsoup.Connection;
import java.io.*;
import java.util.HashMap;
import java.util.Set;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class SiteParsing {

    static final Logger logger =  LoggerFactory.getLogger(SiteParsing.class);

    public HashMap<String, String[]> siteParsing(Set<String> setUrl) throws IOException {
        HashMap<String, String[]> siteContent = new HashMap<>();

            logger.info("Начали процесс!");

            for (String url : setUrl) {
                String[] array = new String[2];

                try {
                    Connection.Response response = Jsoup.connect(url).userAgent("Chrome/4.0.249.0 Safari/532.5")
                            .referrer("http://www.google.com").execute();
                    int statusCode = response.statusCode();

                                     if (statusCode == 200) {
                        String html = Jsoup.connect(url).userAgent("Chrome/4.0.249.0 Safari/532.5")
                                .referrer("http://www.google.com").get().html().replaceAll("\\s+", " ").replaceAll("'", "");

                        array[0] = String.valueOf(200);
                        array[1] = html;

                        siteContent.put(url, array);
                    } else {
                        array[0] = String.valueOf(statusCode);
                        array[1] = "error";

                        siteContent.put(url, array);
                    }
                } catch (HttpStatusException e) {
                    e.printStackTrace();
                     logger.error("Ошибка скачивания");
                    array[0] = String.valueOf(e.getStatusCode());
                    array[1] = "error";
                    siteContent.put(url, array);

                }
                finally {
                    continue;
                }
            }
            return siteContent;
    }


}


