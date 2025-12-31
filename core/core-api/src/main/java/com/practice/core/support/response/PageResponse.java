package com.practice.core.support.response;

import java.util.List;

public class PageResponse<T> {
    private final List<T> content;
    private final boolean hasNext;

    public PageResponse(List<T> content, boolean hasNext) {
        this.content = content;
        this.hasNext = hasNext;
    }

    public List<T> getContent() {
        return content;
    }

    public boolean isHasNext() {
        return hasNext;
    }
}
