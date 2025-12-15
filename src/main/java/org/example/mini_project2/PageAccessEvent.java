package org.example.mini_project2;

public class PageAccessEvent {
    public final int pageNumber;
    public final boolean isWriteOperation;

    public PageAccessEvent(int pageNumber, boolean isWriteOperation) {
        this.pageNumber = pageNumber;
        this.isWriteOperation = isWriteOperation;
    }
}