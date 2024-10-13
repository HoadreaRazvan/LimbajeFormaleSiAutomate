import java.util.*;

public class LR1PushDownAutomaton {
    private static String[][] actionTable;
    private static String[][] gotoTable;

    private Stack<Pair<String, Integer>> stack;

    public LR1PushDownAutomaton() {
        this.stack = new Stack<>();
        this.initializeTables();
    }

    private void initializeTables() {
        this.actionTable = new String[][] {
                {"d5", "", "", "d4", "", ""},
                {"", "d6", "", "", "", "acc"},
                {"", "r2", "d7", "", "r2", "r2"},
                {"", "r4", "r4", "", "", "r4"},
                {"d5", "", "", "d4", "", ""},
                {"", "r6", "r6", "", "r6", "r6"},
                {"d5", "", "", "d4", "", ""},
                {"d5", "", "", "d4", "", ""},
                {"", "d6", "", "", "d11", ""},
                {"", "r1", "d7", "", "r1", "r1"},
                {"", "r3", "r3", "", "r3", "r3"},
                {"", "r5", "r5", "", "r5", "r5"}
        };

        this.gotoTable = new String[][] {
                {"1", "2", "3"},
                {"", "", ""},
                {"", "", ""},
                {"", "", ""},
                {"8", "2", "3"},
                {"", "", ""},
                {"", "9", "3"},
                {"", "", "10"},
                {"", "", ""},
                {"", "", ""},
                {"", "", ""},
                {"", "", ""}
        };
    }

    private int getActionColumn(char symbol) {
        switch (symbol) {
            case 'a': return 0;
            case '+': return 1;
            case '*': return 2;
            case '(': return 3;
            case ')': return 4;
            case '$': return 5;
            default: return -1;
        }
    }

    private int getGoto(int state, char nonTerminal) {
        int col = nonTerminal == 'E' ? 0 : (nonTerminal == 'T' ? 1 : 2);
        return Integer.parseInt(this.gotoTable[state][col]);
    }

    private String getAction(int state, char symbol) {
        int col = this.getActionColumn(symbol);
        if (col == -1 || state >= this.actionTable.length)
            return null;
        return this.actionTable[state][col];
    }

    private void popStack(int count) {
        for (int i = 0; i < count; i++)
            this.stack.pop();
    }

    private void printStack(int sizePrintStack) {
        System.out.print("Stiva "+sizePrintStack+" : ");
        for (int i = 0; i < this.stack.size(); i++) {
            Pair<String, Integer> item = this.stack.get(i);
            System.out.print("(" + item.getKey() + ", " + item.getValue() + ") ");
        }
    }

    public void parseInput(String input) {
        input = input + "$";
        String inputDisplay=input,action;
        int sizePrintStack = 0,index = 0,state,newState,production;
        char symbol;
        this.stack.push(new Pair<>("$", 0));

        while (index < input.length()) {
            state = this.stack.peek().getValue();
            symbol = input.charAt(index);
            action = this.getAction(state, symbol);

            printStack(++sizePrintStack);
            System.out.println("\t" + inputDisplay.substring(index) + "\t"  + action);

            if (action == null || action.equals("")) {
                System.out.println("Eroare de analiza.");
                return;
            }

            if (action.startsWith("d")) {
                newState = Integer.parseInt(action.substring(1));
                this.stack.push(new Pair<>(String.valueOf(symbol), newState));
                index++;
            } else if (action.startsWith("r")) {
                production = Integer.parseInt(action.substring(1));
                reduce(production);
            } else if (action.equals("acc")) {
                System.out.println("Sirul de intrare este corect.");
                return;
            }
        }
        System.out.println("Sirul de intrare este eronat.");
    }

    private void reduce(int production) {
        switch (production) {
            case 1: // E -> E + T
                this.popStack(3);
                this.stack.push(new Pair<>("E", this.getGoto(this.stack.peek().getValue(), 'E')));
                break;
            case 2: // E -> T
                this.popStack(1);
                this.stack.push(new Pair<>("E", this.getGoto(this.stack.peek().getValue(), 'E')));
                break;
            case 3: // T -> T * F
                this.popStack(3);
                this.stack.push(new Pair<>("T", this.getGoto(this.stack.peek().getValue(), 'T')));
                break;
            case 4: // T -> F
                this.popStack(1);
                this.stack.push(new Pair<>("T", this.getGoto(this.stack.peek().getValue(), 'T')));
                break;
            case 5: // F -> ( E )
                this.popStack(3);
                this.stack.push(new Pair<>("F", this.getGoto(this.stack.peek().getValue(), 'F')));
                break;
            case 6: // F -> a
                this.popStack(1);
                this.stack.push(new Pair<>("F", this.getGoto(this.stack.peek().getValue(), 'F')));
                break;
            case 7: // F -> ( E )
                this.popStack(3);
                this.stack.push(new Pair<>("F", this.getGoto(this.stack.peek().getValue(), 'F')));
                break;
        }
    }
}
