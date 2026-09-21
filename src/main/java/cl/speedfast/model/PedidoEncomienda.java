package cl.speedfast.model;

public class PedidoEncomienda extends Pedido {

    private static final int TIEMPO_BASE_MINUTOS = 20;
    private static final double MINUTOS_POR_KILOMETRO = 1.5;

    public PedidoEncomienda(int idPedido, String direccionEntrega) {
        this(idPedido, direccionEntrega, 1);
    }

    public PedidoEncomienda(int idPedido, String direccionEntrega, int distanciaKm) {
        super(idPedido, direccionEntrega, distanciaKm);
    }

    @Override
    public int calcularTiempoEntrega() {
        return (int) Math.round(TIEMPO_BASE_MINUTOS
                + MINUTOS_POR_KILOMETRO * getDistanciaKm());
    }


    @Override
    public void asignarRepartidor() {
        this.repartidorAsignado = "Daniela Tapia (Repartidor Encomienda)";
    }
}