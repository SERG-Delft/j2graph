package com.github.sergdelft.j2graph.walker.json;

import com.github.sergdelft.j2graph.graph.NonTerminal;
import com.github.sergdelft.j2graph.graph.Symbol;
import com.github.sergdelft.j2graph.graph.Token;
import com.github.sergdelft.j2graph.graph.Vocabulary;
import com.github.sergdelft.j2graph.walker.Walker;
import com.google.gson.*;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Visitor to help generate data for ICLR20-Great data format: https://github.com/VHellendoorn/ICLR20-Great
 * <p>
 * The Visitor also mutates binary expressions (<,<=,>,>=)
 */
public class JsonVisitor implements Walker {

    private final ArrayList<ImmutablePair<JsonObject, JsonObject>> jsonPairs = new ArrayList<>();
    private int counter = 0;
    private JsonObject correctJson;
    private JsonObject buggyJson;
    private JsonArray edges = new JsonArray();
    private ArrayList<String> tokens = new ArrayList<>();
    private ArrayList<Integer> tokenIds = new ArrayList<>();

    private void addDummyData() {
        correctJson.add("repair_candidates", new Gson().toJsonTree(new int[]{0}));
    }

    @Override
    public void className(String className) {
    }

    @Override
    public void method(String methodName, NonTerminal root) {
        correctJson = new JsonObject();
        buggyJson = null;
        addDummyData();
        markForBugginess(correctJson, false, 0);

        root.setId(counter);
        tokenIds.add(root.getId());
        tokens.add(root.getName());
    }

    public ArrayList<ImmutablePair<JsonObject, JsonObject>> getCorrectAndBuggyPairs() {
        return jsonPairs;
    }

    private void markForBugginess(JsonObject objToMutate, boolean isBug, int errorLocation) {
        changeJson(objToMutate, "has_bug", isBug ? "true" : "false");
        changeJson(objToMutate, "bug_kind", isBug ? 1 : 0);
        changeJson(objToMutate, "bug_kind_name", isBug ? "OFF_BY_ONE" : "NONE");
        changeJson(objToMutate, "error_location", errorLocation);
        changeJson(objToMutate, "repair_targets", isBug ? new Gson().toJsonTree(new int[]{0}) : new Gson().toJsonTree(new int[]{}));
    }

    @Override
    public void nonTerminal(NonTerminal nonTerminal) {
        counter += 1;
        nonTerminal.setId(counter);
        tokens.add(nonTerminal.getName());
        tokenIds.add(counter);
    }

    @Override
    public void token(Token token) {
        counter += 1;
        token.setId(counter);
        tokens.add(token.getTokenName());
        tokenIds.add(counter);
    }

    @Override
    public void symbol(Symbol symbol) {
        counter += 1;
        symbol.setId(counter);
        tokens.add(symbol.getSymbol());
        tokenIds.add(counter);
    }

    @Override
    public void vocabulary(Vocabulary vocabulary) {
        counter += 1;
        vocabulary.setId(counter);
        tokens.add(vocabulary.getWord());
        tokenIds.add(counter);
    }

    @Override
    public void nextToken(Token t1, Token t2) {
        if (tokenIds.contains(t1.getId()) && tokenIds.contains(t2.getId())) {
            addEdge(t1.getId(), t2.getId(), EdgeType.NEXT_TOKEN);
        }
    }

    @Override
    public void child(NonTerminal t1, Token t2) {
        if (tokenIds.contains(t1.getId()) && tokenIds.contains(t2.getId())) {
            addEdge(t1.getId(), t2.getId(), EdgeType.CHILD);
        }
    }

    @Override
    public void child(NonTerminal t1, NonTerminal t2) {
        if (tokenIds.contains(t1.getId()) && tokenIds.contains(t2.getId())) {
            addEdge(t1.getId(), t2.getId(), EdgeType.CHILD);
        }
    }

    @Override
    public void occurrenceOf(Token t1, Symbol t2) {
        if (tokenIds.contains(t1.getId()) && tokenIds.contains(t2.getId())) {
            addEdge(t1.getId(), t2.getId(), EdgeType.OCCURENCE_OF);
        }
    }

    @Override
    public void subtokenOf(Vocabulary t1, Token t2) {
        if (tokenIds.contains(t1.getId()) && tokenIds.contains(t2.getId())) {
            addEdge(t1.getId(), t2.getId(), EdgeType.SUBTOKEN_OF);
        }
    }

