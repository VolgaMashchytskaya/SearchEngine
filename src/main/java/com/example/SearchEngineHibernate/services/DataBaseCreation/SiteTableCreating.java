package com.example.SearchEngineHibernate.services.DataBaseCreation;
import com.example.SearchEngineHibernate.dto.app.App;
import com.example.SearchEngineHibernate.dto.app.Site;
import com.example.SearchEngineHibernate.services.Repositories.SiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Component
public class SiteTableCreating {
    @Autowired
    private App app;

    @Autowired
    private SiteRepository siteRepository;

    public void siteTableCreating() {

        LocalDateTime today = LocalDateTime.now();
        for (String key : app.getSites().keySet()) {
            Site site = new Site();
            site.setStatus("FAILED");
            site.setStatusTime(today);
            site.setLastError(null);
            site.setUrl(key);
            site.setName(app.getSites().get(key));
            siteRepository.save(site);

        }
    }
}