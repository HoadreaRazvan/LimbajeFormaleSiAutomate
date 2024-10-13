import java.util.*;

public class Main {
    public static void main(String[] args) {
        LR1PushDownAutomaton apd = new LR1PushDownAutomaton();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Introduceti sirul de intrare: ");
        String input = scanner.nextLine();

        apd.parseInput(input);
    }
}