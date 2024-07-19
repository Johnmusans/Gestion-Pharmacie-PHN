import medicaments.Medicament;
import medicaments.Ordonnance;
import medicaments.VenteLibre;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static Map<String, Medicament> medicaments = new HashMap<>();
    private static final String FILENAME = "medicaments.json"; // Changer ce nom de fichier pour tester différents formats

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
        if (FILENAME.endsWith(".csv")) {
            sauvegarderEnCSV();
        } else if (FILENAME.endsWith(".txt")) {
            sauvegarderEnTXT();
        } else if (FILENAME.endsWith(".json")) {
            sauvegarderEnJSON();
        } else if (FILENAME.endsWith(".xml")) {
            sauvegarderEnXML();
        } else {
            System.err.println("Format de fichier inconnu.");
        }
    }

    public static void sauvegarderEnCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILENAME))) {
            for (Medicament medicament : medicaments.values()) {
                writer.write(medicament.toCSV());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde des médicaments en CSV: " + e.getMessage());
        }
    }

    public static void sauvegarderEnTXT() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILENAME))) {
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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILENAME))) {
            writer.write(jsonBuilder.toString());
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde des médicaments en JSON: " + e.getMessage());
        }
    }

    public static void sauvegarderEnXML() {
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<medicaments>");
        for (Medicament medicament : medicaments.values()) {
            xmlBuilder.append("<medicament>");
            xmlBuilder.append("<id>").append(medicament.getId()).append("</id>");
            xmlBuilder.append("<nom>").append(medicament.getNom()).append("</nom>");
            xmlBuilder.append("<quantite>").append(medicament.getQuantite()).append("</quantite>");
            xmlBuilder.append("<type>").append(medicament.getClass().getSimpleName()).append("</type>");
            xmlBuilder.append("</medicament>");
        }
        xmlBuilder.append("</medicaments>");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILENAME))) {
            writer.write(xmlBuilder.toString());
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde des médicaments en XML: " + e.getMessage());
        }
    }

    public static void chargerMedicaments() {
        if (FILENAME.endsWith(".csv")) {
            chargerEnCSV();
        } else if (FILENAME.endsWith(".txt")) {
            chargerEnTXT();
        } else if (FILENAME.endsWith(".json")) {
            chargerEnJSON();
        } else if (FILENAME.endsWith(".xml")) {
            chargerEnXML();
        } else {
            System.err.println("Format de fichier inconnu.");
        }
    }

    public static void chargerEnCSV() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILENAME))) {
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
        try (BufferedReader reader = new BufferedReader(new FileReader(FILENAME))) {
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
        try (BufferedReader reader = new BufferedReader(new FileReader(FILENAME))) {
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            String jsonString = jsonBuilder.toString();
            if (!jsonString.isEmpty()) {
                jsonString = jsonString.substring(1, jsonString.length() - 1); // Remove [ and ]
                String[] medicamentsJson = jsonString.split("\\},\\{");
                for (String medicamentJson : medicamentsJson) {
                    medicamentJson = medicamentJson.replace("{", "").replace("}", "");
                    String[] attributes = medicamentJson.split(",");
                    String id = null;
                    String nom = null;
                    int quantite = 0;
                    String type = null;
                    for (String attribute : attributes) {
                        String[] keyValue = attribute.split(":");
                        String key = keyValue[0].replace("\"", "").trim();
                        String value = keyValue[1].replace("\"", "").trim();
                        switch (key) {
                            case "id":
                                id = value;
                                break;
                            case "nom":
                                nom = value;
                                break;
                            case "quantite":
                                quantite = Integer.parseInt(value);
                                break;
                            case "type":
                                type = value;
                                break;
                        }
                    }
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

    public static void chargerEnXML() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILENAME))) {
            String line;
            StringBuilder xmlBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                xmlBuilder.append(line);
            }
            String xmlString = xmlBuilder.toString();
            String[] medicamentsXml = xmlString.split("</medicament>");
            for (String medicamentXml : medicamentsXml) {
                if (medicamentXml.contains("<medicament>")) {
                    medicamentXml = medicamentXml.substring(medicamentXml.indexOf("<medicament>") + "<medicament>".length());
                    String id = medicamentXml.substring(medicamentXml.indexOf("<id>") + "<id>".length(), medicamentXml.indexOf("</id>"));
                    String nom = medicamentXml.substring(medicamentXml.indexOf("<nom>") + "<nom>".length(), medicamentXml.indexOf("</nom>"));
                    int quantite = Integer.parseInt(medicamentXml.substring(medicamentXml.indexOf("<quantite>") + "<quantite>".length(), medicamentXml.indexOf("</quantite>")));
                    String type = medicamentXml.substring(medicamentXml.indexOf("<type>") + "<type>".length(), medicamentXml.indexOf("</type>"));
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
            System.err.println("Erreur lors du chargement des médicaments en XML: " + e.getMessage());
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
