
package medicaments;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class Medicament {
    private String id;
    private String nom;
    private int quantite;

    public Medicament(String id, String nom, int quantite) {
        this.id = id;
        this.nom = nom;
        this.quantite = quantite;
    }

    public static Medicament fromXML(Element element) {
        return null;
    }

    public static Medicament fromTXT(String line) {
        return null;
    }

    public String getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public String getCouleur() {
        return "";
    }

    @Override
    public String toString() {
        return String.format("| %-10s | %-19s | %-10d |", id, nom, quantite);
    }

    public String toCSV() {
        return id + "," + nom + "," + quantite + "," + getClass().getSimpleName();
    }

    public static Medicament fromCSV(String csv) {
        String[] parts = csv.split(",");
        String id = parts[0];
        String nom = parts[1];
        int quantite = Integer.parseInt(parts[2]);
        String type = parts[3];
        if (type.equals("VenteLibre")) {
            return new VenteLibre(id, nom, quantite);
        } else if (type.equals("Ordonnance")) {
            return new Ordonnance(id, nom, quantite);
        }
        throw new IllegalArgumentException("Type de médicament inconnu : " + type);
    }

    public int toTXT() {
        return 0;
    }

    public Node toXML(Document doc) {
        return null;
    }
}


