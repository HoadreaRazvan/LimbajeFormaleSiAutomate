import java.util.List;
import java.util.Set;

class Grammar {
    public Set<Terminal> terminals;
    public Set<NonTerminal> nonTerminals;
    public List<Production> productions;
    public NonTerminal startSymbol;

    public Grammar(Set<Terminal> terminals, Set<NonTerminal> nonTerminals,
                   List<Production> productions, NonTerminal startSymbol) {
        this.terminals = terminals;
        this.nonTerminals = nonTerminals;
        this.productions = productions;
        this.startSymbol = startSymbol;
    }
}
