package edu.eci.arsw.blueprints.controllers;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import edu.eci.arsw.blueprints.services.BlueprintsServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/blueprints")
@Tag(name = "Blueprints", description = "API para gestión de planos arquitectónicos")
public class BlueprintsAPIController {

    private final BlueprintsServices services;

    public BlueprintsAPIController(BlueprintsServices services) {
        this.services = services;
    }

    @Operation(summary = "Obtener todos los blueprints")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de blueprints retornada exitosamente")
    })
    @GetMapping
    public ResponseEntity<edu.eci.arsw.blueprints.controllers.ApiResponse<Set<Blueprint>>> getAll() {
        return ResponseEntity.ok(edu.eci.arsw.blueprints.controllers.ApiResponse.ok(services.getAllBlueprints()));
    }

    @Operation(summary = "Obtener blueprints por autor")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Blueprints del autor encontrados"),
        @ApiResponse(responseCode = "404", description = "Autor no encontrado")
    })
    @GetMapping("/{author}")
    public ResponseEntity<edu.eci.arsw.blueprints.controllers.ApiResponse<?>> byAuthor(
            @PathVariable String author) {
        try {
            return ResponseEntity.ok(edu.eci.arsw.blueprints.controllers.ApiResponse.ok(
                    services.getBlueprintsByAuthor(author)));
        } catch (BlueprintNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(edu.eci.arsw.blueprints.controllers.ApiResponse.error(e.getMessage()));
        }
    }

    @Operation(summary = "Obtener un blueprint por autor y nombre")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Blueprint encontrado"),
        @ApiResponse(responseCode = "404", description = "Blueprint no encontrado")
    })
    @GetMapping("/{author}/{bpname}")
    public ResponseEntity<edu.eci.arsw.blueprints.controllers.ApiResponse<?>> byAuthorAndName(
            @PathVariable String author, @PathVariable String bpname) {
        try {
            return ResponseEntity.ok(edu.eci.arsw.blueprints.controllers.ApiResponse.ok(
                    services.getBlueprint(author, bpname)));
        } catch (BlueprintNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(edu.eci.arsw.blueprints.controllers.ApiResponse.error(e.getMessage()));
        }
    }

    @Operation(summary = "Crear un nuevo blueprint")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Blueprint creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "409", description = "Blueprint ya existe")
    })
    @PostMapping
    public ResponseEntity<edu.eci.arsw.blueprints.controllers.ApiResponse<?>> add(
            @Valid @RequestBody NewBlueprintRequest req) {
        try {
            Blueprint bp = new Blueprint(req.author(), req.name(), req.points());
            services.addNewBlueprint(bp);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(edu.eci.arsw.blueprints.controllers.ApiResponse.ok(null));
        } catch (BlueprintPersistenceException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(edu.eci.arsw.blueprints.controllers.ApiResponse.error(e.getMessage()));
        }
    }

    @Operation(summary = "Agregar un punto a un blueprint existente")
    @ApiResponses({
        @ApiResponse(responseCode = "202", description = "Punto agregado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Blueprint no encontrado")
    })
    @PutMapping("/{author}/{bpname}/points")
    public ResponseEntity<edu.eci.arsw.blueprints.controllers.ApiResponse<?>> addPoint(
            @PathVariable String author, @PathVariable String bpname,
            @Valid @RequestBody Point p) {
        try {
            services.addPoint(author, bpname, p.x(), p.y());
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(edu.eci.arsw.blueprints.controllers.ApiResponse.ok(null));
        } catch (BlueprintNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(edu.eci.arsw.blueprints.controllers.ApiResponse.error(e.getMessage()));
        }
    }

    @Operation(summary = "Actualizar un blueprint existente (reemplaza todos sus puntos)")
    @ApiResponses({
        @ApiResponse(responseCode = "202", description = "Blueprint actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Blueprint no encontrado")
    })
    @PutMapping("/{author}/{bpname}")
    public ResponseEntity<edu.eci.arsw.blueprints.controllers.ApiResponse<?>> update(
            @PathVariable String author, @PathVariable String bpname,
            @Valid @RequestBody UpdateBlueprintRequest req) {
        try {
            Blueprint bp = new Blueprint(author, bpname, req.points());
            services.updateBlueprint(bp);
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(edu.eci.arsw.blueprints.controllers.ApiResponse.ok(null));
        } catch (BlueprintNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(edu.eci.arsw.blueprints.controllers.ApiResponse.error(e.getMessage()));
        }
    }

    @Operation(summary = "Eliminar un blueprint existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Blueprint eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Blueprint no encontrado")
    })
    @DeleteMapping("/{author}/{bpname}")
    public ResponseEntity<edu.eci.arsw.blueprints.controllers.ApiResponse<?>> delete(
            @PathVariable String author, @PathVariable String bpname) {
        try {
            services.deleteBlueprint(author, bpname);
            return ResponseEntity.ok(edu.eci.arsw.blueprints.controllers.ApiResponse.ok(null));
        } catch (BlueprintNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(edu.eci.arsw.blueprints.controllers.ApiResponse.error(e.getMessage()));
        }
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<edu.eci.arsw.blueprints.controllers.ApiResponse<?>> handleValidation(
            org.springframework.web.bind.MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .findFirst().orElse("Invalid request");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(edu.eci.arsw.blueprints.controllers.ApiResponse.error(msg));
    }

    public record NewBlueprintRequest(
            @NotBlank String author,
            @NotBlank String name,
            @Valid List<Point> points
    ) {}

    public record UpdateBlueprintRequest(
            @Valid List<Point> points
    ) {}
}