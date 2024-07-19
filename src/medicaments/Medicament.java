package medicaments;

public abstract class Medicament {
    protected String id;
    public String nom;
    protected int quantite;

    public Medicament(String id, String nom, int quantite) {
        this.id = id;
        this.nom = nom;
        this.quantite = quantite;
    }

    public static Medicament fromCSV(String line) {
        return null;
    }

    public String getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    @Override
    public String toString() {
        return String.format("| %-10s | %-20s | %-10d |", id, nom, quantite);
    }

    public abstract String getCouleur();

    public int toCSV() {
        return 0;
    }

    public void setNom(String nouveauNom) {
    }
}
