package com.example.SearchEngineHibernate.dto.app;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "field")
@NoArgsConstructor
public class Field {

    @Id
    private int id;

    @Column
    private String name;
    @Column
    private String selector;
    @Column
    private Float weight;

    public Field(int id, String name, String selector, Float weight) {
        this.id = id;
        this.name = name;
        this.selector = selector;
        this.weight = weight;
    }
}




