package com.example.SearchEngineHibernate.dto.search;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
@Getter
@Setter
public class AppError {
    @Value("true")
    private boolean result;
    @Value("null")
    private String errorMess;

}
