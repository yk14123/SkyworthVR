package com.chinafocus.hvrskyworthvr.model.bean;

import java.util.Objects;

public class TagHolder {
    private String mClassName;
    private int startIndex;

    public TagHolder(String className, int startIndex) {
        mClassName = className;
        this.startIndex = startIndex;
    }

    public String getClassName() {
        return mClassName;
    }

    public int getStartIndex() {
        return startIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagHolder tagHolder = (TagHolder) o;
        return Objects.equals(mClassName, tagHolder.mClassName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mClassName);
    }
}