
package serializers;

import medicaments.Medicament;

import java.util.Map;

public interface Serializer {
    void sauvegarder(Map<String, Medicament> medicaments);
    void charger(Map<String, Medicament> medicaments);
}
