package cl.speedfast.modelo;

import cl.speedfast.interfaces.Cancelable;
import cl.speedfast.interfaces.Despachable;

/**
 * Clase base de los pedidos de SpeedFast.
 * Mantiene los datos comunes de las semanas anteriores e incorpora
 * el estado requerido para controlar el proceso concurrente de la Semana 5.
 */
public abstract class Pedido implements Despachable, Cancelable {

    private int id;
    private String direccionEntrega;
    private int distanciaKm;
    private EstadoPedido estado;
    protected String repartidorAsignado;
    protected boolean cancelado;

    public Pedido(int id, String direccionEntrega, int distanciaKm) {
        this.id = id;
        this.direccionEntrega = direccionEntrega;
        this.distanciaKm = distanciaKm;
        this.estado = EstadoPedido.PENDIENTE;
        this.repartidorAsignado = "Sin asignar";
        this.cancelado = false;
    }

    public int getId() {
        return id;
    }

    // Se conserva este getter por compatibilidad con el proyecto de semanas anteriores.
    public int getIdPedido() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIdPedido(int idPedido) {
        this.id = idPedido;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public void setDireccionEntrega(String direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }

    public int getDistanciaKm() {
        return distanciaKm;
    }

    public void setDistanciaKm(int distanciaKm) {
        this.distanciaKm = distanciaKm;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El estado del pedido no puede ser nulo.");
        }
        this.estado = nuevoEstado;
    }

    /**
     * Sobrecarga solicitada en la pauta. Permite actualizar el estado
     * usando texto, pero valida el valor mediante el enum EstadoPedido.
     */
    public void setEstado(String nuevoEstado) {
        if (nuevoEstado == null || nuevoEstado.isBlank()) {
            throw new IllegalArgumentException("El estado del pedido no puede estar vacio.");
        }
        this.estado = EstadoPedido.valueOf(nuevoEstado.trim().toUpperCase());
    }

    public String getRepartidorAsignado() {
        return repartidorAsignado;
    }

    public boolean isCancelado() {
        return cancelado;
    }

    public boolean isDespachado() {
        return estado == EstadoPedido.ENTREGADO;
    }

    // Polimorfismo por sobrecarga conservado desde las semanas anteriores.
    public void asignarRepartidor() {
        this.repartidorAsignado = "Repartidor Automatico";
    }

    public void asignarRepartidor(String nombreRepartidor) {
        this.repartidorAsignado = nombreRepartidor;
    }

    public void mostrarResumen() {
        System.out.println("[" + getClass().getSimpleName() + "]");
        System.out.println("Pedido #" + String.format("%03d", id));
        System.out.println("Direccion: " + direccionEntrega);
        System.out.println("Distancia: " + distanciaKm + " km");
        System.out.println("Estado: " + estado);
        System.out.println("Repartidor asignado: " + repartidorAsignado);
        System.out.println("Tiempo estimado: " + calcularTiempoEntrega() + " minutos");
    }

    @Override
    public void despachar() {
        if (cancelado) {
            throw new IllegalStateException(
                    "No se puede entregar el pedido #" + id + " porque fue cancelado."
            );
        }
        this.estado = EstadoPedido.ENTREGADO;
    }

    @Override
    public void cancelar() {
        if (estado == EstadoPedido.ENTREGADO) {
            System.out.println(
                    "No se puede cancelar el pedido #" + id + " porque ya fue entregado."
            );
            return;
        }

        this.cancelado = true;
        System.out.println("Pedido #" + id + " cancelado exitosamente.");
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()
                + "{id=" + id
                + ", direccionEntrega='" + direccionEntrega + '\''
                + ", estado=" + estado
                + '}';
    }

    public abstract int calcularTiempoEntrega();
}
