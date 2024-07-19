
package medicaments;

public class VenteLibre extends Medicament {
    public VenteLibre(String id, String nom, int quantite) {
        super(id, nom, quantite);
    }

    @Override
    public String getCouleur() {
        return "\u001B[32m"; // Vert
    }
}
