package com.servicenow.exam.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

@EqualsAndHashCode
@Data
public class LineGroup {

    @EqualsAndHashCode.Exclude
    boolean searchIsAdded = false;

    private List<Line> lines = new LinkedList<>();

    @EqualsAndHashCode.Exclude
    private HashSet<String> changedWords = new HashSet<>();
}
