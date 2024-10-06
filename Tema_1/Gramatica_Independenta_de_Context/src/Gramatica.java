import java.util.*;
import java.io.*;

public class Gramatica {
    private ArrayList<String> neterminale;
    private ArrayList<String> terminale;
    private LinkedHashMap<String, ArrayList<String>> productii;
    private String simbolStart;

    public Gramatica() {
        this.neterminale = new ArrayList<String>();
        this.terminale = new ArrayList<String>();
        this.productii = new LinkedHashMap<String, ArrayList<String>>();
        this.citesteGramaticaDinFisier("Fisier.txt");
        this.afisareGramatica();
    }

    private void citesteGramaticaDinFisier(String numeFisier) {
        try (BufferedReader br = new BufferedReader(new FileReader(numeFisier))) {
            String linie,neterminal;
            String[] parts,reguli;
            while ((linie = br.readLine()) != null) {
                if (linie.startsWith("SimbolStart:")) {
                    simbolStart = linie.split(":")[1].trim();
                } else if (linie.startsWith("Neterminale:")) {
                    String[] neterminaleArray = linie.split(":")[1].trim().split(",");
                    neterminale.addAll(Arrays.asList(neterminaleArray));
                } else if (linie.startsWith("Terminale:")) {
                    String[] terminaleArray = linie.split(":")[1].trim().split(",");
                    terminale.addAll(Arrays.asList(terminaleArray));
                } else if (linie.equals("Productii:")) {
                    while ((linie = br.readLine()) != null) {
                        parts = linie.split("->");
                        neterminal = parts[0].trim();
                        reguli = parts[1].trim().split("\\|");
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

    public void afisareGramatica()
    {
        System.out.println("\nSimbolul de start: " + this.simbolStart);
        System.out.println("Neterminale: " + this.neterminale);
        System.out.println("Terminale: " + this.terminale);
        System.out.println("Productii:");
        for (Map.Entry<String, ArrayList<String>> entry : this.productii.entrySet())
            System.out.println("  " + entry.getKey() + " -> " + String.join(" | ", entry.getValue()));
        System.out.println("\n");
    }

    public void genereazaSiruri(int numarMaximCaractere, int numarDeSiruri, boolean derivareExtremStanga) {
        int siruriGenerate = 0;
        String sirCurent;

        while (siruriGenerate < numarDeSiruri) {
            sirCurent = this.simbolStart;
            System.out.println();
            while (true) {
                System.out.print(sirCurent);

                if (this.esteFormatDinTerminale(sirCurent)) {
                    System.out.println("\nSir generat: " + sirCurent + "\n");
                    siruriGenerate++;
                    break;
                }

                if (sirCurent.length() > numarMaximCaractere) {
                    System.out.println("\nSir prea lung, regenerare...\n");
                    break;
                }

                sirCurent = this.aplicaDerivare(sirCurent, derivareExtremStanga);
            }
        }
    }

    private boolean esteFormatDinTerminale(String sir) {
        for (char c : sir.toCharArray())
            if (!terminale.contains(String.valueOf(c)))
                return false;
        return true;
    }

    private String aplicaDerivare(String sir, boolean derivareExtremStanga) {
        String[] simboluri = sir.split("");
        int startIndex = derivareExtremStanga ? 0 : simboluri.length - 1;
        int endIndex = derivareExtremStanga ? simboluri.length : -1;
        int step = derivareExtremStanga ? 1 : -1;

        for (int i = startIndex; i != endIndex; i += step)
            if (neterminale.contains(simboluri[i])) {
                List<String> reguli = productii.get(simboluri[i]);
                Integer indexRegula = new Random().nextInt(reguli.size());
                String regula = reguli.get(indexRegula);

                for (Map.Entry<String, ArrayList<String>> entry : this.productii.entrySet())
                    if (!entry.getValue().equals(reguli))
                        indexRegula+=entry.getValue().size();
                    else
                        break;
                    System.out.print("->(" + ++indexRegula + ")->");

                return sir.substring(0, i) + regula + sir.substring(i + 1);
            }
        return null;
    }

    public ArrayList<String> getNeterminale() {
        return neterminale;
    }

    public void setNeterminale(ArrayList<String> neterminale) {
        this.neterminale = neterminale;
    }

    public ArrayList<String> getTerminale() {
        return terminale;
    }

    public void setTerminale(ArrayList<String> terminale) {
        this.terminale = terminale;
    }

    public LinkedHashMap<String, ArrayList<String>> getProductii() {
        return productii;
    }

    public void setProductii(LinkedHashMap<String, ArrayList<String>> productii) {
        this.productii = productii;
    }

    public String getSimbolStart() {
        return simbolStart;
    }

    public void setSimbolStart(String simbolStart) {
        this.simbolStart = simbolStart;
    }
}