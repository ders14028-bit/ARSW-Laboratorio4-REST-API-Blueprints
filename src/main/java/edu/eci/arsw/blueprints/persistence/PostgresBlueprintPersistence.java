package edu.eci.arsw.blueprints.persistence;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Repository
@Profile("postgres")
public class PostgresBlueprintPersistence implements BlueprintPersistence {

    private final JdbcTemplate jdbc;

    public PostgresBlueprintPersistence(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    @Transactional
    public void saveBlueprint(Blueprint bp) throws BlueprintPersistenceException {
        try {
            jdbc.update("INSERT INTO blueprints (author, name) VALUES (?, ?)",
                    bp.getAuthor(), bp.getName());
        } catch (DuplicateKeyException e) {
            throw new BlueprintPersistenceException(
                    "Blueprint already exists: " + bp.getAuthor() + "/" + bp.getName());
        }
        List<Point> pts = bp.getPoints();
        for (int i = 0; i < pts.size(); i++) {
            jdbc.update("INSERT INTO blueprint_points (author, name, x, y, point_order) VALUES (?, ?, ?, ?, ?)",
                    bp.getAuthor(), bp.getName(), pts.get(i).x(), pts.get(i).y(), i);
        }
    }

    @Override
    public Blueprint getBlueprint(String author, String name) throws BlueprintNotFoundException {
        List<Blueprint> result = jdbc.query(
                "SELECT author, name FROM blueprints WHERE author = ? AND name = ?",
                (rs, rn) -> new Blueprint(rs.getString("author"), rs.getString("name"), new ArrayList<>()),
                author, name);

        if (result.isEmpty()) {
            throw new BlueprintNotFoundException("Blueprint not found: " + author + "/" + name);
        }

        Blueprint bp = result.get(0);
        List<Point> points = jdbc.query(
                "SELECT x, y FROM blueprint_points WHERE author = ? AND name = ? ORDER BY point_order",
                (rs, rn) -> new Point(rs.getInt("x"), rs.getInt("y")),
                author, name);

        points.forEach(bp::addPoint);
        return bp;
    }

    @Override
    public Set<Blueprint> getBlueprintsByAuthor(String author) throws BlueprintNotFoundException {
        List<String> names = jdbc.query(
                "SELECT name FROM blueprints WHERE author = ?",
                (rs, rn) -> rs.getString("name"),
                author);

        if (names.isEmpty()) {
            throw new BlueprintNotFoundException("No blueprints for author: " + author);
        }

        Set<Blueprint> set = new HashSet<>();
        for (String name : names) {
            try { set.add(getBlueprint(author, name)); }
            catch (BlueprintNotFoundException ignored) { }
        }
        return set;
    }

    @Override
    public Set<Blueprint> getAllBlueprints() {
        List<String[]> pairs = jdbc.query(
                "SELECT author, name FROM blueprints",
                (rs, rn) -> new String[]{rs.getString("author"), rs.getString("name")});

        Set<Blueprint> set = new HashSet<>();
        for (String[] pair : pairs) {
            try { set.add(getBlueprint(pair[0], pair[1])); }
            catch (BlueprintNotFoundException ignored) { }
        }
        return set;
    }

    @Override
    @Transactional
    public void addPoint(String author, String name, int x, int y) throws BlueprintNotFoundException {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM blueprints WHERE author = ? AND name = ?",
                Integer.class, author, name);

        if (count == null || count == 0) {
            throw new BlueprintNotFoundException("Blueprint not found: " + author + "/" + name);
        }

        Integer maxOrder = jdbc.queryForObject(
                "SELECT COALESCE(MAX(point_order), -1) FROM blueprint_points WHERE author = ? AND name = ?",
                Integer.class, author, name);

        jdbc.update("INSERT INTO blueprint_points (author, name, x, y, point_order) VALUES (?, ?, ?, ?, ?)",
                author, name, x, y, (maxOrder == null ? 0 : maxOrder + 1));
    }
}
