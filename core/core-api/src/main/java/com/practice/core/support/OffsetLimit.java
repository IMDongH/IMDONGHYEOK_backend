package com.practice.core.support;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class OffsetLimit {
    private final int offset;
    private final int limit;

    public OffsetLimit(int offset, int limit) {
        this.offset = offset;
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }

    public Pageable toPageable() {
        return PageRequest.of(offset / limit, limit);
    }
}
