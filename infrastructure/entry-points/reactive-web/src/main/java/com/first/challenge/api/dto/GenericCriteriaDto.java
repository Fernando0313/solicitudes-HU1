package com.first.challenge.api.dto;

import com.first.challenge.model.criteria.SearchCriteria;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.HashMap;
import java.util.Map;

public record GenericCriteriaDto(Map<String,String> filters,
                                 int page,
                                 int size) {

    public SearchCriteria toDomain() {
        return SearchCriteria.of(filters, page, size);
    }

    /* factory desde ServerRequest */
    public static GenericCriteriaDto fromRequest(ServerRequest req) {
        Map<String,String> f = new HashMap<>();
        req.queryParams().forEach((k,v)->{
            if (!k.equals("page") && !k.equals("size")) f.put(k, v.getFirst());
        });
        int p = req.queryParam("page").map(Integer::valueOf).orElse(0);
        int s = req.queryParam("size").map(Integer::valueOf).orElse(20);
        return new GenericCriteriaDto(f, p, s);
    }
}
