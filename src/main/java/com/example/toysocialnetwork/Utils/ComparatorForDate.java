package com.example.toysocialnetwork.Utils;

import com.example.toysocialnetwork.Domain.PublicEvent;

import java.util.Comparator;

public class ComparatorForDate implements Comparator<PublicEvent> {
    @Override
    public int compare(PublicEvent o1, PublicEvent o2) {
        return o1.getEventDate().isAfter(o2.getEventDate()) ? -1 : o1.getEventDate().equals(o2.getEventDate()) ? 0 : 1 ;
    }
}
