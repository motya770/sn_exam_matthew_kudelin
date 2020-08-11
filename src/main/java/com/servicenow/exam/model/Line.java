package com.servicenow.exam.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@EqualsAndHashCode
@ToString
@Data
public class Line implements Comparable<Line>{
    String[] content;

    @EqualsAndHashCode.Exclude
    boolean isInGroup;

    @Override
    public int compareTo(Line o){
        try {
            SimpleDateFormat format  =new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

            Date date1=format.parse(content[0] + " " + content[1]);
            Date date2=format.parse(o.getContent()[0] + " " + o.getContent()[1]);

            return date1.compareTo(date2);
        } catch (ParseException e) {
           log.error("", e);
        }

        return 0;
    }
}
