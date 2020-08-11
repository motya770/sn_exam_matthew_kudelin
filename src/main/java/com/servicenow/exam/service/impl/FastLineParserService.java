package com.servicenow.exam.service.impl;

import com.servicenow.exam.model.Line;
import com.servicenow.exam.model.LineGroup;
import com.servicenow.exam.service.ILineParserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class FastLineParserService implements ILineParserService {

    public static final int STARTING_CHANGING_WORD_INDEX = -1;
    public static final int FIRST_INDEX_AFTER_DATETIME = 2;


    @Value("${input.file.path}")
    Resource resourceFile;

    private static final int F_START_WORDS_INDEX = 2;

    @Override
    public void parse() {

        List<Line> lines = loadLinesFromFile();
        Set<LineGroup> lineGroups = new HashSet<>();

        for(int i=0; i<lines.size(); i++){

            Line searchLine = lines.get(i);
            LineGroup lineGroup = createLineGroup(lines, searchLine, i);
            if(lineGroup.getLines().size()>0){
                lineGroups.add(lineGroup);
            }
        }

        printLineGroups(lineGroups);
    }

    private List<Line> loadLinesFromFile(){
        List<Line> lines = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(resourceFile.getFile()))) {
            String readLine;
            while ((readLine = br.readLine()) != null) {
                String[] words = readLine.split(" ");
                Line line = new Line();
                line.setContent(words);
                lines.add(line);
            }
        }catch (Exception e){
            log.error("{}", e);
        }
        return lines;
    }

    private void printLineGroups(Set<LineGroup> lineGroups){
        System.out.println("=====");
        lineGroups.forEach(lineGroup -> {

            Collection<Line> lines = lineGroup.getLines();

            for(Line line: lines){
                String[] words = line.getContent();
                for(String word: words){
                    System.out.print(word + " ");
                }
                System.out.println();
            }

            System.out.print("The changing word was:");
            lineGroup.getChangedWords().forEach(changedWord-> System.out.print(changedWord + ", "));
            System.out.println("\n");

        });
        System.out.println("=====");
    }

    private LineGroup createLineGroup(List<Line> lines, Line searchLine, int searchIndex){

        LineGroup lineGroup = new LineGroup();
        List<Line> sameLines = new ArrayList<>();
        int changingWordIndex = STARTING_CHANGING_WORD_INDEX;

        for(int j = 0; j < lines.size(); j++){
            try {
                if (j == searchIndex) {
                    continue;
                }

                Line currentLine = lines.get(j);

                if (searchLine.getContent().length != currentLine.getContent().length) {
                    continue;
                }

                //if can use it because we walt all same lines to be in the group
                if (checkIfSame(searchLine, currentLine)) {
                    sameLines.add(currentLine);
                    continue;
                }

                //find index of one changed word in two lines - only one per sentence
                if (changingWordIndex == STARTING_CHANGING_WORD_INDEX) {
                    changingWordIndex = findChangingIndex(searchLine, currentLine);
                }

                // lines are not in the group
                if (changingWordIndex == STARTING_CHANGING_WORD_INDEX) {
                    continue;
                }

                boolean found = checkLineIsSameExceptIndex(changingWordIndex, searchLine, currentLine);

                if (!found) {
                    continue;
                }

                addToLineGroup(lineGroup, searchLine, currentLine, changingWordIndex, j);
            }catch (Exception e){
                log.error("{}", e);
            }
        }

        if(changingWordIndex>STARTING_CHANGING_WORD_INDEX){
            lineGroup.getLines().addAll(sameLines);
        }

        sortLines(lineGroup);

        return lineGroup;
    }

    private void sortLines(LineGroup lineGroup) {
        Comparator<Line> comparatorByDate =
                (Line line1, Line line2) ->
                {
                    try {
                        SimpleDateFormat format  =new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

                        Date date1=format.parse(line1.getContent()[0] + " " + line1.getContent()[1]);
                        Date date2=format.parse(line2.getContent()[0] + " " + line2.getContent()[1]);

                        int result = date1.compareTo(date2);
                        if(result!=0){
                            return result;
                        }

                        for(int i=FIRST_INDEX_AFTER_DATETIME; i<line1.getContent().length; i++){
                            result = line1.getContent()[i].compareTo(line2.getContent()[i]);
                            if(result!=-0){
                                return result;
                            }
                        }

                    } catch (ParseException e) {
                        log.error("", e);
                    }
                    return 0;
                };

        Collections.sort(lineGroup.getLines(), comparatorByDate);
    }

    private boolean checkIfSame(Line searchLine, Line currentLine){
        return Arrays.equals(searchLine.getContent(), currentLine.getContent());
    }

    private boolean checkLineIsSameExceptIndex(int changingWordIndex, Line searchLine, Line currentLine){
        for(int i = FIRST_INDEX_AFTER_DATETIME; i<searchLine.getContent().length; i++){
            if(i == changingWordIndex){
                continue;
            }

            if(!searchLine.getContent()[i].equals(currentLine.getContent()[i])){
                return false;
            }
        }

        return true;
    }

    private int findChangingIndex(Line searchLine, Line currentLine){
        int changingWordIndex = STARTING_CHANGING_WORD_INDEX;
        int counterNotSameWord=0;
        for(int i= F_START_WORDS_INDEX; i < searchLine.getContent().length; i++){
            if(!searchLine.getContent()[i].equals(currentLine.getContent()[i])){
                counterNotSameWord++;
                changingWordIndex = i;
            }
            if(counterNotSameWord > 1){
                return STARTING_CHANGING_WORD_INDEX;
            }
        }
        return changingWordIndex;
    }

    private void addToLineGroup(LineGroup lineGroup, Line searchLine, Line currentLine, int changingWordIndex, int j) {
        if (!lineGroup.isSearchIsAdded()) {
            lineGroup.getLines().add(searchLine);
        }
        lineGroup.getLines().add(currentLine);
        lineGroup.setSearchIsAdded(true);
        lineGroup.getChangedWords().add(searchLine.getContent()[changingWordIndex]);
        lineGroup.getChangedWords().add(currentLine.getContent()[changingWordIndex]);
    }
}
