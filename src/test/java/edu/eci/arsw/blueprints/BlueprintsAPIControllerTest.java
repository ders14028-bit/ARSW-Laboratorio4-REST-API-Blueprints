package edu.eci.arsw.blueprints;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.eci.arsw.blueprints.controllers.BlueprintsAPIController;
import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.services.BlueprintsServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class BlueprintsAPIControllerTest {

    MockMvc mvc;

    ObjectMapper mapper = new ObjectMapper();

    @Mock
    BlueprintsServices services;

    @BeforeEach
    void setup() {
        BlueprintsAPIController controller = new BlueprintsAPIController(services);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getAllReturnsOk() throws Exception {
        when(services.getAllBlueprints()).thenReturn(Set.of());
        BlueprintsAPIController controller = new BlueprintsAPIController(services);
        var resp = controller.getAll();
        assertEquals(200, resp.getStatusCodeValue());
    }

    @Test
    void getByAuthorNotFoundReturns404() throws Exception {
        when(services.getBlueprintsByAuthor("noone")).thenThrow(new BlueprintNotFoundException("no"));
        BlueprintsAPIController controller = new BlueprintsAPIController(services);
        var resp = controller.byAuthor("noone");
        assertEquals(404, resp.getStatusCodeValue());
    }

    @Test
    void postCreatesBlueprint() throws Exception {
        var req = new BlueprintsAPIController.NewBlueprintRequest("john","kitchen", List.of(new Point(1,1)));
        BlueprintsAPIController controller = new BlueprintsAPIController(services);
        var resp = controller.add(req);
        assertEquals(201, resp.getStatusCodeValue());
        verify(services, times(1)).addNewBlueprint(any(Blueprint.class));
    }

    @Test
    void putAddPointReturnsAccepted() throws Exception {
        var p = new Point(3,4);
        BlueprintsAPIController controller = new BlueprintsAPIController(services);
        var resp = controller.addPoint("john","kitchen", p);
        assertEquals(202, resp.getStatusCodeValue());
        verify(services, times(1)).addPoint("john","kitchen",3,4);
    }
}
