import java.io.*;
import java.util.*;

public class LR1PushDownAutomaton {
    private String[][] actionTable;
    private String[][] gotoTable;
    private String [] actionTableHeader;
    private String [] gotoTableHeader;
    private LinkedHashMap<String, ArrayList<String>> production;
    private String parseInputString;
    private Stack<Pair<String, Integer>> stack;

    public LR1PushDownAutomaton() {
        this.stack = new Stack<>();
        this.readingFile();
        this.show();
    }

    private void readingFile() {
        try (BufferedReader br = new BufferedReader(new FileReader("Tema_2.txt"))) {
            String line;
            String currentSection = "";
            int linesToRead = 0;
            List<String> actionTableHeaderList = new ArrayList<>();
            List<String[]> actionTableList = new ArrayList<>();
            List<String> gotoTableHeaderList = new ArrayList<>();
            List<String[]> gotoTableList = new ArrayList<>();
            List<String> productionList = new ArrayList<>();

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                if (line.startsWith("Action Table Header:")) {
                    currentSection = "ACTION_TABLE_HEADER";
                    String headerLine = line.substring("Action Table Header:".length()).trim();
                    actionTableHeaderList = Arrays.asList(headerLine.split("\\s+"));
                } else if (line.startsWith("Action Table:")) {
                    currentSection = "ACTION_TABLE";
                    linesToRead = Integer.parseInt(line.substring("Action Table:".length()).trim());
                } else if (line.startsWith("Go to Table Header:")) {
                    currentSection = "GOTO_TABLE_HEADER";
                    String headerLine = line.substring("Go to Table Header:".length()).trim();
                    gotoTableHeaderList = Arrays.asList(headerLine.split("\\s+"));
                } else if (line.startsWith("Go To Table:")) {
                    currentSection = "GOTO_TABLE";
                    linesToRead = Integer.parseInt(line.substring("Go To Table:".length()).trim());
                } else if (line.startsWith("Production:")) {
                    currentSection = "PRODUCTION";
                    linesToRead = Integer.parseInt(line.substring("Production:".length()).trim());
                } else if (line.startsWith("Parse Input String:")) {
                    currentSection = "PARSE_INPUT_STRING";
                    this.parseInputString = line.substring("Parse Input String:".length()).trim();
                } else {

                    if (currentSection.equals("ACTION_TABLE") && linesToRead > 0) {
                        String[] tokens = line.split("\\s+");
                        actionTableList.add(tokens);
                        linesToRead--;
                    } else if (currentSection.equals("GOTO_TABLE") && linesToRead > 0) {
                        String[] tokens = line.split("\\s+");
                        gotoTableList.add(tokens);
                        linesToRead--;
                    } else if (currentSection.equals("PRODUCTION") && linesToRead > 0) {
                        productionList.add(line);
                        linesToRead--;
                    }
                }
            }

            this.actionTableHeader = actionTableHeaderList.toArray(new String[0]);
            this.actionTable = actionTableList.toArray(new String[0][]);
            this.gotoTableHeader = gotoTableHeaderList.toArray(new String[0]);
            this.gotoTable = gotoTableList.toArray(new String[0][]);

            this.production = new LinkedHashMap<>();
            for (String prodLine : productionList) {
                String[] parts = prodLine.split("->");
                if (parts.length == 2) {
                    String lhs = parts[0].trim();
                    String rhs = parts[1].trim();
                    this.production.computeIfAbsent(lhs, k -> new ArrayList<>()).add(rhs);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void show() {
        System.out.println("Action Table:");

        System.out.print("\t");
        for (String header : this.actionTableHeader) {
            System.out.print(header + "\t");
        }
        System.out.println();

        for (int i = 0; i < this.actionTable.length; i++) {
            System.out.print((i + 1) + "\t");
            for (String cell : this.actionTable[i]) {
                System.out.print(cell + "\t");
            }
            System.out.println();
        }

        System.out.println("\nGo To Table:");

        System.out.print("\t");
        for (String header : this.gotoTableHeader) {
            System.out.print(header + "\t");
        }
        System.out.println();

        for (int i = 0; i < this.gotoTable.length; i++) {
            System.out.print((i + 1) + "\t");
            for (String cell : this.gotoTable[i]) {
                System.out.print(cell + "\t");
            }
            System.out.println();
        }

        System.out.println("\nProductions:");
        int index = 1;
        for (Map.Entry<String, ArrayList<String>> entry : production.entrySet()) {
            String lhs = entry.getKey();
            for (String rhs : entry.getValue()) {
                System.out.println(index++ +". "+lhs + " -> " + rhs);
            }
        }

        System.out.print("\nParse Input String:\t");
        System.out.println(parseInputString);
    }

    private void printStack(int sizePrintStack) {
        System.out.print("Stack "+sizePrintStack+" : ");
        for (int i = 0; i < this.stack.size(); i++) {
            Pair<String, Integer> item = this.stack.get(i);
            System.out.print("(" + item.getKey() + ", " + item.getValue() + ") ");
        }
    }

    private int getActionColumn(char symbol) {
        for(int i = 0; i < this.actionTableHeader.length; i++)
            if(this.actionTableHeader[i].equals(String.valueOf(symbol)))
                return i;
        return -1;
    }

    private int getGoto(int state, char nonTerminal) {
        int col = -1;
        for (int i = 0; i < this.gotoTableHeader.length; i++)
            if (this.gotoTableHeader[i].equals(String.valueOf(nonTerminal))) {
                col = i;
                break;
            }
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

    public void parseInput() {
        String input=this.parseInputString;
        input = input + "$";
        String inputDisplay=input,action;
        int sizePrintStack = 0,index = 0,state,newState,production;
        char symbol;
        this.stack.push(new Pair<>("$", 0));

        while (index < input.length()) {
            state = this.stack.peek().getValue();
            symbol = input.charAt(index);
            action = this.getAction(state, symbol);

            this.printStack(++sizePrintStack);
            System.out.print("\t" + inputDisplay.substring(index) + "\t"  + action);

            if (action == null || action.equals("")) {
                System.out.println("\nParse error.");
                return;
            }

            if (action.startsWith("d")) {
                newState = Integer.parseInt(action.substring(1));
                this.stack.push(new Pair<>(String.valueOf(symbol), newState));
                index++;
            } else if (action.startsWith("r")) {
                production = Integer.parseInt(action.substring(1));
                this.reduce(production);
            } else if (action.equals("acc")) {
                System.out.println("\nThe input string is correct.");
                return;
            }
        }
        System.out.println("\nThe input string is incorrect.");
    }

    private void reduce(int state) {
        String productionKeyValue = this.getValueForKeyAtIndex(state);
        this.popStack(productionKeyValue.split("\\|")[1].length());
        System.out.println("->"+productionKeyValue.split("\\|")[0]+"+TS("+this.stack.peek().getValue()+","+productionKeyValue.split("\\|")[0]+")");
        this.stack.push(new Pair<>(productionKeyValue.split("\\|")[0], this.getGoto(this.stack.peek().getValue(), productionKeyValue.charAt(0))));
    }

    public String getValueForKeyAtIndex(int index) {
        int currentIndex = 1;
        for (Map.Entry<String, ArrayList<String>> entry : this.production.entrySet()) {
            for(String value : entry.getValue()) {
                if (currentIndex == index)
                    return entry.getKey()+"|"+value;
                currentIndex++;
            }
        }
        return null;
    }
}
