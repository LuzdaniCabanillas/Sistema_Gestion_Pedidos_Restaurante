package controlador;

import utilitarios.ConexionMySQL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;
import modelo.Cuenta;
import modelo.EstadoCuenta;


public class CuentaDAO {       
    
     // CREATE
    public boolean insertar(Cuenta cuenta) {

        String sql =
            "INSERT INTO cuenta "
            + "(fecha_generacion, subtotal, igv, total, estado) "
            + "VALUES (?, ?, ?, ?, ?)";

    try (
        Connection cn = ConexionMySQL.getConexion();
        PreparedStatement ps = cn.prepareStatement(sql)
    ) {

          // 1 - Fecha de generación
        ps.setTimestamp(
                1,
                Timestamp.valueOf(
                        cuenta.getFechaGeneracion()
                )
        );

        // 2 - Subtotal
        ps.setDouble(
                2,
                cuenta.getSubTotal()
        );

        // 3 - IGV
        ps.setDouble(
                3,
                cuenta.getIgv()
        );

        // 4 - Total
        ps.setDouble(
                4,
                cuenta.getTotal()
        );

        // 5 - Estado
        ps.setString(
                5,
                cuenta.getEstado().name()
        );

        return ps.executeUpdate() > 0;

    } catch (SQLException e) {

        System.out.println(
                "Error al insertar cuenta: "
                + e.getMessage()
        );

        return false;
    }
    }

    // MAPEAR
    private Cuenta mapear(ResultSet rs)
            throws SQLException {

       Cuenta cuenta = new Cuenta(
            rs.getDouble("subtotal"),
            rs.getDouble("igv"),
            rs.getDouble("total"),
            EstadoCuenta.valueOf(
                    rs.getString("estado")
            )
    );

    // Recuperamos el ID autogenerado de MySQL
    cuenta.setId(
            rs.getInt("id")
    );

    return cuenta;
        
        
    }

    // READ - TODOS
    public List<Cuenta> listar() {

        List<Cuenta> lista = new ArrayList<>();

        String sql =
                "SELECT * FROM cuenta ORDER BY id DESC";

        try (
            Connection cn = ConexionMySQL.getConexion();
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al listar cuentas: "
                    + e.getMessage()
            );
        }

        return lista;
    }

    // READ - UNO
    public Cuenta buscarPorId(int id) {

        String sql =
                "SELECT * FROM cuenta WHERE id=?";

        try (
            Connection cn = ConexionMySQL.getConexion();
            PreparedStatement ps = cn.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapear(rs);
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al buscar cuenta: "
                    + e.getMessage()
            );
        }

        return null;
    }

    // UPDATE
    public boolean actualizar(Cuenta cuenta) {

        String sql =
                "UPDATE cuenta SET "
                + "subtotal=?, igv=?, total=?, estado=? "
                + "WHERE id=?";

        try (
            Connection cn = ConexionMySQL.getConexion();
            PreparedStatement ps = cn.prepareStatement(sql)
        ) {

            ps.setDouble(1, cuenta.getSubTotal());
            ps.setDouble(2, cuenta.getIgv());
            ps.setDouble(3, cuenta.getTotal());

            ps.setString(
                    4,
                    cuenta.getEstado().name()
            );

            ps.setInt(5, cuenta.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println(
                    "Error al actualizar cuenta: "
                    + e.getMessage()
            );

            return false;
        }
    }

    // DELETE
    public boolean eliminar(int id) {

        String sql =
                "DELETE FROM cuenta WHERE id=?";

        try (
            Connection cn = ConexionMySQL.getConexion();
            PreparedStatement ps = cn.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println(
                    "Error al eliminar cuenta: "
                    + e.getMessage()
            );

            return false;
        }
    }
    
}
