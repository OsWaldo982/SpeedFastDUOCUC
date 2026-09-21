package cl.speedfast.model;

public class PedidoComida extends Pedido {

    private static final int TIEMPO_BASE_MINUTOS = 15;
    private static final int MINUTOS_POR_KILOMETRO = 2;

    public PedidoComida(int idPedido, String direccionEntrega) {
        this(idPedido, direccionEntrega, 1);
    }

    public PedidoComida(int idPedido, String direccionEntrega, int distanciaKm) {
        super(idPedido, direccionEntrega, distanciaKm);
    }

    @Override
    public int calcularTiempoEntrega() {
        return TIEMPO_BASE_MINUTOS + (MINUTOS_POR_KILOMETRO * getDistanciaKm());
    }


    @Override
    public void asignarRepartidor() {
        this.repartidorAsignado = "Luis Díaz (Repartidor de Comida)";
    }
}