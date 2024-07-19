package serializers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import medicaments.Medicament;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Map;

public class JsonSerializer implements Serializer {
    private static final String FILENAME = "medicaments.json";
    private static final Gson GSON = new Gson();

    @Override
    public void sauvegarder(Map<String, Medicament> medicaments) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILENAME))) {
            GSON.toJson(medicaments, writer);
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde des médicaments en JSON: " + e.getMessage());
        }
    }

    @Override
    public void charger(Map<String, Medicament> medicaments) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILENAME))) {
            Type type = new TypeToken<Map<String, Medicament>>() {}.getType();
            Map<String, Medicament> loadedMedicaments = GSON.fromJson(reader, type);
            medicaments.putAll(loadedMedicaments);
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement des médicaments en JSON: " + e.getMessage());
        }
    }
}
