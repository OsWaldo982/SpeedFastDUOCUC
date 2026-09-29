package cl.speedfast.modelo;

import cl.speedfast.controlador.ZonaDeCarga;
import cl.speedfast.dao.EntregaDAO;
import cl.speedfast.dao.PedidoDAO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Representa a un repartidor de SpeedFast.
 *
 * Puede utilizarse para representar un repartidor
 * almacenado en MySQL y también como tarea concurrente
 * durante el proceso de entrega.
 */
public class Repartidor implements Runnable {

    private int id;
    private final String nombre;
    private final ZonaDeCarga zonaDeCarga;

    /**
     * Constructor utilizado por la lógica concurrente.
     */
    public Repartidor(
            int id,
            String nombre,
            ZonaDeCarga zonaDeCarga
    ) {

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del repartidor es obligatorio."
            );
        }

        this.id = id;
        this.nombre = nombre;
        this.zonaDeCarga = zonaDeCarga;
    }

    /**
     * Constructor compatible con la lógica anterior.
     */
    public Repartidor(
            String nombre,
            ZonaDeCarga zonaDeCarga
    ) {
        this(0, nombre, zonaDeCarga);
    }

    /**
     * Constructor utilizado por RepartidorDAO.
     */
    public Repartidor(int id, String nombre) {
        this(id, nombre, null);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public void run() {

        if (zonaDeCarga == null) {
            throw new IllegalStateException(
                    "Este repartidor no tiene una zona de carga asignada."
            );
        }

        System.out.println(
                "[Repartidor - "
                        + nombre
                        + "] Iniciando jornada."
        );

        while (!Thread.currentThread().isInterrupted()) {

            Pedido pedido =
                    zonaDeCarga.retirarPedido();

            if (pedido == null) {
                break;
            }

            pedido.asignarRepartidor(nombre);

            try {

                System.out.println(
                        "[Repartidor - "
                                + nombre
                                + "] Retirando pedido #"
                                + pedido.getIdPedido()
                );

                System.out.println(
                        "[Repartidor - "
                                + nombre
                                + "] Estado: "
                                + pedido.getEstado()
                );

                System.out.println(
                        "[Repartidor - "
                                + nombre
                                + "] Entregando pedido #"
                                + pedido.getIdPedido()
                );

                // Simula el tiempo de entrega.
                int tiempoEntrega =
                        ThreadLocalRandom
                                .current()
                                .nextInt(1000, 3001);

                Thread.sleep(tiempoEntrega);

                // ==========================================
                // CAMBIAR ESTADO EN JAVA
                // ==========================================

                pedido.despachar();

                System.out.println(
                        "[Repartidor - "
                                + nombre
                                + "] Pedido #"
                                + pedido.getIdPedido()
                                + " entregado."
                );

                // ==========================================
                // ACTUALIZAR ESTADO EN MYSQL
                // ==========================================

                PedidoDAO pedidoDAO =
                        new PedidoDAO();

                boolean estadoActualizado =
                        pedidoDAO.actualizarEstado(pedido);

                if (estadoActualizado) {

                    System.out.println(
                            "Estado del pedido #"
                                    + pedido.getIdPedido()
                                    + " actualizado a "
                                    + pedido.getEstado()
                                    + " en MySQL."
                    );

                } else {

                    System.out.println(
                            "No fue posible actualizar el estado "
                                    + "del pedido #"
                                    + pedido.getIdPedido()
                                    + " en MySQL."
                    );
                }

                // ==========================================
                // REGISTRAR ENTREGA EN MYSQL
                // ==========================================

                Entrega entrega =
                        new Entrega(
                                pedido.getIdPedido(),
                                id,
                                LocalDate.now(),
                                LocalTime.now()
                        );

                EntregaDAO entregaDAO =
                        new EntregaDAO();

                boolean entregaGuardada =
                        entregaDAO.guardar(entrega);

                if (entregaGuardada) {

                    System.out.println(
                            "Entrega guardada en MySQL. ID: "
                                    + entrega.getId()
                    );

                } else {

                    System.out.println(
                            "No fue posible guardar la entrega "
                                    + "del pedido #"
                                    + pedido.getIdPedido()
                    );
                }

                System.out.println(
                        "[Repartidor - "
                                + nombre
                                + "] Estado final: "
                                + pedido.getEstado()
                );

            } catch (InterruptedException e) {

                System.out.println(
                        "[Repartidor - "
                                + nombre
                                + "] Proceso interrumpido durante "
                                + "la entrega del pedido #"
                                + pedido.getIdPedido()
                );

                Thread.currentThread().interrupt();
                return;

            } catch (RuntimeException e) {

                System.out.println(
                        "[Repartidor - "
                                + nombre
                                + "] Error con pedido #"
                                + pedido.getIdPedido()
                                + ": "
                                + e.getMessage()
                );
            }
        }

        System.out.println(
                "[Repartidor - "
                        + nombre
                        + "] No quedan pedidos. Jornada finalizada."
        );
    }

    @Override
    public String toString() {
        return nombre;
    }
}