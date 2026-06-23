package edu.eci.arsw.blueprints.realtime;

import edu.eci.arsw.blueprints.model.Point;

import java.util.List;

/**
 * Payload que el servidor difunde a todos los clientes suscritos al tópico
 * "/topic/blueprints.{author}.{name}".
 *
 * El front lo consume así (ver App.jsx):
 *   subscribeBlueprint(client, author, name, (upd) => drawAll({ points: upd.points }))
 *
 * Por eso el campo se llama "points": debe coincidir con upd.points en el cliente.
 */
public record BlueprintUpdate(String author, String name, List<Point> points) {
}