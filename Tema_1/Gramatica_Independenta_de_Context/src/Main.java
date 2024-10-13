public class Main {
    public static void main(String[] args) {
        Gramatica gramatica = new Gramatica();

        System.out.println("Derivare extrem stanga:");
        gramatica.genereazaSiruri(60, 5, true);

        System.out.println("\nDerivare extrem dreapta:");
        gramatica.genereazaSiruri(60, 5, false);
     }
}