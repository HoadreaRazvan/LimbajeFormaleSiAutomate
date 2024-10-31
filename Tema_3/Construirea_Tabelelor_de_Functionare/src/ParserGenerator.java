import java.util.*;
import java.io.*;

class ParserGenerator {
    private Grammar grammar;
    private Map<Symbol, Set<Terminal>> firstSets;
    private Map<NonTerminal, Set<Terminal>> followSets;
    private List<State> states;
    private Map<State, Map<Symbol, State>> transitions;
    private Map<State, Map<Terminal, String>> actionTable;
    private Map<State, Map<NonTerminal, State>> gotoTable;

    public ParserGenerator(Grammar grammar) {
        this.grammar = grammar;
        firstSets = new HashMap<>();
        followSets = new HashMap<>();
        states = new ArrayList<>();
        transitions = new HashMap<>();
        actionTable = new HashMap<>();
        gotoTable = new HashMap<>();
    }

    public void generateParsingTable() {
        computeFirstSets();
        computeFollowSets();
        constructCanonicalCollection();
        buildParsingTable();
    }

    private void computeFirstSets() {
        for (Symbol symbol : grammar.terminals) {
            Set<Terminal> set = new HashSet<>();
            set.add((Terminal) symbol);
            firstSets.put(symbol, set);
        }
        for (Symbol symbol : grammar.nonTerminals) {
            firstSets.put(symbol, new HashSet<>());
        }
    }

    private void computeFollowSets() {
        for (NonTerminal nt : grammar.nonTerminals) {
            followSets.put(nt, new HashSet<>());
        }
        followSets.get(grammar.startSymbol).add(new Terminal("$"));
    }

    private void constructCanonicalCollection() {
        NonTerminal augmentedStart = new NonTerminal(grammar.startSymbol.name + "'");
        Production augmentedProduction = new Production(augmentedStart, Arrays.asList(grammar.startSymbol));
        grammar.nonTerminals.add(augmentedStart);
        grammar.productions.add(0, augmentedProduction);

        Item startItem = new Item(augmentedProduction, 0, new Terminal("$"));
        Set<Item> startClosure = closure(new HashSet<>(Arrays.asList(startItem)));
        State startState = new State(startClosure);
        states.add(startState);

        Queue<State> queue = new LinkedList<>();
        queue.add(startState);

        while (!queue.isEmpty()) {
            State state = queue.poll();
            Map<Symbol, Set<Item>> symbolItemMap = new HashMap<>();
            for (Item item : state.items) {
                if (item.dotPosition < item.production.rhs.size()) {
                    Symbol symbolAfterDot = item.production.rhs.get(item.dotPosition);
                    symbolItemMap.computeIfAbsent(symbolAfterDot, k -> new HashSet<>()).add(item);
                }
            }

            for (Map.Entry<Symbol, Set<Item>> entry : symbolItemMap.entrySet()) {
                Symbol X = entry.getKey();
                Set<Item> itemsForSymbol = entry.getValue();
                Set<Item> gotoSet = new HashSet<>();
                for (Item item : itemsForSymbol) {
                    Item newItem = new Item(item.production, item.dotPosition + 1, item.lookahead);
                    gotoSet.add(newItem);
                }
                State gotoState = new State(closure(gotoSet));

                if (!states.contains(gotoState)) {
                    states.add(gotoState);
                    queue.add(gotoState);
                } else {
                    for (State s : states) {
                        if (s.equals(gotoState)) {
                            gotoState = s;
                            break;
                        }
                    }
                }

                transitions.computeIfAbsent(state, k -> new HashMap<>()).put(X, gotoState);
            }
        }
    }

    private void buildParsingTable() {
        for (State state : states) {
            actionTable.put(state, new HashMap<>());
            gotoTable.put(state, new HashMap<>());

            for (Item item : state.items) {
                if (item.dotPosition < item.production.rhs.size()) {
                    Symbol symbol = item.production.rhs.get(item.dotPosition);
                    if (symbol instanceof Terminal) {
                        State nextState = transitions.get(state).get(symbol);
                        if (nextState != null) {
                            actionTable.get(state).put((Terminal) symbol, "d" + states.indexOf(nextState));
                        }
                    } else if (symbol instanceof NonTerminal) {
                        State nextState = transitions.get(state).get(symbol);
                        if (nextState != null) {
                            gotoTable.get(state).put((NonTerminal) symbol, nextState);
                        }
                    }
                } else {
                    if (item.production.lhs.name.equals(grammar.startSymbol.name + "'")) {
                        actionTable.get(state).put(new Terminal("$"), "acc");
                    } else {
                        int productionIndex = grammar.productions.indexOf(item.production);
                        String action = "r" + productionIndex;
                        actionTable.get(state).put(item.lookahead, action);
                    }
                }
            }
        }
    }

