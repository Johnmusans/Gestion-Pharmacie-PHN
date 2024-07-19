
package medicaments;

public class Ordonnance extends Medicament {
    public Ordonnance(String id, String nom, int quantite) {
        super(id, nom, quantite);
    }

    @Override
    public String getCouleur() {
        return "\u001B[31m"; // Rouge
    }
}
