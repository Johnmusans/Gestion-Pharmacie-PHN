package serializers;

import medicaments.Medicament;

import java.io.*;
import java.util.Map;

public class TxtSerializer implements Serializer {
    private static final String FILENAME = "medicaments.txt";

    @Override
    public void sauvegarder(Map<String, Medicament> medicaments) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILENAME))) {
            for (Medicament medicament : medicaments.values()) {
                writer.write(medicament.toTXT());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde des médicaments en TXT: " + e.getMessage());
        }
    }

    @Override
    public void charger(Map<String, Medicament> medicaments) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILENAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Medicament medicament = Medicament.fromTXT(line);
                medicaments.put(medicament.getId(), medicament);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement des médicaments en TXT: " + e.getMessage());
        }
    }
}
