import medicaments.Medicament;
import medicaments.Ordonnance;
import medicaments.VenteLibre;
import serializers.JsonSerializer;
import serializers.Serializer;
import serializers.TxtSerializer;
import serializers.XmlSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static Map<String, Medicament> medicaments = new HashMap<>();
    private static final Serializer[] SERIALIZERS = {
            new serializers.CsvSerializer(),
            new TxtSerializer(),
            new JsonSerializer(),
            new XmlSerializer()
    };

    public static void ajouterMedicament(Medicament medicament) {
        medicaments.put(medicament.getId(), medicament);
        sauvegarderMedicaments();
    }

    public static void supprimerMedicament(String id) {
        if (medicaments.remove(id) != null) {
            sauvegarderMedicaments();
        } else {
            System.err.println("Médicament avec ID " + id + " non trouvé.");
        }
    }

    public static Medicament rechercherMedicamentParId(String id) throws MedicamentNotFoundException {
        Medicament medicament = medicaments.get(id);
        if (medicament == null) {
            throw new MedicamentNotFoundException("Médicament avec ID " + id + " non trouvé.");
        }
        return medicament;
    }

    public static Medicament rechercherMedicamentParNom(String nom) throws MedicamentNotFoundException {
        for (Medicament medicament : medicaments.values()) {
            if (medicament.getNom().equalsIgnoreCase(nom)) {
                return medicament;
            }
        }
        throw new MedicamentNotFoundException("Médicament avec nom " + nom + " non trouvé.");
    }

    public static void afficherMedicamentsParType(Class<?> type) {
        System.out.println("| ID         | Nom                 | Quantité   |");
        System.out.println("|------------|---------------------|------------|");
        for (Medicament medicament : medicaments.values()) {
            if (type.isInstance(medicament)) {
                System.out.println(medicament.getCouleur() + medicament + "\u001B[0m");
            }
        }
    }

    public static void modifierMedicament(String id, String nouveauNom, int nouvelleQuantite) {
        Medicament medicament = medicaments.get(id);
        if (medicament != null) {
            medicament.setNom(nouveauNom);
            medicament.setQuantite(nouvelleQuantite);
            sauvegarderMedicaments();
        } else {
            System.err.println("Médicament avec ID " + id + " non trouvé.");
        }
    }

    public static void sauvegarderMedicaments() {
        for (Serializer serializer : SERIALIZERS) {
            serializer.sauvegarder(medicaments);
        }
    }

    public static void chargerMedicaments() {
        for (Serializer serializer : SERIALIZERS) {
            serializer.charger(medicaments);
        }
    }

    public static void listerMedicamentsParLettre(String lettre) {
        lettre = lettre.toLowerCase();
        System.out.println("| ID         | Nom                 | Quantité   |");
        System.out.println("|------------|---------------------|------------|");
        for (Medicament medicament : medicaments.values()) {
            if (medicament.getNom().toLowerCase().startsWith(lettre)) {
                System.out.println(medicament.getCouleur() + medicament + "\u001B[0m");
            }
        }
    }

    public static int obtenirNombreDeMedicamentsEnStock() {
        int total = 0;
        for (Medicament medicament : medicaments.values()) {
            total += medicament.getQuantite();
        }
        return total;
    }

    public static void afficherMenu() {
        System.out.println("\n***** Menu de gestion des médicaments *****");
        System.out.println("1. Ajouter un médicament");
        System.out.println("2. Supprimer un médicament");
        System.out.println("3. Rechercher un médicament par ID");
        System.out.println("4. Rechercher un médicament par nom");
        System.out.println("5. Afficher tous les médicaments par type");
        System.out.println("6. Modifier un médicament");
        System.out.println("7. Lister les médicaments par première lettre");
        System.out.println("8. Afficher le nombre total de médicaments en stock");
        System.out.println("9. Quitter");
        System.out.print("Votre choix: ");
    }

    public static void main(String[] args) {
        chargerMedicaments();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            afficherMenu();
            int choix = scanner.nextInt();
            scanner.nextLine(); // Consomme la nouvelle ligne

            switch (choix) {
                case 1:
                    System.out.print("ID: ");
                    String id = scanner.nextLine();
                    System.out.print("Nom: ");
                    String nom = scanner.nextLine();
                    System.out.print("Quantité: ");
                    int quantite = scanner.nextInt();
                    scanner.nextLine(); // Consomme la nouvelle ligne
                    System.out.print("Type (1 pour Vente Libre, 2 pour Ordonnance): ");
                    int type = scanner.nextInt();
                    scanner.nextLine(); // Consomme la nouvelle ligne

                    Medicament medicament;
                    if (type == 1) {
                        medicament = new VenteLibre(id, nom, quantite);
                    } else if (type == 2) {
                        medicament = new Ordonnance(id, nom, quantite);
                    } else {
                        System.out.println("Type invalide.");
                        break;
                    }
                    ajouterMedicament(medicament);
                    System.out.println("Médicament ajouté avec succès.");
                    break;

                case 2:
                    System.out.print("ID du médicament à supprimer: ");
                    id = scanner.nextLine();
                    supprimerMedicament(id);
                    System.out.println("Médicament supprimé avec succès.");
                    break;

                case 3:
                    System.out.print("ID du médicament à rechercher: ");
                    id = scanner.nextLine();
                    try {
                        medicament = rechercherMedicamentParId(id);
                        System.out.println(medicament);
                    } catch (MedicamentNotFoundException e) {
                        System.err.println(e.getMessage());
                    }
                    break;

                case 4:
                    System.out.print("Nom du médicament à rechercher: ");
                    nom = scanner.nextLine();
                    try {
                        medicament = rechercherMedicamentParNom(nom);
                        System.out.println(medicament);
                    } catch (MedicamentNotFoundException e) {
                        System.err.println(e.getMessage());
                    }
                    break;

                case 5:
                    System.out.print("Type de médicament à afficher (1 pour Vente Libre, 2 pour Ordonnance): ");
                    type = scanner.nextInt();
                    scanner.nextLine(); // Consomme la nouvelle ligne
                    if (type == 1) {
                        afficherMedicamentsParType(VenteLibre.class);
                    } else if (type == 2) {
                        afficherMedicamentsParType(Ordonnance.class);
                    } else {
                        System.out.println("Type invalide.");
                    }
                    break;

                case 6:
                    System.out.print("ID du médicament à modifier: ");
                    id = scanner.nextLine();
                    System.out.print("Nouveau nom: ");
                    String nouveauNom = scanner.nextLine();
                    System.out.print("Nouvelle quantité: ");
                    int nouvelleQuantite = scanner.nextInt();
                    scanner.nextLine(); // Consomme la nouvelle ligne
                    modifierMedicament(id, nouveauNom, nouvelleQuantite);
                    System.out.println("Médicament modifié avec succès.");
                    break;

                case 7:
                    System.out.print("Première lettre des médicaments à lister: ");
                    String lettre = scanner.nextLine();
                    listerMedicamentsParLettre(lettre);
                    break;

                case 8:
                    int nombreTotal = obtenirNombreDeMedicamentsEnStock();
                    System.out.println("Nombre total de médicaments en stock: " + nombreTotal);
                    break;

                case 9:
                    sauvegarderMedicaments();
                    System.out.println("Au revoir !");
                    scanner.close();
                    return;

                default:
                    System.out.println("Choix invalide. Veuillez réessayer.");
            }
        }
    }
}
