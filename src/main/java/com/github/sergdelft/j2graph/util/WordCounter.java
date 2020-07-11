package com.github.sergdelft.j2graph.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class WordCounter {

    public static Collection<String> breakString(String word) {
        if(word.length() == 1)
            return Arrays.asList(word);

        int current = 0;
        List<String> words = new ArrayList<>();

        for(int i = 1; i < word.length(); i++) {
            if(word.charAt(i) == '_' || Character.isUpperCase(word.charAt(i))) {
                String wordToAdd = word.substring(current, i);
                words.add(wordToAdd);
                current = i + (word.charAt(i) == '_' ? 1 : 0);
            }
        }
        String remainingWord = word.substring(current);
        words.add(remainingWord);

        return words.stream().map(w -> w.toLowerCase()).collect(Collectors.toList());
    }
}
