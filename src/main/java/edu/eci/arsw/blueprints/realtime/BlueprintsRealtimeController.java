package edu.eci.arsw.blueprints.realtime;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.services.BlueprintsServices;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * Controlador STOMP para colaboración en tiempo real sobre blueprints (Lab P4).
 *
 * Flujo:
 *  1. El front publica un punto a "/app/draw" con { author, name, point }.
 *  2. Este controlador persiste el punto reusando BlueprintsServices
 *     (la misma capa de servicio que ya usa el controlador REST del Lab 4,
 *     por lo que el punto queda guardado igual en Postgres o en memoria
 *     según el perfil activo).
 *  3. Se relee el blueprint actualizado (ya con el filtro aplicado, si hay
 *     uno activo: redundancy/undersampling) y se difunde a todos los
 *     clientes suscritos a "/topic/blueprints.{author}.{name}".
 *
 * No reemplaza al controlador REST (BlueprintsAPIController): ambos
 * coexisten y comparten la misma capa de servicio/persistencia.
 */
@Controller
public class BlueprintsRealtimeController {

    private final BlueprintsServices services;
    private final SimpMessagingTemplate messagingTemplate;

    public BlueprintsRealtimeController(BlueprintsServices services,
                                         SimpMessagingTemplate messagingTemplate) {
        this.services = services;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/draw")
    public void handleDraw(DrawMessage msg) {
        try {
            services.addPoint(msg.author(), msg.name(), msg.point().x(), msg.point().y());

            Blueprint updated = services.getBlueprint(msg.author(), msg.name());

            String topic = "/topic/blueprints." + msg.author() + "." + msg.name();
            messagingTemplate.convertAndSend(topic,
                    new BlueprintUpdate(msg.author(), msg.name(), updated.getPoints()));

        } catch (BlueprintNotFoundException e) {
            // El blueprint debe existir antes de poder dibujar en él (se crea vía REST).
            // Se ignora silenciosamente el punto para no romper la sesión STOMP del cliente;
            // si se prefiere notificar el error al cliente, se puede enviar a una cola
            // privada con messagingTemplate.convertAndSendToUser(...).
        }
    }
}