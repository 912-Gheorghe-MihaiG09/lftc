package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class CFGrammar {


    private final String ELEMENT_SEPARATOR = "&";

    private final String SEPARATOR_OR_TRANSITION = "\\|";
    private final String TRANSITION_CONCATENATION = " ";
    private final String SEPARATOR_LEFT_RIGHT_HAND_SIDE = ":=";

    public static final String Epsilon = "eps";

    // LR(0)
    private Set<String> nonTerminals;
    private Set<String> terminals;
    private Map<String, Set<List<String>>> productions;
    private String startingSymbol;


    private void processProduction(String production) {
        String[] leftAndRightHandSide = production.split(this.SEPARATOR_LEFT_RIGHT_HAND_SIDE);
        String splitL = leftAndRightHandSide[0];
        String[] splitR= leftAndRightHandSide[1].split(this.SEPARATOR_OR_TRANSITION);

        this.productions.putIfAbsent(splitL, new HashSet<>());
        for (String s : splitR) {
            this.productions.get(splitL).add(Arrays.stream(s.split(this.TRANSITION_CONCATENATION)).collect(Collectors.toList()));
        }
    }

    private void loadFromFile(String filePath) {
        try (Scanner scanner = new Scanner(new File(filePath))) {
            this.nonTerminals = new HashSet<>(List.of(scanner.nextLine().split(this.ELEMENT_SEPARATOR)));
            this.terminals = new HashSet<>(List.of(scanner.nextLine().split(this.ELEMENT_SEPARATOR)));
            this.startingSymbol = scanner.nextLine();

            this.productions = new HashMap<>();
            while (scanner.hasNextLine()) {
                this.processProduction(scanner.nextLine());
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public CFGrammar(String filePath) {
        this.loadFromFile(filePath);
    }

    public Set<String> getNonTerminals() {
        return this.nonTerminals;
    }

    public Set<String> getTerminals() {
        return this.terminals;
    }

    public Map<String, Set<List<String>>> getProductions() {
        return this.productions;
    }

    public String getStartingSymbol() {
        return this.startingSymbol;
    }
}