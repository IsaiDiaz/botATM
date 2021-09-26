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
        this.cuentas= new ArrayList<>();
    }

    public Cliente(){
        this.nombreCliente=null;
        this.pin=null;
        this.id=null;
        this.cuentas= new ArrayList<>();
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

    public String toString(){
        return ("Nombre: "+getNombreCliente()+"\nId: "+getId()+"Cuentas: "+getCuentas().toString());
    }

    public Cuenta buscarCuenta(int indice){
       return getCuentas().get(indice);
    }

    public String mostrarCuentas(){
        String r="";
        int index=1;
        for (Cuenta cuenta:cuentas) {
            r=r+index+":\n"+cuenta.toString();
        }
        return r;
    }
}
