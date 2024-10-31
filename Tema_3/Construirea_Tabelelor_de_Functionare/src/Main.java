import java.util.*;

public class Main {
    public static void main(String[] args) {

        Terminal id = new Terminal("id");
        Terminal plus = new Terminal("+");
        Terminal star = new Terminal("*");
        Terminal openParen = new Terminal("(");
        Terminal closeParen = new Terminal(")");
        Terminal dollar = new Terminal("$");
        Terminal difference = new Terminal("-");

        NonTerminal E = new NonTerminal("E");
        NonTerminal T = new NonTerminal("T");
        NonTerminal F = new NonTerminal("F");

        List<Production> productions = new ArrayList<>();
        // E -> E + T
        productions.add(new Production(E, Arrays.asList(E, plus, T)));
        // E -> T
        productions.add(new Production(E, Arrays.asList(T)));
        // T -> T * F
        productions.add(new Production(T, Arrays.asList(T, star, F)));
        // T -> F
        productions.add(new Production(T, Arrays.asList(F)));
        // F -> ( E )
        productions.add(new Production(F, Arrays.asList(openParen, E, closeParen)));
        // F -> id
        productions.add(new Production(F, Arrays.asList(id)));
        // E -> E - T
        productions.add(new Production(E, Arrays.asList(E , difference, T)));



        Set<Terminal> terminals = new HashSet<>(Arrays.asList(id, plus, star, openParen, closeParen, dollar,difference));
        Set<NonTerminal> nonTerminals = new HashSet<>(Arrays.asList(E, T, F));

        Grammar grammar = new Grammar(terminals, nonTerminals, productions, E);

        ParserGenerator parserGenerator = new ParserGenerator(grammar);
        parserGenerator.generateParsingTable();
        parserGenerator.printParsingTable();

        parserGenerator.printParsingTableToFile("C:\\Users\\hoadr\\OneDrive\\Desktop\\File.txt");
    }
}
