package com.practice.core.support;

import java.util.List;

public class Page<T> {
    private final List<T> content;
    private final boolean hasNext;

    public Page(List<T> content, boolean hasNext) {
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
