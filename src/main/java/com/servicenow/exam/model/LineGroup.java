package com.servicenow.exam.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.*;

@EqualsAndHashCode
@Data
public class LineGroup {

    @EqualsAndHashCode.Exclude
    boolean searchIsAdded = false;

    private List<Line> lines = new LinkedList<>();

    @EqualsAndHashCode.Exclude
    private HashSet<String> changedWords = new HashSet<>();
}
