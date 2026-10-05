package controlador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import modelo.Plato;
import utilitarios.ConexionMySQL;



//Este CRUD es simplemente un DAO auxiliar para que FrmDetallePedido pueda mostrar los platos disponibles.


public class PlatoDAO {

    public List<Plato> listar() {

        List<Plato> lista =
                new ArrayList<>();

        String sql =
                "SELECT id, nombre, precio, "
                        + "descripcion, categoria, estado, stock "
                        + "FROM Plato "
                        + "WHERE estado = 'ACTIVO' "
                        + "ORDER BY nombre";

        try (
                Connection conexion =
                        ConexionMySQL.obtenerConexion();

                PreparedStatement ps =
                        conexion.prepareStatement(sql);

                ResultSet rs =
                        ps.executeQuery()
        ) {

            while (rs.next()) {

                Plato plato =
                        new Plato();

                plato.setId(
                        rs.getInt("id")
                );

                plato.setNombre(
                        rs.getString("nombre")
                );

                plato.setPrecio(
                        rs.getDouble("precio")
                );

                plato.setDescripcion(
                        rs.getString("descripcion")
                );

                plato.setCategoria(
                        rs.getString("categoria")
                );

                plato.setEstado(
                        rs.getString("estado")
                );

                plato.setStock(
                        rs.getInt("stock")
                );

                lista.add(plato);
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al listar platos: "
                            + e.getMessage()
            );
        }

        return lista;
    }
}