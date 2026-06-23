package edu.eci.arsw.blueprints;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import edu.eci.arsw.blueprints.persistence.InMemoryBlueprintPersistence;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryBlueprintPersistenceTest {

    @Test
    void initialDataIsPresent() {
        InMemoryBlueprintPersistence persistence = new InMemoryBlueprintPersistence();
        Set<Blueprint> all = persistence.getAllBlueprints();
        assertFalse(all.isEmpty(), "Initial sample data should exist");
    }

    @Test
    void saveAndRetrieveBlueprint() throws BlueprintPersistenceException, BlueprintNotFoundException {
        InMemoryBlueprintPersistence persistence = new InMemoryBlueprintPersistence();
        Blueprint bp = new Blueprint("alice", "home", List.of(new Point(1,2)));
        persistence.saveBlueprint(bp);
        Blueprint loaded = persistence.getBlueprint("alice", "home");
        assertEquals("alice", loaded.getAuthor());
        assertEquals("home", loaded.getName());
        assertEquals(1, loaded.getPoints().size());
    }

    @Test
    void saveDuplicateThrows() throws BlueprintPersistenceException {
        InMemoryBlueprintPersistence persistence = new InMemoryBlueprintPersistence();
        Blueprint bp = new Blueprint("bob", "place", List.of(new Point(0,0)));
        persistence.saveBlueprint(bp);
        assertThrows(BlueprintPersistenceException.class, () -> persistence.saveBlueprint(bp));
    }

    @Test
    void addPointAppendsPoint() throws BlueprintNotFoundException, BlueprintPersistenceException {
        InMemoryBlueprintPersistence persistence = new InMemoryBlueprintPersistence();
        Blueprint bp = new Blueprint("carol", "shed", List.of(new Point(0,0)));
        persistence.saveBlueprint(bp);
        persistence.addPoint("carol", "shed", 5, 6);
        Blueprint loaded = persistence.getBlueprint("carol", "shed");
        assertEquals(2, loaded.getPoints().size());
        assertEquals(5, loaded.getPoints().get(1).x());
    }
}
