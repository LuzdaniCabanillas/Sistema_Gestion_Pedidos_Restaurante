
package controlador;

/**
SERGIO
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Cajero;
import modelo.Mozo;
import utilitarios.ConexionMySQL;
public class EmpleadoPagoDAO {

    public List<Mozo> listarMozos() throws SQLException {
        List<Mozo> lista = new ArrayList<>();

        String sql = """
            SELECT e.id, e.nombre, e.dni, e.turno
            FROM Empleado e
            INNER JOIN Mozo m ON m.id = e.id
            ORDER BY e.nombre
            """;

        try (Connection cn = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Mozo mozo = new Mozo(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("dni"),
                    rs.getString("turno")
                );

                lista.add(mozo);
            }
        }

        return lista;
    }

    public List<Cajero> listarCajeros() throws SQLException {
        List<Cajero> lista = new ArrayList<>();

        String sql = """
            SELECT e.id, e.nombre, e.dni, e.turno
            FROM Empleado e
            INNER JOIN Cajero c ON c.id = e.id
            ORDER BY e.nombre
            """;

        try (Connection cn = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Cajero cajero = new Cajero(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("dni"),
                    rs.getString("turno")
                );

                lista.add(cajero);
            }
        }

        return lista;
    }
}


