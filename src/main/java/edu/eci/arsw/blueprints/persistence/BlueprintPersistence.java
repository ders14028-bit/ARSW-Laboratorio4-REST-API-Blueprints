package edu.eci.arsw.blueprints.persistence;

import edu.eci.arsw.blueprints.model.Blueprint;
import java.util.Set;

public interface BlueprintPersistence {

    void saveBlueprint(Blueprint bp) throws BlueprintPersistenceException;

    Blueprint getBlueprint(String author, String name) throws BlueprintNotFoundException;

    Set<Blueprint> getBlueprintsByAuthor(String author) throws BlueprintNotFoundException;

    Set<Blueprint> getAllBlueprints();

    void addPoint(String author, String name, int x, int y) throws BlueprintNotFoundException;

    /**
     * Reemplaza por completo los puntos de un blueprint existente con los
     * puntos provistos en {@code bp}. No cambia el author/name (esos son
     * la clave del blueprint y no se actualizan).
     */
    void updateBlueprint(Blueprint bp) throws BlueprintNotFoundException;

    /**
     * Elimina un blueprint y todos sus puntos asociados.
     */
    void deleteBlueprint(String author, String name) throws BlueprintNotFoundException;
}