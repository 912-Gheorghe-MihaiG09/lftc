package org.example;

class SymbolTable {
    @Override
    public String toString() {
        return "SymbolTable{" +
                hashTable +
                '}';
    }

    private final HashTable<String, Integer> hashTable;

    private Integer codeValue = 0;

    public SymbolTable() {
        hashTable = new HashTable<>();
    }

    // Add a symbol to the symbol table
    public Integer add(String symbol) {
        if (symbol == null) {
            throw new IllegalArgumentException("Symbol cannot be null");
        }

        if(this.contains(symbol)) {
            return this.hashTable.get(symbol);
        }

        codeValue++;
        hashTable.add(symbol, codeValue);
        return this.hashTable.get(symbol);
    }

    // Search for a symbol in the symbol table and return its value
    public Integer search(String symbol) {
        if (symbol == null) {
            throw new IllegalArgumentException("Symbol cannot be null");
        }


        return hashTable.get(symbol);
    }

    // Check if a symbol is in the symbol table
    public boolean contains(String symbol) {
        if (symbol == null) {
            throw new IllegalArgumentException("Symbol cannot be null");
        }

        return hashTable.get(symbol) != null;
    }
}
