package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        SymbolTable indentifierSymbolTable = new SymbolTable();
        SymbolTable constantSymbolTable = new SymbolTable();

        indentifierSymbolTable.add("a");
        constantSymbolTable.add("5");
        indentifierSymbolTable.add("b");
        constantSymbolTable.add("6");
        indentifierSymbolTable.add("a");
        indentifierSymbolTable.add("a");
        constantSymbolTable.add("3");
        indentifierSymbolTable.add("b");
        indentifierSymbolTable.add("b");
        constantSymbolTable.add("6");
        indentifierSymbolTable.add("a");
        indentifierSymbolTable.add("a");
        indentifierSymbolTable.add("b");

        System.out.println(indentifierSymbolTable.toString());
        System.out.println(constantSymbolTable.toString());

//        // Define the file path
//        String reservedWordsPath = "reservedWords.txt";
//        List<String> reservedWords = new ArrayList<>();
//
//        try (BufferedReader reader = new BufferedReader(new FileReader(reservedWordsPath)) ) {
//            String line;
//
//            while ((line = reader.readLine()) != null) {
//                reservedWords.add(line);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        String programPath = "program.txt";
//
//        try (BufferedReader reader = new BufferedReader(new FileReader(programPath)) ) {
//            String line;
//
//            while ((line = reader.readLine()) != null) {
//                String[] tokens = line.split(" ");
//                for(String token : tokens){
//                    _handleToken(token);
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        // Convert the array to a List
//        List<String> tokenList = new ArrayList<>(Arrays.asList(tokens));
//
//        // Print the tokens
//        for (String token : tokenList) {
//            System.out.println(token);
//        }
    }
//
//    private void _handleToken(String token){
//        if(token.contains("$")){
//
//        }
//    }
}