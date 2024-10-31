public class Main {
    public static void main(String[] args) {
        LR1PushDownAutomaton apd = new LR1PushDownAutomaton();
        apd.parseInput();
        apd.showStack();
    }
}