package com.example.SearchEngineHibernate.dto.statistics;

import com.example.SearchEngineHibernate.dto.app.App;
import com.example.SearchEngineHibernate.dto.app.Site;
import com.example.SearchEngineHibernate.services.Repositories.SiteRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Component
@ComponentScan
public class SitesList {

    @Autowired
    private SiteRepository siteRepository;

    public List<Site> sitesList() throws SQLException, ParseException {
        List<Site> sites = new ArrayList<>();
        Iterable<Site> iterable = siteRepository.findAll();
        iterable.forEach(sites::add);
        return sites;

        }
    }



