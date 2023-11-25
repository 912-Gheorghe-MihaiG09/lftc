package org.example;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class MyScanner {

    private final Map<String, Integer> keywords;
    private final Map<String, Integer> operators;
    private final Map<String, Integer> separators;

    private final String file;
    private final SymbolTable indentifierSymbolTable;
    private final SymbolTable constantSymbolTable;
    private final List<Pair<String, Integer>> pifTable;
    private final List<String> errors;

    public MyScanner(String file, String tokensPath) throws FileNotFoundException {
        this.keywords = new HashMap<>();
        this.operators = new HashMap<>();
        this.separators = new HashMap<>();
        this.file = file;
        this.indentifierSymbolTable = new SymbolTable();
        this.constantSymbolTable = new SymbolTable();
        this.pifTable = new ArrayList<>();
        this.errors = new ArrayList<>();
        readTokenFile(tokensPath);
    }

    public void scan() throws FileNotFoundException {
        String[] fileContent = readFile().split("\n");

        // go line by line
        for(int j = 0; j < fileContent.length; j++) {
            String line = fileContent[j].trim();
            // if the line is not empty
            if(!line.isEmpty()) {
                StringBuilder word = new StringBuilder();
                for (int i = 0; i < line.length(); i++) {
                    char character = line.charAt(i);
                    // check for constant strings
                    if(character == '"'){
                        // build the word with the chars after the first '"'
                        do{
                            character = line.charAt(i);
                            word.append(character);
                            i++;
                            // stop when you reach the second " or when you get ot the end of the line
                        } while(i < line.length() && line.charAt(i) != '"');
                        // if the end was not reached => reached the second " => add " to the word
                        if(i < line.length()){
                            character = line.charAt(i);
                            word.append(character);
                        }

                    // check for separators, if you reached a separator or ' ' => a word was finished
                    } else if (isSeparator(String.valueOf(character)) || character == ' ' ) {

                        // handle the finished word
                        handleInternalTables(word.toString(), j);

                        // is a separator was reached  add it to pif
                        if(character != ' ') {
                            if (isSeparator(String.valueOf(character))) {
                                pifTable.add(new Pair<>(String.valueOf(separators.get(String.valueOf(character))), -1));
                            }
                        }

                        // reset the word
                        word = new StringBuilder();
                    } else{
                        // if not a separator or a " continue building the word
                        word.append(character);
                    }
                }
                // if the line is finished handle what is left
                handleInternalTables(word.toString(), j);
            }
        }

        // print the conclusion
        System.out.println(scanMessage());
    }

    private void handleInternalTables(String word, int lineNumber){
        if(!word.isEmpty()) {
            // check every possibility and add the values to pif
            if (isReservedWord(word)) {
                pifTable.add(new Pair<>(String.valueOf(keywords.get(word)), -1));
            } else if (isOperator(word)) {
                pifTable.add(new Pair<>(String.valueOf(operators.get(word)), -1));
            } else if (isIdentifier(word)) {
                Integer id = indentifierSymbolTable.add(word);
                pifTable.add(new Pair<>(word, id));
            } else if (isConstant(word)) {
                Integer id = constantSymbolTable.add(word);
                pifTable.add(new Pair<>(word, id));
            } else {
                // if nothing matches add an error
                errors.add("invalid token: " + word + " at line: " + (lineNumber + 1));
            }
        }
    }

    private boolean isReservedWord(String word) {
        return keywords.containsKey(word);
    }

    private boolean isOperator(String word) {
        return operators.containsKey(word);
    }

    private boolean isSeparator(String word) {
        return separators.containsKey(word);
    }

    private boolean isIdentifier(String word) {
        return Pattern.matches("^\\$[a-zA-Z][a-zA-Z0-9_]*|\\$_[a-zA-Z][a-zA-Z0-9_]*", word);
    }

    private boolean isConstant(String word) {
        boolean isInteger = Pattern.matches("^[0-9]+|-[0-9]+|\\+[0-9]+$", word);
        boolean isBool = Pattern.matches("^true|false$", word);
        boolean isString = Pattern.matches("^\"[a-zA-Z0-9_\\s]*\"", word);
        return isInteger || isBool || isString;
    }

    public String getPIF() {
        return pifTable.toString();
    }

    public String getIdentifierSymbolTable() {
        return this.indentifierSymbolTable.toString();
    }

    public String getConstantSymbolTable() {
        return this.constantSymbolTable.toString();
    }

    public String scanMessage() {
        if (errors.isEmpty()) {
            return "Lexically correct";
        } else {
            return String.join("\n", errors);
        }
    }

    private String readFile() throws FileNotFoundException {
        // get the file content as a string
        StringBuilder fileContent = new StringBuilder();
        Scanner scanner = new Scanner(new File(this.file));
        while(scanner.hasNextLine()){
            fileContent.append(scanner.nextLine()).append("\n");
        }

        return fileContent.toString().replace("\t", "");
    }

    private void readTokenFile(String tokensPath) {
        // get and store tokens from the file
        try {
            BufferedReader reader = new BufferedReader(new FileReader(tokensPath));
            String line;
            int index = 1;

            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("//")) {
                    if(index <= 12) {
                        keywords.put(line, index);
                    } else if(index <= 20) {
                        separators.put(line, index);
                    } else {
                        operators.put(line, index);
                    }
                }
                index++;
            }
            reader.close();
            System.out.println("keywords: " + keywords);
            System.out.println("separators: " + separators);
            System.out.println("operators: " + operators);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        String inputFilePath = "src/main/java/org/example/p2.txt";
        String tokensFilePath = "src/main/java/org/example/token.in";
        MyScanner scanner = new MyScanner(inputFilePath, tokensFilePath);
        scanner.scan();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("PIF.out"))) {
            writer.write(scanner.getPIF());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(scanner.getIdentifierSymbolTable());
        System.out.println(scanner.getConstantSymbolTable());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("ST.out"))) {
            writer.write("const table: " + scanner.getConstantSymbolTable() + "\n");
            writer.write("identifier table:" + scanner.getIdentifierSymbolTable());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
