package com.practice.core.support;

import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Getter
public class OffsetLimit {
    private final int offset;
    private final int limit;

    public OffsetLimit(int offset, int limit) {
        this.offset = offset;
        this.limit = limit;
    }

    public Pageable toPageable() {
        return PageRequest.of(offset , limit);
    }
}
