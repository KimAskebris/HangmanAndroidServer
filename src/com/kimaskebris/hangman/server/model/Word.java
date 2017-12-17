package com.kimaskebris.hangman.server.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Word {

    //Put a path to a file with line seperated words
    public static final String fileName = "";
    private List<String> words;

    public String getWord(){
        getAllWords();
        Random randomGenerator = new Random();
        return words.get(randomGenerator.nextInt(words.size()));

    }

    public String dash(String word){
        String dashedWord = word.replaceAll(".", "_");
        return dashedWord;
    }

    private void getAllWords() {

        words = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                words.add(line);
            }
        } catch (IOException e) {
            System.out.println("Caught IOException " + e.getMessage() );
        }
    }

}
