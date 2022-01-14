package com.example.toysocialnetwork.Paging;

public interface Pageable {
    /**
     * Gets the page number
     * @return the number of the page
     */
    int getPageNumber();

    /**
     * Gets the page size
     * @return the size of the page
     */
    int getPageSize();
}
