package cl.speedfast.dao;

import cl.speedfast.conexion.ConexionBD;
import cl.speedfast.modelo.EstadoPedido;
import cl.speedfast.modelo.Pedido;
import cl.speedfast.modelo.PedidoComida;
import cl.speedfast.modelo.PedidoEncomienda;
import cl.speedfast.modelo.PedidoExpress;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    // ==========================================
    // GUARDAR PEDIDO
    // ==========================================

    public boolean guardar(Pedido pedido) {

        String sql = """
                INSERT INTO pedido (direccion, tipo, estado)
                VALUES (?, ?, ?)
                """;

        try (
                Connection conexion = ConexionBD.conectar();
                PreparedStatement statement = conexion.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS
                )
        ) {

            statement.setString(1, pedido.getDireccionEntrega());
            statement.setString(2, obtenerTipo(pedido));
            statement.setString(3, pedido.getEstado().name());

            int filasInsertadas = statement.executeUpdate();

            if (filasInsertadas > 0) {

                try (ResultSet claves = statement.getGeneratedKeys()) {

                    if (claves.next()) {
                        pedido.setId(claves.getInt(1));
                    }
                }

                return true;
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al guardar el pedido: "
                            + e.getMessage()
            );
        }

        return false;
    }


    // ==========================================
    // ACTUALIZAR ESTADO
    // ==========================================

    public boolean actualizarEstado(Pedido pedido) {

        String sql = """
                UPDATE pedido
                SET estado = ?
                WHERE id = ?
                """;

        try (
                Connection conexion = ConexionBD.conectar();
                PreparedStatement statement =
                        conexion.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    pedido.getEstado().name()
            );

            statement.setInt(
                    2,
                    pedido.getId()
            );

            int filasActualizadas =
                    statement.executeUpdate();

            return filasActualizadas > 0;

        } catch (SQLException e) {

            System.out.println(
                    "Error al actualizar el estado del pedido: "
                            + e.getMessage()
            );

            return false;
        }
    }


    // ==========================================
    // LISTAR TODOS LOS PEDIDOS
    // ==========================================

    public List<Pedido> listarTodos() {

        List<Pedido> pedidos = new ArrayList<>();

        String sql = """
                SELECT id, direccion, tipo, estado
                FROM pedido
                ORDER BY id
                """;

        try (
                Connection conexion = ConexionBD.conectar();
                PreparedStatement statement =
                        conexion.prepareStatement(sql);
                ResultSet resultado =
                        statement.executeQuery()
        ) {

            while (resultado.next()) {

                int id =
                        resultado.getInt("id");

                String direccion =
                        resultado.getString("direccion");

                String tipo =
                        resultado.getString("tipo");

                String estado =
                        resultado.getString("estado");

                Pedido pedido;

                switch (tipo.toUpperCase()) {

                    case "COMIDA":
                        pedido = new PedidoComida(
                                id,
                                direccion
                        );
                        break;

                    case "ENCOMIENDA":
                        pedido = new PedidoEncomienda(
                                id,
                                direccion
                        );
                        break;

                    case "EXPRESS":
                        pedido = new PedidoExpress(
                                id,
                                direccion
                        );
                        break;

                    default:
                        System.out.println(
                                "Tipo de pedido desconocido: "
                                        + tipo
                        );
                        continue;
                }

                // Recuperar también el estado almacenado en MySQL
                pedido.setEstado(
                        EstadoPedido.valueOf(
                                estado.toUpperCase()
                        )
                );

                pedidos.add(pedido);
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al listar los pedidos: "
                            + e.getMessage()
            );
        }

        return pedidos;
    }


    // ==========================================
    // OBTENER TIPO
    // ==========================================

    private String obtenerTipo(Pedido pedido) {

        if (pedido instanceof PedidoComida) {
            return "COMIDA";
        }

        if (pedido instanceof PedidoEncomienda) {
            return "ENCOMIENDA";
        }

        if (pedido instanceof PedidoExpress) {
            return "EXPRESS";
        }

        throw new IllegalArgumentException(
                "Tipo de pedido no reconocido."
        );
    }
}