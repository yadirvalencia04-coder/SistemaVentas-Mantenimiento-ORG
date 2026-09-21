package service;

import model.Cliente;
import model.Producto;
import model.Venta;
import repository.VentaRepository;
import util.Console;
import util.Validaciones;

public class VentaService {

    private ClienteService clienteService;
    private ProductoService productoService;
    private VentaRepository ventaRepo;

    private Venta ventaActual;

    public VentaService(ClienteService clienteService, ProductoService productoService, VentaRepository ventaRepo) {
        this.clienteService = clienteService;
        this.productoService = productoService;
        this.ventaRepo = ventaRepo;
    }

    public void crearVenta(String dniCliente) {

        Cliente cliente = clienteService.buscarCliente(dniCliente);

        if (cliente == null) {
            imprimirError("Cliente no existe");
            return;
        }

        ventaActual = new Venta(cliente);
        Console.info("Venta creada para: " + cliente.getNombre());
    }

    public void agregarProductoVenta(int idProducto, int cantidad) {

        if (!validarVentaActiva()) return;

        Producto producto = productoService.buscarProducto(idProducto);

        if (producto == null) {
            imprimirError("Producto no encontrado");
            return;
        }

        if (!Validaciones.validarCantidad(cantidad)) {
            imprimirError("Cantidad inválida");
            return;
        }

        ventaActual.agregarDetalle(producto, cantidad);
        Console.info("Producto agregado: " + producto.getNombre() + " x" + cantidad);
    }

    public void finalizarVenta() {

        if (!validarVentaActiva()) return;

        ventaActual.finalizar();
        ventaRepo.guardar(ventaActual);

        Console.info("Venta finalizada. Total: " + ventaActual.calcularTotal());
        ventaActual = null;
    }

    public Venta obtenerVentaActual() {
        return ventaActual;
    }

    // ---- métodos privados (Refactor Semana 4) ----

    private void imprimirError(String msg) {
        Console.error(msg);
    }

    private boolean validarVentaActiva() {
        if (ventaActual == null) {
            imprimirError("No hay venta activa");
            return false;
        }
        return true;
    }
}