package com.first.challenge.model.criteria;

import lombok.Value;

import java.util.Map;


@Value(staticConstructor = "of")
public class SearchCriteria {
    Map<String, String> filters;   // operador:valor  (ej. amount=gte:1000000)
    int page;
    int size;
}