package com.example.SearchEngineHibernate.dto.app;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "site")
public class
Site {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column
    private String status;
    @Column
    private LocalDateTime statusTime;
    @Column
    private String lastError;
    @Column
    private String url;
    @Column
    private String name;

    @OneToMany(mappedBy = "site",fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Page> pages;

    @OneToMany(mappedBy = "site",fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Lemma> lemmas;

    public Site() {
    }

    public Site(int id, String status, LocalDateTime statusTime, String lastError, String url, String name) {
        this.id = id;
        this.status = status;
        this.statusTime = statusTime;
        this.lastError = lastError;
        this.url = url;
        this.name = name;
        pages = new ArrayList<>();
        lemmas = new ArrayList<>();

    }

}




