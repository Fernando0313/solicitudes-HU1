package com.first.challenge.model.criteria;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(force = true)   // para deserialización si algún cliente lo consume
@AllArgsConstructor(staticName = "of")
public class PageResponse<T> {
    List<T> content;      // elementos de la página actual
    int currentPage;      // página que se está devollando (base 0)
    int pageSize;         // tamaño de página solicitado
    long totalElements;   // total de registros sin paginar
    int totalPages;       // cantidad de páginas que hay

    /* factory rápida desde UseCase */
    public static <T> PageResponse<T> of(List<T> content,
                                         int currentPage,
                                         int pageSize,
                                         long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);
        return PageResponse.of(content, currentPage, pageSize, totalElements, totalPages);
    }
}
