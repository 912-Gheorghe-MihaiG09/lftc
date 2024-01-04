package org.example;

import java.util.*;
import java.util.stream.Stream;

class LL1Parser {
    private final Map<String, Set<String>> firstSet = new HashMap<>();
    private final Map<String, Set<String>> followSet = new HashMap<>();
    private final Map<String, Map<String, List<String>>> parsingTable = new HashMap<>();

    private final CFGrammar grammar;

    // Add this constructor in the LL1Parser class
    public LL1Parser(CFGrammar grammar){
        this.grammar = grammar;
        computeFirstSet();
        computeFollowSets();
        computeParsingTable();
    }



    // we left the print in order to see how the algorithm works
    public ParseTreeNode parse(List<String> input) {
        // Initialize a stack to perform parsing
        Stack<ParseTreeNode> stack = new Stack<>();
        // Add the starting symbol to the stack
        ParseTreeNode startingNode = new ParseTreeNode(grammar.getStartingSymbol());
        stack.push(startingNode);
        // Add the end-of-input marker to the input

        input = new ArrayList<>(input);
        input.add("$");

        // Start parsing
        int inputIndex = 0;
        while (!stack.isEmpty()) {
            System.out.println("stack: " + stack);

            ParseTreeNode top = stack.pop();

            System.out.println("top: " + top);

            if (grammar.getNonTerminals().contains(top.getSymbol())) {
                System.out.println("top: " + top + " is non terminal");
                // If the top of the stack is a non-terminal
                // Get the production from the parsing table
                List<String> production = parsingTable.get(top.getSymbol()).get(input.get(inputIndex));

                System.out.println("production: " + production.toString());

                if (production != null) {
                    // Push the right-hand side of the production onto the stack
                    for (int i = production.size() - 1; i >= 0; i--) {
                        if (!production.get(i).equals(CFGrammar.Epsilon)) {
                            String symbol = production.get(i);
                            System.out.println("adding symbol to stack: " + symbol);
                            ParseTreeNode childNode = new ParseTreeNode(symbol);
                            stack.push(childNode);
                            System.out.println("new stack: " + stack);
                            top.addChild(childNode);
                        }
                    }
                } else {
                    // Handle parsing error
                    System.out.println("Error: No production in the parsing table for symbol " + top +
                            " and input symbol " + input.get(inputIndex));
                    return null;
                }
            } else if (top.getSymbol().equals(input.get(inputIndex))) {
                // If the top of the stack is a terminal and matches the input symbol
                inputIndex++; // Move to the next input symbol
            } else {
                // Handle parsing error
                System.out.println("Error: Mismatch between stack symbol " + top +
                        " and input symbol " + input.get(inputIndex));
                return null;
            }
        }

        return startingNode; // Parsing successful
    }
    public void computeParsingTable(){
        for(String nonTerminal : grammar.getNonTerminals()){
            parsingTable.put(nonTerminal, new HashMap<>());

            for(List<String> prodRule : grammar.getProductions().get(nonTerminal)){
                if(prodRule.contains(grammar.Epsilon)){
                    Map<String, List<String>> map = new HashMap<>();
                    for(String terminal : followSet.get(nonTerminal)){
                        map.put(terminal, prodRule);
                    }
                    parsingTable.get(nonTerminal).putAll(map);
                } else {
                    Map<String, List<String>> map = new HashMap<>();
                    if(grammar.getTerminals().contains(prodRule.get(0))){
                        map.put(prodRule.get(0), prodRule);
                    } else {
                        for (String terminal : computeFirstForList(prodRule)) {
                            if (!terminal.equals(grammar.Epsilon)) {
                                map.put(terminal, prodRule);
                            }
                        }
                    }
                    parsingTable.get(nonTerminal).putAll(map);
                }
            }
        }

        System.out.println(parsingTable);
    }

    // Computes the FOLLOW sets for all non-terminals in the grammar
    private void computeFollowSets() {
        for(String symbol : grammar.getNonTerminals()){
            followSet.put(symbol, follow(symbol));
        }
    }

