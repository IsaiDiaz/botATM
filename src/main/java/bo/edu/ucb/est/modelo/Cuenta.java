package bo.edu.ucb.est.modelo;

public class Cuenta {
    private String moneda;
    private String tipo;
    private String nroCuenta;
    private double saldo;

    // constructor de la clase
    public Cuenta(String moneda, String tipo, String nroCuenta, double saldo) {
        this.moneda = moneda;
        this.tipo = tipo;
        this.nroCuenta = nroCuenta;
        this.saldo = saldo;
    }

    public Cuenta(String moneda,String tipo,String nroCuenta){
        this.moneda= moneda;
        this.tipo=tipo;
        this.nroCuenta= nroCuenta;
        this.saldo=0;
    }

    // getters y setters
    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNroCuenta() {
        return nroCuenta;
    }

    public void setNroCuenta(String nroCuenta) {
        this.nroCuenta = nroCuenta;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    //funcion para el retiro de dinero de una cuenta
    public boolean retirar(double monto){
        boolean resultado= false;
        // valida el monto de retiro
        if(monto>0 && monto<=saldo){
            // si es valido, se resta del saldo
            saldo-=monto;
            // resultado toma true como señal de que la transaccion fue exitosa
            resultado=true;
        }
        return resultado;
    }

    public boolean depositar(double monto){
        boolean resultado=false;
        // valida el monto a depositar
        if(monto>0){
            //si es correcto, se suma al saldo
            saldo+=monto;
            //toma true si la transaccion fue exitosa
            resultado=true;
        }
        return resultado;
    }
}
