package edu.eci.arsw.blueprints;

import edu.eci.arsw.blueprints.filters.BlueprintsFilter;
import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistence;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import edu.eci.arsw.blueprints.services.BlueprintsServices;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BlueprintsServicesTest {

    @Mock
    BlueprintPersistence persistence;

    @Mock
    BlueprintsFilter filter;

    @InjectMocks
    BlueprintsServices services;

    @Test
    void addNewBlueprintDelegatesToPersistence() throws BlueprintPersistenceException {
        Blueprint bp = new Blueprint("u","n", List.of(new Point(1,1)));
        services.addNewBlueprint(bp);
        verify(persistence, times(1)).saveBlueprint(bp);
    }

    @Test
    void getBlueprintAppliesFilter() throws BlueprintNotFoundException {
        Blueprint bp = new Blueprint("a","b", List.of(new Point(2,2)));
        when(persistence.getBlueprint("a","b")).thenReturn(bp);
        when(filter.apply(bp)).thenReturn(bp);
        Blueprint out = services.getBlueprint("a","b");
        assertSame(bp, out);
        verify(filter, times(1)).apply(bp);
    }

    @Test
    void addPointDelegates() throws BlueprintNotFoundException {
        services.addPoint("x","y",3,4);
        verify(persistence, times(1)).addPoint("x","y",3,4);
    }
}
