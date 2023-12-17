package com.example.SearchEngineHibernate.dto.app;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Map;

@Getter
@Setter
@Component
@EnableConfigurationProperties
@PropertySource("/application.yaml")
@ConfigurationProperties(prefix = "indexing-settings")
public class App {

    private Map<String,String> sites;

}