    @Override
    public void returnsTo(NonTerminal t1, Token t2) {
        if (tokenIds.contains(t1.getId()) && tokenIds.contains(t2.getId())) {
            addEdge(t1.getId(), t2.getId(), EdgeType.RETURNS_TO);
        }
    }

    @Override
    public void nextLexicalUse(Token t1, Token t2) {
        if (tokenIds.contains(t1.getId()) && tokenIds.contains(t2.getId())) {
            addEdge(t1.getId(), t2.getId(), EdgeType.NEXT_LEXICAL_USE);
        }
    }

    @Override
    public void assignedFrom(Token t1, NonTerminal t2) {
        if (tokenIds.contains(t1.getId()) && tokenIds.contains(t2.getId())) {
            addEdge(t1.getId(), t2.getId(), EdgeType.ASSIGNED_FROM);
        }
    }

    @Override
    public void endMethod(String methodName, NonTerminal root) {
        changeJson(correctJson, "source_tokens", new Gson().toJsonTree(tokens));
        changeJson(correctJson, "edges", new Gson().toJsonTree(edges));
        buggyJson = findBinaryExpressionAndMutateJson(correctJson.deepCopy());
        if (buggyJson != null && edges.size() != 0) {
            ImmutablePair<JsonObject, JsonObject> pair = new ImmutablePair<>(correctJson, buggyJson);
            jsonPairs.add(pair);
        }
        counter = 0;
        edges = new JsonArray();
        tokens = new ArrayList<>();
        correctJson = new JsonObject();
        buggyJson = new JsonObject();
        tokenIds = new ArrayList<>();
    }

    @Override
    public void end() {
    }

    private JsonObject findBinaryExpressionAndMutateJson(JsonObject objToMutate) {
        JsonElement tokens = objToMutate.get("source_tokens");
        ArrayList<Integer> mutatableTokens = new ArrayList<>();
        int tokenIndex = 0;
        for (JsonElement token : tokens.getAsJsonArray()) {
            if (Arrays.asList("<", "<=", ">", ">=").contains(token.getAsString())) {
                mutatableTokens.add(tokenIndex);
            }
            tokenIndex++;
        }
        if (!mutatableTokens.isEmpty()) {
            Random randomizer = new Random();
            Integer randomIndex = mutatableTokens.get(randomizer.nextInt(mutatableTokens.size()));
            JsonArray allTokens = tokens.getAsJsonArray();
            JsonElement token = allTokens.get(randomIndex);
            String binaryExpression = token.getAsString();
            switch (binaryExpression) {
                case "<":
                    token = new JsonPrimitive("<=");
                    allTokens.set(randomIndex, token);
                    break;
                case "<=":
                    token = new JsonPrimitive("<");
                    allTokens.set(randomIndex, token);
                    break;
                case ">":
                    token = new JsonPrimitive(">=");
                    allTokens.set(randomIndex, token);
                    break;
                case ">=":
                    token = new JsonPrimitive(">");
                    allTokens.set(randomIndex, token);
                    break;
            }
            markForBugginess(objToMutate, true, randomIndex);
            return objToMutate;
        } else {
            return null;
        }
    }

    private void changeJson(JsonObject objToMutate, String property, JsonElement element) {
        objToMutate.remove(property);
        objToMutate.add(property, element);
    }

    private void changeJson(JsonObject objToMutate, String property, String value) {
        objToMutate.remove(property);
        objToMutate.addProperty(property, value);
    }

    private void changeJson(JsonObject objToMutate, String property, int value) {
        objToMutate.remove(property);
        objToMutate.addProperty(property, value);
    }

    private void addEdge(int idFrom, int idTo, EdgeType edgeType) {
        JsonArray edge = new JsonArray();
        edge.add(idFrom);
        edge.add(idTo);
        edge.add(edgeType.getValue());
        edge.add(edgeType.toString());
        edges.add(edge);
    }

    enum EdgeType {
        NEXT_TOKEN(0),
        CHILD(1),
        OCCURENCE_OF(2),
        SUBTOKEN_OF(3),
        RETURNS_TO(4),
        NEXT_LEXICAL_USE(5),
        ASSIGNED_FROM(6);

        private final int value;

        EdgeType(int v) {
            value = v;
        }

        public int getValue() {
            return value;
        }
    }
}