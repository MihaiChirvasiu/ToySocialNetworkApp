package com.example.toysocialnetwork.Paging;

import java.util.stream.Stream;

public interface Page<E> {
    /**
     * Gets a page
     * @return a page
     */
    Pageable getPageable();

    Pageable nextPageable();

    /**
     * Getter for the contents on a page
     * @return
     */
    Stream<E> getContent();


}
