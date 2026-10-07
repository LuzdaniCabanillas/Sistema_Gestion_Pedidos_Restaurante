/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;
import utilitarios.ConexionMySQL;
import modelo.Cuenta;
import modelo.EstadoCuenta;
import modelo.MetodoPago;
import modelo.Pago;

/**
 *
 * @author sergi
 */
public class PagoDAO {
    
     // CREATE
    public boolean insertar(Pago pago) {

        String sql =
                "INSERT INTO pago "
                + "(fecha_hora, metodo_pago, monto, cuenta_id) "
                + "VALUES (?, ?, ?, ?)";

        try (
            Connection cn = ConexionMySQL.getConexion();
            PreparedStatement ps = cn.prepareStatement(sql)
        ) {

            ps.setTimestamp(
                    1,
                    Timestamp.valueOf(
                            pago.getFechaHora()
                    )
            );

            ps.setString(
                    2,
                    pago.getMetodoPago().name()
            );

            ps.setDouble(
                    3,
                    pago.getMonto()
            );

            ps.setInt(
                    4,
                    pago.getCuentaId()
            );

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println(
                    "Error al insertar pago: "
                    + e.getMessage()
            );

            return false;
        }
    }

    // MAPEAR
    private Pago mapear(ResultSet rs)
            throws SQLException {

        Pago pago = new Pago();

        pago.setId(
                rs.getInt("id")
        );

        pago.setFechaHora(
                rs.getTimestamp("fecha_hora")
                        .toLocalDateTime()
        );

        pago.setMetodoPago(
                MetodoPago.valueOf(
                        rs.getString("metodo_pago")
                )
        );

        pago.setMonto(
                rs.getDouble("monto")
        );

        pago.setCuentaId(
                rs.getInt("cuenta_id")
        );

        return pago;
    }

    // READ - TODOS
    public List<Pago> listar() {

        List<Pago> lista =
                new ArrayList<>();

        String sql =
                "SELECT * FROM pago "
                + "ORDER BY id DESC";

        try (
            Connection cn = ConexionMySQL.getConexion();
            PreparedStatement ps =
                    cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al listar pagos: "
                    + e.getMessage()
            );
        }

        return lista;
    }

    // READ - UNO
    public Pago buscarPorId(int id) {

        String sql =
                "SELECT * FROM pago WHERE id=?";

        try (
            Connection cn = ConexionMySQL.getConexion();
            PreparedStatement ps =
                    cn.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            try (ResultSet rs =
                    ps.executeQuery()) {

                if (rs.next()) {
                    return mapear(rs);
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al buscar pago: "
                    + e.getMessage()
            );
        }

        return null;
    }

    // UPDATE
    public boolean actualizar(Pago pago) {

        String sql =
                "UPDATE pago SET "
                + "metodo_pago=?, monto=? "
                + "WHERE id=?";

        try (
            Connection cn = ConexionMySQL.getConexion();
            PreparedStatement ps =
                    cn.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    pago.getMetodoPago().name()
            );

            ps.setDouble(
                    2,
                    pago.getMonto()
            );

            ps.setInt(
                    3,
                    pago.getId()
            );

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println(
                    "Error al actualizar pago: "
                    + e.getMessage()
            );

            return false;
        }
    }

    // DELETE
    public boolean eliminar(int id) {

        String sql =
                "DELETE FROM pago WHERE id=?";

        try (
            Connection cn = ConexionMySQL.getConexion();
            PreparedStatement ps =
                    cn.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println(
                    "Error al eliminar pago: "
                    + e.getMessage()
            );

            return false;
        }
    }
    
}
