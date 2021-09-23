package bo.edu.ucb.est.modelo;

import java.util.ArrayList;
import java.util.List;

public class Cliente {
    private String nombreCliente;
    private String pin;
    private String id; // id del chat que usara el bot de telegram
    private List<Cuenta> cuentas;

    //constructor de la clase
    public Cliente(String nombreCliente, String pin, String id) {
        this.nombreCliente = nombreCliente;
        this.pin = pin;
        this.id = id;
        this.cuentas= new ArrayList<Cuenta>();
    }

    public Cliente(){
        this.nombreCliente=null;
        this.pin=pin;
        this.id=null;
        this.cuentas= new ArrayList<Cuenta>();
    }

    //getters y setters
    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public List<Cuenta> getCuentas() {
        return cuentas;
    }

    public void setCuentas(List<Cuenta> cuentas) {
        this.cuentas = cuentas;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void agregarCuenta(Cuenta cuenta){
        getCuentas().add(cuenta);
    }
}
