package bo.edu.ucb.est.modelo;
import java.util.ArrayList;
import java.util.List;

public class Banco {
private String nombre;
private List<Cliente> clientes;

//constructor de la clase
public Banco( String nombre){
    this.nombre=nombre;
    this.clientes=new ArrayList();
}

//getters y setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Cliente> getClientes() {
        return clientes;
    }

    public void agregarCliente(Cliente cliente){
        clientes.add(cliente);
    }

    //Busca un cliente por su nombre y devuelve el objeto Cliente que corresponda
    public Cliente buscarClientePorNombre(String nombreCliente){
    //itera por la lista de clientes
        for (Cliente cliente:clientes) {
            // verifica si el nombre ingresado es igual a alguno de los clientes en la lista
            if(cliente.getNombreCliente().equals(nombreCliente)){
                // si coincide, devuelve el cliente
                return cliente;
            }
        }
        // si no retorna null
        return null;
    }
    public Cliente buscarClientePorId(String id){
    for(Cliente cliente:clientes){
        if(cliente.getId().equals(id)){
            return cliente;
        }
    }
    return null;
    }

    public int numeroDeCuentas(){
        int cant=0;
        for (Cliente cliente:clientes) {
            cant+=cliente.cantidadCuentas();
        }
        cant=cant+100000;
        return cant;
    }
}
