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
public class LineParserService implements ILineParserService {

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
        for(int j = 0; j < lines.size(); j++){
            if(j==searchIndex){
                continue;
            }

            Line currentLine = lines.get(j);
            //if(currentLine.isInGroup()){
              //  continue;
            //}

            String[] searchLineContent = searchLine.getContent();
            String[] currentContent = currentLine.getContent();

            if(searchLineContent.length != currentContent.length){
                continue;
            }

            int counterNotSameWord=0;
            String firstWord = null;
            String secondWord = null;

            for(int i= F_START_WORDS_INDEX; i < searchLineContent.length; i++){
                if(!searchLineContent[i].equals(currentContent[i])){
                    counterNotSameWord++;
                    firstWord = searchLineContent[i];
                    secondWord = currentContent[i];
                }
                if(counterNotSameWord>1){
                    break;
                }
            }

            if(counterNotSameWord==1){
                if( lineGroup.getChangedWords().size()==0 || lineGroup.getChangedWords().contains(firstWord)) {

                    addToLineGroup(searchLine, lineGroup, j, currentLine, firstWord, secondWord);
                    //searchLine.setInGroup(true);
                    //currentLine.setInGroup(true);
                }
            }
        }

        Collections.sort(lineGroup.getLines(), new Comparator<Line>() {
            @Override
            public int compare(Line line1, Line line2) {
                try {
                    SimpleDateFormat format  =new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

                    Date date1=format.parse(line1.getContent()[0] + " " + line1.getContent()[1]);
                    Date date2=format.parse(line2.getContent()[0] + " " + line2.getContent()[1]);

                    return date1.compareTo(date2);
                } catch (ParseException e) {
                    log.error("", e);
                }
                return 0;
            }
        });

        return lineGroup;
    }

    private void addToLineGroup(Line searchLine, LineGroup lineGroup, int j, Line currentLine, String firstWord, String secondWord) {
        if (!lineGroup.isSearchIsAdded()) {
            lineGroup.getLines().add(searchLine);
        }
        lineGroup.getLines().add(currentLine);
        lineGroup.setSearchIsAdded(true);
        lineGroup.getChangedWords().add(firstWord + " " + j);
        lineGroup.getChangedWords().add(secondWord + " " + j);
    }
}
