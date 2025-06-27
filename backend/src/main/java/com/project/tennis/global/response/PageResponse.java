package com.project.tennis.global.response;

import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

@Builder
public record PageResponse<T>(
        List<T> items,
        int page,
        int size,
        int totalPages,
        int totalElements
) {
    public static <T, R> PageResponse<R> from(Page<T> page, Function<T, R> mapper) {
        List<R> items = page.getContent().stream()
                .map(mapper)
                .toList();

        return PageResponse.<R>builder()
                .items(items)
                .page(page.getNumber() + 1)
                .size(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements((int) page.getTotalElements())
                .build();
    }
}