    private Set<Item> closure(Set<Item> items) {
        Set<Item> closureSet = new HashSet<>(items);
        boolean added;
        do {
            added = false;
            Set<Item> newItems = new HashSet<>();
            for (Item item : closureSet) {
                if (item.dotPosition < item.production.rhs.size()) {
                    Symbol symbolAfterDot = item.production.rhs.get(item.dotPosition);
                    if (symbolAfterDot instanceof NonTerminal) {
                        NonTerminal B = (NonTerminal) symbolAfterDot;

                        List<Symbol> beta = new ArrayList<>(item.production.rhs.subList(item.dotPosition + 1, item.production.rhs.size()));
                        beta.add(item.lookahead);
                        Set<Terminal> lookaheads = computeFirst(beta);

                        for (Production p : grammar.productions) {
                            if (p.lhs.equals(B)) {
                                for (Terminal lookahead : lookaheads) {
                                    Item newItem = new Item(p, 0, lookahead);
                                    if (!closureSet.contains(newItem)) {
                                        newItems.add(newItem);
                                        added = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            closureSet.addAll(newItems);
        } while (added);
        return closureSet;
    }

    private Set<Terminal> computeFirst(List<Symbol> symbols) {
        Set<Terminal> result = new HashSet<>();
        for (Symbol symbol : symbols) {
            Set<Terminal> firstSet = firstSets.get(symbol);
            result.addAll(firstSet);
        }
        return result;
    }

    public void printParsingTable() {
        Set<Terminal> actionSymbols = new HashSet<>();
        Set<NonTerminal> gotoSymbols = new HashSet<>();

        for (Map<Terminal, String> actionRow : actionTable.values()) {
            actionSymbols.addAll(actionRow.keySet());
        }

        for (Map<NonTerminal, State> gotoRow : gotoTable.values()) {
            gotoSymbols.addAll(gotoRow.keySet());
        }

        List<Terminal> actionSymbolList = new ArrayList<>(actionSymbols);
        List<NonTerminal> gotoSymbolList = new ArrayList<>(gotoSymbols);

        actionSymbolList.sort(Comparator.comparing(s -> s.name));
        gotoSymbolList.sort(Comparator.comparing(s -> s.name));

        System.out.println("TABLE ACTION:");

        System.out.print(String.format("%-8s", "State"));
        for (Terminal t : actionSymbolList) {
            System.out.print(String.format("%-8s", t.name));
        }
        System.out.println();
        for (int i = 0; i < states.size(); i++) {
            State state = states.get(i);
            System.out.print(String.format("%-8s", i));
            Map<Terminal, String> actions = actionTable.getOrDefault(state, new HashMap<>());
            for (Terminal t : actionSymbolList) {
                String action = actions.getOrDefault(t, "");
                System.out.print(String.format("%-8s", action));
            }
            System.out.println();
        }

        System.out.println("\nTABLE GOTO:");
        System.out.print(String.format("%-8s", "State"));
        for (NonTerminal nt : gotoSymbolList) {
            System.out.print(String.format("%-8s", nt.name));
        }
        System.out.println();

        for (int i = 0; i < states.size(); i++) {
            State state = states.get(i);
            System.out.print(String.format("%-8s", i));
            Map<NonTerminal, State> gotos = gotoTable.getOrDefault(state, new HashMap<>());
            for (NonTerminal nt : gotoSymbolList) {
                State gotoState = gotos.get(nt);
                String value = (gotoState != null) ? String.valueOf(states.indexOf(gotoState)) : "";
                System.out.print(String.format("%-8s", value));
            }
            System.out.println();
        }
    }


    public void printParsingTableToFile(String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            Set<Terminal> actionSymbols = new HashSet<>();
            Set<NonTerminal> gotoSymbols = new HashSet<>();

            for (Map<Terminal, String> actionRow : actionTable.values()) {
                actionSymbols.addAll(actionRow.keySet());
            }

            for (Map<NonTerminal, State> gotoRow : gotoTable.values()) {
                gotoSymbols.addAll(gotoRow.keySet());
            }

            List<Terminal> actionSymbolList = new ArrayList<>(actionSymbols);
            List<NonTerminal> gotoSymbolList = new ArrayList<>(gotoSymbols);

            actionSymbolList.sort(Comparator.comparing(s -> s.name));
            gotoSymbolList.sort(Comparator.comparing(s -> s.name));

            //writer.write("TABLE ACTION:\n");

            //writer.write(String.format("%-8s", "State"));
//            for (Terminal t : actionSymbolList) {
//                writer.write(String.format("%-8s", t.name));
//            }


            writer.write("Action Table: "+states.size()+"\n");
            for (int i = 0; i < states.size(); i++) {
                State state = states.get(i);
                //writer.write(String.format("%-8s", i));
                Map<Terminal, String> actions = actionTable.getOrDefault(state, new HashMap<>());
                for (Terminal t : actionSymbolList) {
                    String action = actions.getOrDefault(t, "-");
                    writer.write(String.format("%-8s", action));
                }
                writer.write("\n");
            }

            //writer.write("\nTABLE GOTO:\n");
//            writer.write(String.format("%-8s", "State"));
//            for (NonTerminal nt : gotoSymbolList) {
//                writer.write(String.format("%-8s", nt.name));
//            }
            writer.write("Go To Table: "+states.size()+"\n");

            for (int i = 0; i < states.size(); i++) {
                State state = states.get(i);
                //writer.write(String.format("%-8s", i));
                Map<NonTerminal, State> gotos = gotoTable.getOrDefault(state, new HashMap<>());
                for (NonTerminal nt : gotoSymbolList) {
                    State gotoState = gotos.get(nt);
                    String value = (gotoState != null) ? String.valueOf(states.indexOf(gotoState)) : "-";
                    writer.write(String.format("%-8s", value));
                }
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
