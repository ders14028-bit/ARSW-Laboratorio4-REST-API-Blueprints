package edu.eci.arsw.blueprints.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuración del endpoint STOMP usado para colaboración en tiempo real
 * sobre los blueprints (Lab P4).
 *
 * - Los clientes se conectan al endpoint "/ws-blueprints" (coincide con lo que
 *   ya espera el cliente STOMP del front: brokerURL = `${API_BASE}/ws-blueprints`).
 * - Los mensajes que el cliente envía van prefijados con "/app" (p.ej. "/app/draw"),
 *   y son enrutados al @MessageMapping correspondiente en BlueprintsRealtimeController.
 * - Las actualizaciones que el servidor difunde a los clientes se publican
 *   bajo el prefijo "/topic" (p.ej. "/topic/blueprints.juan.plano-1").
 *
 * Se habilita SockJS como fallback por si el navegador o la red no permite
 * WebSockets nativos; el front puede usarlo o no (ver stompClient.js).
 */

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-blueprints")
                .setAllowedOriginPatterns("*") // en producción: restringir al origen real del front
                .withSockJS();

        // Si el front usa brokerURL nativo (sin SockJS), también se expone sin el sufijo SockJS:
        registry.addEndpoint("/ws-blueprints")
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic"); // broadcast servidor -> clientes
        registry.setApplicationDestinationPrefixes("/app"); // mensajes clientes -> servidor
    }
}
