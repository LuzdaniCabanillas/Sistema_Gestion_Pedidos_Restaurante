
package controlador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Mozo;
import utilitarios.ConexionMySQL;


public class MozoDAO {
    

    public List<Mozo> listar() {

        List<Mozo> lista = new ArrayList<>();

        String sql = """
                SELECT e.id, e.nombre, e.dni, e.turno
                FROM Empleado e
                INNER JOIN Mozo m ON e.id = m.id
                ORDER BY e.nombre
                """;

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Mozo mozo = new Mozo();

                mozo.setId(rs.getInt("id"));
                mozo.setNombre(rs.getString("nombre"));
                mozo.setDni(Integer.parseInt(rs.getString("dni")));
                mozo.setTurno(rs.getString("turno"));

                lista.add(mozo);
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al listar mozos: "
                    + e.getMessage()
            );
        }

        return lista;
    }
}