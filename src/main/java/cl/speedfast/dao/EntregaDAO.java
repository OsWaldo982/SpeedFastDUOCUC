package cl.speedfast.dao;

import cl.speedfast.conexion.ConexionBD;
import cl.speedfast.modelo.Entrega;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Time;

public class EntregaDAO {

    public boolean guardar(Entrega entrega) {

        String sql = """
                INSERT INTO entrega
                (id_pedido, id_repartidor, fecha, hora)
                VALUES (?, ?, ?, ?)
                """;

        try (
                Connection conexion = ConexionBD.conectar();
                PreparedStatement statement = conexion.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS
                )
        ) {

            statement.setInt(1, entrega.getIdPedido());
            statement.setInt(2, entrega.getIdRepartidor());
            statement.setDate(3, Date.valueOf(entrega.getFecha()));
            statement.setTime(4, Time.valueOf(entrega.getHora()));

            int filasInsertadas = statement.executeUpdate();

            if (filasInsertadas > 0) {

                try (ResultSet claves = statement.getGeneratedKeys()) {

                    if (claves.next()) {
                        entrega.setId(claves.getInt(1));
                    }
                }

                return true;
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error al guardar la entrega: "
                            + e.getMessage()
            );
        }

        return false;
    }
}