package com.github.sergdelft.j2graph.graph;

public class Vocabulary {
    private static int COUNTER = 0;

    private final String word;
    private final int id;

    public Vocabulary(String word) {
        this.id = ++COUNTER;
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    public int getId() {
        return id;
    }
}
