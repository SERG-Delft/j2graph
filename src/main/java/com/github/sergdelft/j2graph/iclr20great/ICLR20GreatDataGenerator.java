package com.github.sergdelft.j2graph.iclr20great;

import com.github.sergdelft.j2graph.ast.JDT;
import com.github.sergdelft.j2graph.graph.ClassGraph;
import com.github.sergdelft.j2graph.graph.MethodGraph;
import com.github.sergdelft.j2graph.walker.GraphWalker;
import com.github.sergdelft.j2graph.walker.iclr20great.ICLR20GreatVisitor;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ThreadLocalRandom;


/**
 * Generator for ICLR20-Great data format: https://github.com/VHellendoorn/ICLR20-Great
 * <p>
 * Also generates token vocabulary for training data (no BPE version).
 */
public class ICLR20GreatDataGenerator {

    final int BUGGY_METHODS_PER_HUNDRED = 10;

    public void run() {
        try {
            iterateFiles("path/to/folder/containing/java/files/for/train", Split.TRAIN);
            iterateFiles("path/to/folder/containing/java/files/for/train/validation", Split.DEV);
            iterateFiles("path/to/folder/containing/java/files/for/test", Split.EVAL);
            System.out.println("Finished");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void iterateFiles(String path, Split split) throws IOException {
        System.out.println("Starting to preprocess files for " + split.name().toLowerCase());

        BufferedOutputStream processedDataStream = new BufferedOutputStream(new FileOutputStream(split.name().toLowerCase() + ".txt"));
        PrintWriter processedDataWriter = new PrintWriter(processedDataStream, true, StandardCharsets.UTF_8);

        BufferedOutputStream vocabStream = new BufferedOutputStream(new FileOutputStream(split.name().toLowerCase() + "_vocab.txt"));
        PrintWriter vocabWriter = new PrintWriter(vocabStream, true, StandardCharsets.UTF_8);

        Files.walk(Paths.get(path))
                .filter(Files::isRegularFile)
                .forEach(filePath -> processFile(split, processedDataWriter, vocabWriter, filePath));

        processedDataWriter.close();
        processedDataStream.close();
        vocabWriter.close();
        vocabStream.close();
    }

    private void processFile(Split split, PrintWriter processedDataWriter, PrintWriter vocabWriter, Path filePath) {
        GraphWalker graphWalker = new GraphWalker();
        try {
            String sourceCode = loadSourceCode(filePath.toString());
            ClassGraph graph = new JDT().parse(sourceCode);
            if (graph != null) {
                ICLR20GreatVisitor ICLR20GreatVisitor = new ICLR20GreatVisitor();
                graphWalker.accept(graph, ICLR20GreatVisitor);
                if (split.equals(Split.TRAIN) && !ICLR20GreatVisitor.getCorrectAndBuggyPairs().isEmpty()) {
                    saveTokensToFile(vocabWriter, graph);
                }
                for (Pair<JsonObject, JsonObject> pair : ICLR20GreatVisitor.getCorrectAndBuggyPairs()) {
                    boolean balanced = false;
                    writeData(processedDataWriter, pair, balanced);
                }
            }
        } catch (IllegalArgumentException | IOException e) {
            System.out.println("Couldn't parse code. Ignoring and continuing...");
        }


    }

    private void writeData(PrintWriter processedDataWriter, Pair<JsonObject, JsonObject> pair, boolean balanced) {
        processedDataWriter.println(pair.getLeft());
        if (balanced || ThreadLocalRandom.current().nextInt(0, 100 + 1) < BUGGY_METHODS_PER_HUNDRED) {
            processedDataWriter.println(pair.getRight());
        }
        processedDataWriter.flush();
    }

    protected String loadSourceCode(String fixture) throws IOException {
        return new String (Files.readAllBytes(Paths.get(fixture)));
    }

    private void saveTokensToFile(PrintWriter vocabWriter, ClassGraph graph) {
        for (MethodGraph methodGraph : graph.getMethods()) {
            vocabWriter.println("");
            methodGraph.getTokens().forEach(t -> vocabWriter.print(t.getTokenName() + " "));
            vocabWriter.println("");
            methodGraph.getSymbols().forEach(s -> vocabWriter.print(s.getSymbol() + " "));
            vocabWriter.println("");
            methodGraph.getVocabulary().forEach(v -> vocabWriter.print(v.getWord() + " "));
            vocabWriter.println("");
            methodGraph.getNonTerminals().forEach(nt -> vocabWriter.print(nt.getName() + " "));
            vocabWriter.flush();
        }
    }

    enum Split {
        TRAIN,
        DEV,
        EVAL
    }
}
