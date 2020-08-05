package com.github.sergdelft.j2graph;

import com.github.sergdelft.j2graph.ast.JDT;
import com.github.sergdelft.j2graph.graph.*;
import com.github.sergdelft.j2graph.walker.GraphWalker;
import com.github.sergdelft.j2graph.walker.json.JsonVisitor;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.tuple.Pair;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class DataGenerator {

    enum Split {
        TRAIN,
        DEV,
        EVAL
    }

    protected String loadFixture(String fixture) {
        try {
            return new String (Files.readAllBytes(Paths.get(fixture)));
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {
        try {
            iterateFiles("C:\\Users\\Kasutaja\\DATASET\\duplicated\\java-small\\training", Split.TRAIN);
            iterateFiles("C:\\Users\\Kasutaja\\DATASET\\duplicated\\java-small\\validation", Split.DEV);
            iterateFiles("C:\\Users\\Kasutaja\\DATASET\\duplicated\\java-small\\test", Split.EVAL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void iterateFiles(String path, Split split) throws IOException {
        System.out.println("Starting to preprocess files for " + split.name().toLowerCase());
        GraphWalker graphWalker = new GraphWalker();
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(split.name().toLowerCase() + ".txt"));
        PrintWriter writer = new PrintWriter(out, true, StandardCharsets.UTF_8);
        HashSet<Token> tokens = new HashSet<>();
        HashSet<Symbol> symbols = new HashSet<>();
        HashSet<Vocabulary> vocabularies = new HashSet<>();

        List<Path> directoryListing = Files.walk(Paths.get(path))
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());

        if (!directoryListing.isEmpty()) {
            for (Path filePath : directoryListing) {
                processFile(split, graphWalker, writer, tokens, symbols, vocabularies, filePath);
            }
            if (split.equals(Split.TRAIN)) {
                writeTokensToFile(split, writer, tokens, symbols, vocabularies);
            }
        } else {
            System.out.println(path + " is not a directory!");
        }
    }

    private void processFile(Split split, GraphWalker out, PrintWriter writer, HashSet<Token> tokens, HashSet<Symbol> symbols, HashSet<Vocabulary> vocabularies, Path filePath) {
        String sourceCode = loadFixture(filePath.toString());
        ClassGraph graph = new JDT().parse(sourceCode);
        if (graph != null) {
            if (split.equals(Split.TRAIN)) {
                saveTokens(tokens, symbols, vocabularies, graph);
            }
            JsonVisitor jsonVisitor = new JsonVisitor();
            out.accept(graph, jsonVisitor);
            for (Pair<JsonObject, JsonObject> pair : jsonVisitor.getCorrectAndBuggyPairs()) {
                writer.println(pair.getLeft());
                writer.println(pair.getRight());
                writer.flush();
            }
        }
    }

    private void writeTokensToFile(Split split, PrintWriter writer, HashSet<Token> tokens, HashSet<Symbol> symbols, HashSet<Vocabulary> vocabularies) throws IOException {
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(split.name().toLowerCase() + "_vocab.txt"));
        PrintWriter vocabWriter = new PrintWriter(out, true, StandardCharsets.UTF_8);
        vocabWriter.println("");
        for (Token token : tokens) {
            vocabWriter.print(token.getTokenName() + " ");
            writer.flush();
        }
        vocabWriter.println("");
        for (Symbol symbol : symbols) {
            vocabWriter.print(symbol.getSymbol() + " ");
            writer.flush();
        }
        vocabWriter.println("");
        for (Vocabulary vocabulary: vocabularies) {
            vocabWriter.print(vocabulary.getWord() + " ");
            writer.flush();
        }
        writer.close();
    }

    private void saveTokens(HashSet<Token> tokens, HashSet<Symbol> symbols, HashSet<Vocabulary> vocabularies, ClassGraph graph) {
        for (MethodGraph methodGraph : graph.getMethods()) {
            tokens.addAll(methodGraph.getTokens());
            symbols.addAll(methodGraph.getSymbols());
            vocabularies.addAll(methodGraph.getVocabulary());
        }
    }
}
