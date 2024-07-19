import medicaments.Medicament;
import medicaments.Ordonnance;
import medicaments.VenteLibre;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static Map<String, Medicament> medicaments = new HashMap<>();
    private static final String CSV_FILENAME = "medicaments.csv";
    private static final String TXT_FILENAME = "medicaments.txt";
    private static final String JSON_FILENAME = "medicaments.json";
    private static final String FORMAT = "json"; // ou csv, txt selon votre préférence

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
            sauvegarderMedicaments();
        } else {
            System.err.println("Médicament avec ID " + id + " non trouvé.");
        }
    }

    public static void sauvegarderMedicaments() {
        switch (FORMAT.toLowerCase()) {
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
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");
        boolean first = true;
        for (Medicament medicament : medicaments.values()) {
            if (!first) {
                jsonBuilder.append(",");
            }
            first = false;
            jsonBuilder.append("{");
            jsonBuilder.append("\"id\":\"").append(medicament.getId()).append("\",");
            jsonBuilder.append("\"nom\":\"").append(medicament.getNom()).append("\",");
            jsonBuilder.append("\"quantite\":").append(medicament.getQuantite()).append(",");
            jsonBuilder.append("\"type\":\"").append(medicament.getClass().getSimpleName()).append("\"");
            jsonBuilder.append("}");
        }
        jsonBuilder.append("]");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(JSON_FILENAME))) {
            writer.write(jsonBuilder.toString());
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde des médicaments en JSON: " + e.getMessage());
        }
    }

    public static void chargerMedicaments() {
        switch (FORMAT.toLowerCase()) {
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
            String json = jsonBuilder.toString();
            if (json.startsWith("[")) {
                json = json.substring(1, json.length() - 1);
                String[] items = json.split("(?<=\\}),\\s*(?=\\{)");
                for (String item : items) {
                    item = item.trim();
                    if (item.isEmpty()) continue;
                    Map<String, String> jsonMap = parseJsonObject(item);
                    String id = jsonMap.get("id");
                    String nom = jsonMap.get("nom");
                    int quantite = Integer.parseInt(jsonMap.get("quantite"));
                    String type = jsonMap.get("type");
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
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement des médicaments en JSON: " + e.getMessage());
        }
    }

    private static Map<String, String> parseJsonObject(String jsonObject) {
        Map<String, String> map = new HashMap<>();
        jsonObject = jsonObject.substring(1, jsonObject.length() - 1); // Remove curly braces
        String[] keyValuePairs = jsonObject.split("\",");
        for (String pair : keyValuePairs) {
            String[] keyValue = pair.split("\":\"");
            String key = keyValue[0].replace("\"", "").trim();
            String value = keyValue[1].replace("\"", "").trim();
            map.put(key, value);
        }
        return map;
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
            if (medicament.getNom().toUpperCase().charAt(0) == lettre) {
                System.out.println(medicament.getCouleur() + medicament + "\u001B[0m");
            }
        }
    }

    public static int obtenirNombreDeMedicamentsEnStock() {
        return medicaments.size();
    }

    public static void alerterStockBas() {
        for (Medicament medicament : medicaments.values()) {
            if (medicament.getQuantite() < 5) {
                System.out.println(medicament.getCouleur() + medicament + "\u001B[0m");
            }
        }
    }

    public static void genererRapportStock() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("rapport_stock.txt"))) {
            writer.write("| ID         | Nom                 | Quantité   |");
            writer.newLine();
            writer.write("|------------|---------------------|------------|");
            writer.newLine();
            for (Medicament medicament : medicaments.values()) {
                writer.write(medicament.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la génération du rapport de stock: " + e.getMessage());
        }
    }

    public static void mettreAJourInformationsMedicament(String id, String nouveauNom, int nouvelleQuantite) {
        Medicament medicament = medicaments.get(id);
        if (medicament != null) {
            medicament.setNom(nouveauNom);
            medicament.setQuantite(nouvelleQuantite);
            sauvegarderMedicaments();
        } else {
            System.err.println("Médicament avec ID " + id + " non trouvé.");
        }
    }

    public static void main(String[] args) {
        chargerMedicaments(); // Charger les médicaments au démarrage
        Scanner scanner = new Scanner(System.in);
        boolean continuer = true;
        while (continuer) {
            afficherMenu();
            System.out.print("Choisissez une option: ");
            int choix = scanner.nextInt();
            scanner.nextLine(); // Consommer la nouvelle ligne
            String id, nom, nouveauNom;
            int quantite, nouvelleQuantite, type;
            switch (choix) {
                case 1:
                    System.out.print("ID du médicament: ");
                    id = scanner.nextLine();
                    System.out.print("Nom du médicament: ");
                    nom = scanner.nextLine();
                    System.out.print("Quantité: ");
                    quantite = scanner.nextInt();
                    scanner.nextLine(); // Consommer la nouvelle ligne
                    System.out.print("Type (1: VenteLibre, 2: Ordonnance): ");
                    type = scanner.nextInt();
                    scanner.nextLine(); // Consommer la nouvelle ligne
                    Medicament medicament = (type == 1) ? new VenteLibre(id, nom, quantite) : new Ordonnance(id, nom, quantite);
                    ajouterMedicament(medicament);
                    break;
                case 2:
                    System.out.print("ID du médicament à supprimer: ");
                    id = scanner.nextLine();
                    supprimerMedicament(id);
                    break;
                case 3:
                    System.out.print("ID du médicament à rechercher: ");
                    id = scanner.nextLine();
                    try {
                        medicament = rechercherMedicament(id);
                        System.out.println(medicament);
                    } catch (MedicamentNotFoundException e) {
                        System.err.println(e.getMessage());
                    }
                    break;
                case 4:
                    System.out.print("Type (1: VenteLibre, 2: Ordonnance): ");
                    type = scanner.nextInt();
                    scanner.nextLine(); // Consommer la nouvelle ligne
                    afficherMedicamentsParType((type == 1) ? VenteLibre.class : Ordonnance.class);
                    break;
                case 5:
                    System.out.print("ID du médicament à modifier: ");
                    id = scanner.nextLine();
                    System.out.print("Nouvelle quantité: ");
                    quantite = scanner.nextInt();
                    scanner.nextLine(); // Consommer la nouvelle ligne
                    modifierMedicament(id, quantite);
                    break;
                case 6:
                    System.out.print("Lettre: ");
                    char lettre = scanner.nextLine().toUpperCase().charAt(0);
                    listerMedicamentsParLettre(lettre);
                    break;
                case 7:
                    System.out.println("Nombre de médicaments en stock: " + obtenirNombreDeMedicamentsEnStock());
                    break;
                case 8:
                    alerterStockBas();
                    break;
                case 9:
                    genererRapportStock();
                    break;
                case 10:
                    System.out.print("ID du médicament à mettre à jour: ");
                    id = scanner.nextLine();
                    System.out.print("Nouveau nom: ");
                    nouveauNom = scanner.nextLine();
                    System.out.print("Nouvelle quantité: ");
                    nouvelleQuantite = scanner.nextInt();
                    scanner.nextLine(); // Consommer la nouvelle ligne
                    mettreAJourInformationsMedicament(id, nouveauNom, nouvelleQuantite);
                    break;
                case 11:
                    sauvegarderMedicaments();
                    break;
                case 12:
                    chargerMedicaments();
                    break;
                case 13:
                    continuer = false;
                    break;
                default:
                    System.out.println("Option non reconnue. Veuillez réessayer.");
                    break;
            }
        }
        scanner.close();
    }
}
