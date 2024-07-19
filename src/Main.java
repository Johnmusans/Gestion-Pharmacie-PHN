import medicaments.Medicament;
import medicaments.Ordonnance;
import medicaments.VenteLibre;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static Map<String, Medicament> medicaments = new HashMap<>();
    private static final String CSV_FILENAME = "medicaments.csv";
    private static final String TXT_FILENAME = "medicaments.txt";
    private static final String JSON_FILENAME = "medicaments.json";

    public static void ajouterMedicament(Medicament medicament) {
        medicaments.put(medicament.getId(), medicament);
    }

    public static void supprimerMedicament(String id) {
        medicaments.remove(id);
    }

    public static Medicament rechercherMedicament(String id) throws MedicamentNotFoundException {
        Medicament medicament = medicaments.get(id);
        if (medicament == null) {
            throw new MedicamentNotFoundException("Médicament avec ID " + id + " non trouvé.");
        }
        return medicament;
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

    public static void modifierMedicament(String id, int nouvelleQuantite) {
        Medicament medicament = medicaments.get(id);
        if (medicament != null) {
            medicament.setQuantite(nouvelleQuantite);
        }
    }

    public static void sauvegarderMedicaments(String format) {
        switch (format.toLowerCase()) {
            case "csv":
                sauvegarderEnCSV();
                break;
            case "txt":
                sauvegarderEnTXT();
                break;
            case "json":
                sauvegarderEnJSON();
                break;
            default:
                System.out.println("Format inconnu.");
                break;
        }
    }

    public static void sauvegarderEnCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILENAME))) {
            for (Medicament medicament : medicaments.values()) {
                writer.write(medicament.toCSV());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde des médicaments en CSV: " + e.getMessage());
        }
    }

    public static void sauvegarderEnTXT() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TXT_FILENAME))) {
            for (Medicament medicament : medicaments.values()) {
                writer.write(medicament.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde des médicaments en TXT: " + e.getMessage());
        }
    }

    public static void sauvegarderEnJSON() {
        JSONArray jsonArray = new JSONArray();
        for (Medicament medicament : medicaments.values()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", medicament.getId());
            jsonObject.put("nom", medicament.getNom());
            jsonObject.put("quantite", medicament.getQuantite());
            jsonObject.put("type", medicament.getClass().getSimpleName());
            jsonArray.put(jsonObject);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(JSON_FILENAME))) {
            writer.write(jsonArray.toString());
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde des médicaments en JSON: " + e.getMessage());
        }
    }

    public static void chargerMedicaments(String format) {
        switch (format.toLowerCase()) {
            case "csv":
                chargerEnCSV();
                break;
            case "txt":
                chargerEnTXT();
                break;
            case "json":
                chargerEnJSON();
                break;
            default:
                System.out.println("Format inconnu.");
                break;
        }
    }

    public static void chargerEnCSV() {
        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILENAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Medicament medicament = Medicament.fromCSV(line);
                medicaments.put(medicament.getId(), medicament);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement des médicaments en CSV: " + e.getMessage());
        }
    }

    public static void chargerEnTXT() {
        try (BufferedReader reader = new BufferedReader(new FileReader(TXT_FILENAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                String id = parts[1].trim();
                String nom = parts[2].trim();
                int quantite = Integer.parseInt(parts[3].trim());
                String type = parts[4].trim();
                Medicament medicament;
                if (type.equals("VenteLibre")) {
                    medicament = new VenteLibre(id, nom, quantite);
                } else if (type.equals("Ordonnance")) {
                    medicament = new Ordonnance(id, nom, quantite);
                } else {
                    continue;
                }
                medicaments.put(id, medicament);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement des médicaments en TXT: " + e.getMessage());
        }
    }

    public static void chargerEnJSON() {
        try (BufferedReader reader = new BufferedReader(new FileReader(JSON_FILENAME))) {
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            JSONArray jsonArray = new JSONArray(jsonBuilder.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                String nom = jsonObject.getString("nom");
                int quantite = jsonObject.getInt("quantite");
                String type = jsonObject.getString("type");
                Medicament medicament;
                if (type.equals("VenteLibre")) {
                    medicament = new VenteLibre(id, nom, quantite);
                } else if (type.equals("Ordonnance")) {
                    medicament = new Ordonnance(id, nom, quantite);
                } else {
                    continue;
                }
                medicaments.put(id, medicament);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement des médicaments en JSON: " + e.getMessage());
        }
    }

    public static void afficherMenu() {
        System.out.println("Menu:");
        System.out.println("1. Ajouter un médicament");
        System.out.println("2. Supprimer un médicament");
        System.out.println("3. Rechercher un médicament");
        System.out.println("4. Afficher les médicaments par type");
        System.out.println("5. Modifier un médicament");
        System.out.println("6. Lister les médicaments par lettre");
        System.out.println("7. Afficher le nombre de médicaments en stock");
        System.out.println("8. Alerte de stock bas");
        System.out.println("9. Générer un rapport de stock");
        System.out.println("10. Mettre à jour les informations d'un médicament");
        System.out.println("11. Sauvegarder les médicaments");
        System.out.println("12. Charger les médicaments");
        System.out.println("13. Quitter");
    }

    public static void listerMedicamentsParLettre(char lettre) {
        System.out.println("| ID         | Nom                 | Quantité   |");
        System.out.println("|------------|---------------------|------------|");
        for (Medicament medicament : medicaments.values()) {
            if (medicament.getNom().charAt(0) == lettre) {
                System.out.println(medicament.getCouleur() + medicament + "\u001B[0m");
            }
        }
    }

    public static void afficherNombreMedicaments() {
        System.out.println("Nombre total de médicaments en stock: " + medicaments.size());
    }

    public static void alerteStockBas(int seuil) {
        System.out.println("Médicaments avec stock bas (moins de " + seuil + " unités):");
        for (Medicament medicament : medicaments.values()) {
            if (medicament.getQuantite() < seuil) {
                System.out.println(medicament.getCouleur() + medicament + "\u001B[0m");
            }
        }
    }

    public static void genererRapportStock() {
        System.out.println("Rapport de stock:");
        System.out.println("| ID         | Nom                 | Quantité   |");
        System.out.println("|------------|---------------------|------------|");
        for (Medicament medicament : medicaments.values()) {
            System.out.println(medicament.getCouleur() + medicament + "\u001B[0m");
        }
    }

    public static void mettreAJourMedicament(String id, String nouveauNom, int nouvelleQuantite) {
        Medicament medicament = medicaments.get(id);
        if (medicament != null) {
            medicament.nom = nouveauNom;
            medicament.setQuantite(nouvelleQuantite);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean continuer = true;

        while (continuer) {
            afficherMenu();
            try {
                int choix = scanner.nextInt();
                scanner.nextLine(); // Consommer la nouvelle ligne

                switch (choix) {
                    case 1:
                        System.out.println("Ajouter un médicament:");
                        System.out.print("ID: ");
                        String id = scanner.nextLine();
                        System.out.print("Nom: ");
                        String nom = scanner.nextLine();
                        System.out.print("Quantité: ");
                        int quantite = scanner.nextInt();
                        scanner.nextLine(); // Consommer la nouvelle ligne
                        System.out.print("Type (1 pour Vente Libre, 2 pour Ordonnance): ");
                        int type = scanner.nextInt();
                        scanner.nextLine(); // Consommer la nouvelle ligne
                        if (type == 1) {
                            ajouterMedicament(new VenteLibre(id, nom, quantite));
                        } else if (type == 2) {
                            ajouterMedicament(new Ordonnance(id, nom, quantite));
                        }
                        break;
                    case 2:
                        System.out.println("Supprimer un médicament:");
                        System.out.print("ID: ");
                        id = scanner.nextLine();
                        supprimerMedicament(id);
                        break;
                    case 3:
                        System.out.println("Rechercher un médicament:");
                        System.out.print("ID: ");
                        id = scanner.nextLine();
                        Medicament medicament = rechercherMedicament(id);
                        System.out.println(medicament.getCouleur() + medicament + "\u001B[0m");
                        break;
                    case 4:
                        System.out.println("Afficher les médicaments par type:");
                        System.out.print("Type (1 pour Vente Libre, 2 pour Ordonnance): ");
                        type = scanner.nextInt();
                        scanner.nextLine(); // Consommer la nouvelle ligne
                        if (type == 1) {
                            afficherMedicamentsParType(VenteLibre.class);
                        } else if (type == 2) {
                            afficherMedicamentsParType(Ordonnance.class);
                        }
                        break;
                    case 5:
                        System.out.println("Modifier un médicament:");
                        System.out.print("ID: ");
                        id = scanner.nextLine();
                        System.out.print("Nouvelle quantité: ");
                        quantite = scanner.nextInt();
                        scanner.nextLine(); // Consommer la nouvelle ligne
                        modifierMedicament(id, quantite);
                        break;
                    case 6:
                        System.out.println("Lister les médicaments par lettre:");
                        System.out.print("Lettre: ");
                        char lettre = scanner.nextLine().charAt(0);
                        listerMedicamentsParLettre(lettre);
                        break;
                    case 7:
                        afficherNombreMedicaments();
                        break;
                    case 8:
                        System.out.print("Seuil de stock bas: ");
                        int seuil = scanner.nextInt();
                        scanner.nextLine(); // Consommer la nouvelle ligne
                        alerteStockBas(seuil);
                        break;
                    case 9:
                        genererRapportStock();
                        break;
                    case 10:
                        System.out.println("Mettre à jour les informations d'un médicament:");
                        System.out.print("ID: ");
                        id = scanner.nextLine();
                        System.out.print("Nouveau nom: ");
                        nom = scanner.nextLine();
                        System.out.print("Nouvelle quantité: ");
                        quantite = scanner.nextInt();
                        scanner.nextLine(); // Consommer la nouvelle ligne
                        mettreAJourMedicament(id, nom, quantite);
                        break;
                    case 11:
                        System.out.println("Sauvegarder les médicaments:");
                        System.out.print("Format (csv, txt, json): ");
                        String format = scanner.nextLine();
                        sauvegarderMedicaments(format);
                        break;
                    case 12:
                        System.out.println("Charger les médicaments:");
                        System.out.print("Format (csv, txt, json): ");
                        format = scanner.nextLine();
                        chargerMedicaments(format);
                        break;
                    case 13:
                        continuer = false;
                        break;
                    default:
                        System.out.println("Choix invalide.");
                        break;
                }
            } catch (MedicamentNotFoundException e) {
                System.err.println(e.getMessage());
            } catch (Exception e) {
                System.err.println("Erreur: " + e.getMessage());
            }
        }

        scanner.close();
    }
}
