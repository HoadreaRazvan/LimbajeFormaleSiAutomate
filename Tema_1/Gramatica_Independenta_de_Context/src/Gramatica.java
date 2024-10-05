import java.util.*;
import java.io.*;

public class Gramatica {
    private Set<String> neterminale;
    private Set<String> terminale;
    private Map<String, List<String>> productii;
    private String simbolStart;

    public Gramatica() {
        neterminale = new HashSet<>();
        terminale = new HashSet<>();
        productii = new HashMap<>();
        citesteGramaticaDinFisier("Fisier.txt");
        afisareGramatica();
    }

    public void genereazaSiruri(int numarMaximCaractere, int numarDeSiruri, boolean derivareExtremStanga) {
        Random rand = new Random();
        int siruriGenerate = 0;

        while (siruriGenerate < numarDeSiruri) {
            String sirCurent = simbolStart;
            boolean sirValid = false;
            System.out.println();
            while (true) {
                System.out.print(sirCurent);

                if (esteFormatDinTerminale(sirCurent)) {
                    System.out.println("\nSir generat: " + sirCurent + "\n");
                    sirValid = true;
                    break;
                }

                if (sirCurent.length() > numarMaximCaractere) {
                    System.out.println("\nSir prea lung, regenerare...\n");
                    break;
                }

                sirCurent = aplicaDerivare(sirCurent, rand, derivareExtremStanga);
            }
            if (sirValid)
                siruriGenerate++;

        }
    }

    public void afisareGramatica()
    {
        System.out.println("\nSimbolul de start: " + this.simbolStart);
        System.out.println("Neterminale: " + this.neterminale);
        System.out.println("Terminale: " + this.terminale);
        System.out.println("Productii:");
        for (Map.Entry<String, List<String>> entry : this.productii.entrySet())
            System.out.println("  " + entry.getKey() + " -> " + String.join(" | ", entry.getValue()));
        System.out.println("\n");
    }

    private void citesteGramaticaDinFisier(String numeFisier) {
        try (BufferedReader br = new BufferedReader(new FileReader(numeFisier))) {
            String linie;

            while ((linie = br.readLine()) != null) {
                if (linie.startsWith("SimbolStart:")) {
                    simbolStart = linie.split(":")[1].trim();
                } else if (linie.startsWith("Neterminale:")) {
                    String[] neterminaleArray = linie.split(":")[1].trim().split(",");
                    neterminale.addAll(Arrays.asList(neterminaleArray));
                } else if (linie.startsWith("Terminale:")) {
                    String[] terminaleArray = linie.split(":")[1].trim().split(",");
                    terminale.addAll(Arrays.asList(terminaleArray));
                } else if (linie.startsWith("Productii:")) {
                    while ((linie = br.readLine()) != null && !linie.trim().isEmpty()) {
                        String[] parts = linie.split("->");
                        String neterminal = parts[0].trim();
                        String[] reguli = parts[1].trim().split("\\|");
                        productii.put(neterminal, new ArrayList<>());
                        for (String regula : reguli)
                            productii.get(neterminal).add(regula.trim().equals("null")? "" : regula.trim());
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Eroare la citirea fisierului: " + e.getMessage());
        }
    }

    private boolean esteFormatDinTerminale(String sir) {
        for (char c : sir.toCharArray())
            if (!terminale.contains(String.valueOf(c)))
                return false;
        return true;
    }

    private String aplicaDerivare(String sir, Random rand, boolean derivareExtremStanga) {
        String[] simboluri = sir.split("");
        int startIndex = derivareExtremStanga ? 0 : simboluri.length - 1;
        int endIndex = derivareExtremStanga ? simboluri.length : -1;
        int step = derivareExtremStanga ? 1 : -1;

        for (int i = startIndex; i != endIndex; i += step)
            if (neterminale.contains(simboluri[i])) {
                String neterminal = simboluri[i];
                List<String> reguli = productii.get(neterminal);
                Integer indexRegula = rand.nextInt(reguli.size());
                String regula = reguli.get(indexRegula);
                System.out.print("->(" + indexRegula + ")->");
                return sir.substring(0, i) + regula + sir.substring(i + 1);
            }

        return sir;
    }
}