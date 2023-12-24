package org.example;

import java.util.*;

class LL1Parser {
    private final Map<String, Set<String>> firstSet = new HashMap<>();
    private final Map<String, Set<String>> followSet = new HashMap<>();
    private final Map<String, Map<String, String>> parsingTable = new HashMap<>();

    private final CFGrammar grammar;

    // Add this constructor in the LL1Parser class
    public LL1Parser(CFGrammar grammar){
        this.grammar = grammar;
        computeFirstSet();
        computeFollowSets();
    }

    private void computeFollowSets() {
        for(String symbol : grammar.getNonTerminals()){
            followSet.put(symbol, follow(symbol));
        }
    }

    private Set<String> follow(String symbol){
        Set<String> follow = new HashSet<>();

        if(symbol.equals(grammar.getStartingSymbol())){
            follow.add("$");
        }

        Set<String> result = new HashSet<>();

        for(String key : grammar.getProductions().keySet()){
            for(List<String> production : grammar.getProductions().get(key)){
                if(production.contains(symbol)){
                    List<String> rhs = production;
                    while(rhs.contains(symbol)){
                        int indexOfSymbol = rhs.indexOf(symbol);
                        rhs = rhs.subList(indexOfSymbol + 1, rhs.size());
                        if(!rhs.isEmpty()){
                            result = computeFirstForList(rhs);
                            if(result.contains(CFGrammar.Epsilon)){
                                result.remove(CFGrammar.Epsilon);
                                Set<String> ansNew = follow(key);
                                if(!ansNew.isEmpty()){
                                    result.addAll(ansNew);
                                }
                            }
                        } else{
                            if(!symbol.equals(key)){
                                result = follow(key);
                            }
                        }
                    }
                }
            }
        }

        follow.addAll(result);

        return follow;
    }

    private Set<String> computeFirstForList(List<String> symbols) {
        Set<String> firstSetForList = new HashSet<>();

        for (String symbol : symbols) {
            if (grammar.getNonTerminals().contains(symbol)) {
                firstSetForList.addAll(firstSet.get(symbol));
                if (!firstSet.get(symbol).contains("eps")) {
                    // If ε is not in FIRST(X), stop adding symbols
                    break;
                }
            } else {
                firstSetForList.add(symbol);
                break;
            }
        }

        return firstSetForList;
    }


    public void computeFirstSet() {
        for (String nonTerminal : grammar.getNonTerminals()) {
            computeFirst(nonTerminal);
        }
    }

    private void computeFirst(String symbol) {
        if (firstSet.containsKey(symbol)) {
            return;
        }

        Set<String> first = new HashSet<>();

        for (List<String> production : grammar.getProductions().get(symbol)) {
            if(grammar.getNonTerminals().contains(production.get(0))) {
                computeFirst(production.get(0));
                first.addAll(firstSet.get(production.get(0)));
            } else {
                first.add(production.get(0));
            }
        }

        firstSet.put(symbol, first);
    }


    public void printFirstFollowParsingTable() {
        System.out.println("FIRST set:");
        for (String nonTerminal : firstSet.keySet()) {
            System.out.println(nonTerminal + ": " + firstSet.get(nonTerminal));
        }

        System.out.println("\nFOLLOW set:");
        for (String nonTerminal : followSet.keySet()) {
            System.out.println(nonTerminal + ": " + followSet.get(nonTerminal));
        }
//
//        System.out.println("\nLL(1) Parsing Table:");
//        for (String nonTerminal : parsingTable.keySet()) {
//            for (String terminal : parsingTable.get(nonTerminal).keySet()) {
//                System.out.println("M[" + nonTerminal + ", " + terminal + "] = " + parsingTable.get(nonTerminal).get(terminal));
//            }
//        }
    }
}

