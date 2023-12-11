package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    int identifierPifCode = -1;
    int constPifCode = -2;
    public static void main(String[] args) {
        SymbolTable indentifierSymbolTable = new SymbolTable();
        SymbolTable constantSymbolTable = new SymbolTable();


        // Define the file path
        String reservedWordsPath = "/Users/mihaigheorghe/IdeaProjects/lftc_symbol_table/src/main/java/org/example/token.in";
        List<String> reservedWords = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(reservedWordsPath)) ) {
            String line;

            while ((line = reader.readLine()) != null) {
                reservedWords.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> tokenList = new ArrayList<>();

        String programPath = "/Users/mihaigheorghe/IdeaProjects/lftc_symbol_table/src/main/java/org/example/program.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(programPath)) ) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(" ");
                tokenList.addAll(Arrays.asList(tokens));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Print the tokens
        for(String  token : tokenList){
            System.out.println(token);
        }
    }
}