    // Computes the FOLLOW set for a specific non-terminal symbol
    private Set<String> follow(String symbol){
        Set<String> follow = new HashSet<>();

        // If the symbol is the starting symbol, add '$' to its FOLLOW set
        if(symbol.equals(grammar.getStartingSymbol())){
            follow.add("$");
        }

        Set<String> result = new HashSet<>();

        // Iterate through all productions in the grammar
        for(String key : grammar.getProductions().keySet()){
            for(List<String> production : grammar.getProductions().get(key)){
                // Check if the current production contains the target symbol
                if(production.contains(symbol)){
                    List<String> rhs = production;
                    // Process occurrences of the target symbol in the production
                    while(rhs.contains(symbol)){
                        int indexOfSymbol = rhs.indexOf(symbol);
                        rhs = rhs.subList(indexOfSymbol + 1, rhs.size());
                        if(!rhs.isEmpty()){
                            // Compute FIRST set for the remaining symbols in the production
                            result = computeFirstForList(rhs);

                            // If epsilon is in FIRST, add FOLLOW of the left-hand side non-terminal
                            if(result.contains(CFGrammar.Epsilon)){
                                result.remove(CFGrammar.Epsilon);
                                Set<String> ansNew = follow(key);
                                if(!ansNew.isEmpty()){
                                    result.addAll(ansNew);
                                }
                            }
                        } else{
                            // If the target symbol is at the end, add FOLLOW of the left-hand side non-terminal
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

    // Computes the FIRST set for a list of symbols
    private Set<String> computeFirstForList(List<String> symbols) {
        Set<String> firstSetForList = new HashSet<>();

        for (String symbol : symbols) {
            if (grammar.getNonTerminals().contains(symbol)) {
                firstSetForList.addAll(firstSet.get(symbol));
                if (!firstSet.get(symbol).contains(CFGrammar.Epsilon)) {
                    // If epsilon is not in FIRST(X), stop adding symbols
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

    // Computes the FIRST set for a specific non-terminal symbol
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

        System.out.println();
        printParsingTable();
    }

    public void printParsingTable() {
        List<List<String>> table = new ArrayList<>();

        Set<String> terminalExtended = grammar.getTerminals();
        terminalExtended.add("$");
        terminalExtended.remove(grammar.Epsilon);

        List<String> header = new ArrayList<>();
        header.add(" ");
        header.addAll(terminalExtended);

        table.add(header);



        for(String key : parsingTable.keySet()){
            List<String> row = new ArrayList<>();
            row.add(key);
            for(String terminal : terminalExtended){
                if(parsingTable.get(key).containsKey(terminal)){
                    row.add(parsingTable.get(key).get(terminal).toString());
                }
                else {
                    row.add("");
                }
            }
            table.add(row);
        }

        // Calculate appropriate Length of each column by looking at width of data in each column.
        Map<Integer, Integer> columnLengths = new HashMap<>();
        table.forEach(a -> Stream.iterate(0, (i -> i < a.size()), (i -> ++i)).forEach(i -> {
            columnLengths.putIfAbsent(i, 0);
            if (columnLengths.get(i) < a.get(i).length()) {
                columnLengths.put(i, a.get(i).length());
            }
        }));

         // Prepare format String
        final StringBuilder formatString = new StringBuilder("");
        columnLengths.entrySet().stream().forEach(e -> formatString.append("| %-" + e.getValue() + "s "));
        formatString.append("|\n");

        for(List<String> rowTable : table) {
            System.out.printf(formatString.toString(), rowTable.toArray());
        }
    }
}



class ParseTreeNode {
    private String symbol;
    private List<ParseTreeNode> children;

    public ParseTreeNode(String symbol) {
        this.symbol = symbol;
        this.children = new ArrayList<>();
    }

    public String getSymbol() {
        return symbol;
    }

    public List<ParseTreeNode> getChildren() {
        return children;
    }

    public void addChild(ParseTreeNode child) {
        children.add(child);
    }

    @Override
    public String toString() {
        return symbol;
    }
}

// this class helps us to print the parsing tree,
// the parsing tree must be read from the bottom to the top
class ParseTreePrinter {
    public static void printTree(ParseTreeNode root) {
        printTree(root, 0);
    }

    private static void printTree(ParseTreeNode node, int depth) {
        // Print the current node
        printIndent(depth);
        System.out.println(node);

        // Recursively print the children
        for (ParseTreeNode child : node.getChildren()) {
            printTree(child, depth + 1);
        }
    }

    private static void printIndent(int depth) {
        for (int i = 0; i < depth; i++) {
            System.out.print("  "); // You can adjust the number of spaces for indentation
        }
    }
}
