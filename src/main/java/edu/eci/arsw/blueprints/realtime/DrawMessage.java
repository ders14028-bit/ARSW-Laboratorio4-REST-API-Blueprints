package edu.eci.arsw.blueprints.realtime;

import edu.eci.arsw.blueprints.model.Point;

/**
 * Payload que el front envía a "/app/draw" al hacer clic en el canvas.
 * Coincide exactamente con lo que envía App.jsx:
 *   { author, name, point: { x, y } }
 */
public record DrawMessage(String author, String name, Point point) {
}