package bo.edu.ucb.est.bot;

import bo.edu.ucb.est.modelo.Banco;
import bo.edu.ucb.est.modelo.Cliente;
import bo.edu.ucb.est.modelo.Cuenta;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

public class BotATM extends TelegramLongPollingBot {
    private final Banco banco;
    private final SendMessage mensaje= new SendMessage();
    private final Map <String,Integer> usuarios=new HashMap<>();
    private final Map<String,Cuenta> cuentaMap =new HashMap<>();
    private final Map <String,String> monedaMap= new HashMap<>();
    private final Map <String,String> nombreMap= new HashMap<>();
    //private Map <String,Cliente> clienteMap= new HashMap<>();
    //private Cliente clienteActual;

    public BotATM(Banco banco){
        this.banco=banco;
        for (Cliente cliente:banco.getClientes()) {
            usuarios.put(cliente.getId(),3);
        }
    }

    @Override
    public String getBotUsername() {
        return "atm_ucb_bot";
    }

    @Override
    public String getBotToken() {
        return "";
    }

    @Override
    public void onUpdateReceived(Update update) {
        int cuenta;
        double monto;
        Cuenta c;
        Cuenta cuentaActual;
        Cliente clienteActual = null;
        System.out.println("LLego mensaje: "+update.getMessage().getText()+" de "+update.getMessage().getFrom().getFirstName()+" id: "+update.getMessage().getChatId().toString());
        if(update.hasMessage()){
            Message mensajeDeUsuario=update.getMessage();
            String id= update.getMessage().getChatId().toString();
            if(usuarios.get(id)==null){
                usuarios.put(id,0);
            }else {
                clienteActual= banco.buscarClientePorId(id);
            }
            mensaje.setChatId(id);
            int estadoClienteActual = usuarios.get(id);
            switch (estadoClienteActual){
                //registro de nuevo cliente
                case 0:
                    mensajeRegistrarCliente();
                    usuarios.replace(id,1);
                    break;
                case 1:
                    String nombreRegistro=mensajeDeUsuario.getText();
                    nombreMap.put(id,nombreRegistro);
                    mensajeRegistroPin();
                    usuarios.replace(id,2);
                    break;
                case 2:
                    String pinRegistro = mensajeDeUsuario.getText();
                    registroCliente(id, pinRegistro,nombreMap.get(id));
                    nombreMap.remove(id);
                    mensajeRegistroExitoso();
                    usuarios.replace(id,3);
                    break;
                    //Fin de registro de nuevo cliente
                //ingreso al sistema
                case 3://Saluda al cliente y solicita PIN para acceder
                    assert clienteActual != null;
                    ingresoAlSistema(clienteActual);
                    usuarios.replace(id,4);
                    break;
                case 4://Verifica PIN, si es correcto despliega el menu, de otra forma reinicia la verificacion
                    String pin=mensajeDeUsuario.getText();
                    assert clienteActual != null;
                    String pinCorrecto=clienteActual.getPin();
                    if(pin.equals(pinCorrecto)){
                        bienvenidaCliente();
                        despliegaMenu();
                        usuarios.replace(id,5);
                    }else{
                        pinIncorrecto();
                        ingresoAlSistema(clienteActual);
                    }
                    break;
                case 5://Recibe la opcion seleccionada por el cliente y actualiza su estado para procesarla
                    cuentaMap.remove(id);
                    int opcion=validaIngreso(mensajeDeUsuario.getText(),5);
                    if(opcion>=1 && opcion<=3){
                        assert clienteActual != null;
                        if(clienteActual.cantidadCuentas()>0) {
                            mostrarCuentas(clienteActual);//muestra las cuentas del cliente
                            int estado = usuarios.get(id);
                            usuarios.replace(id, estado + opcion);
                        }else{
                            clienteSinCuentas();
                            despliegaMenu();
                        }
                    }else if(opcion==4){
                        mensajeCrearCuenta();
                        usuarios.replace(id,9);
                    }else if(opcion==5){
                        salida();
                        usuarios.replace(id,3);
                    } else{
                        error();
                        despliegaMenu();
                    }
                    break;
                case 6://Muestra el saldo de una cuenta
                    assert clienteActual != null;
                    cuenta=validaIngreso(mensajeDeUsuario.getText(),clienteActual.getCuentas().size());
                    if(cuenta>0) {
                        cuentaActual = clienteActual.buscarCuenta(cuenta - 1);
                        mostrarSaldo(cuentaActual.getSaldo(),cuentaActual.getMoneda());
                        usuarios.replace(id, 5);
                        despliegaMenu();
                    }else{
                        cuentaNoValida();
                        despliegaMenu();
                        usuarios.replace(id,5);
                    }
                    break;
                case 7: //Realiza el retiro de una cuenta
                    assert clienteActual != null;
                    cuenta=validaIngreso(mensajeDeUsuario.getText(),clienteActual.getCuentas().size());
                    if(cuenta>0) {
                        cuentaActual = clienteActual.buscarCuenta(cuenta - 1);
                        cuentaMap.put(id, cuentaActual);
                        mensajeRetiro();
                        usuarios.replace(id, 10);
                    }else{
                        cuentaNoValida();
                        despliegaMenu();
                        usuarios.replace(id,5);
                    }
                    break;
                case 8: //Realiza deposito a una cuenta
                    assert clienteActual != null;
                    cuenta=validaIngreso(mensajeDeUsuario.getText(),clienteActual.getCuentas().size());
                    if(cuenta>0){
                        cuentaActual =clienteActual.buscarCuenta(cuenta-1);
                        cuentaMap.put(id, cuentaActual);
                        mensajeDeposito();
                        usuarios.replace(id,11);
                    }else{
                        cuentaNoValida();
                        despliegaMenu();
                        usuarios.replace(id,5);
                    }
                    break;
                case 9://crea una cuenta nueva
                    int eleccion=validaIngreso(mensajeDeUsuario.getText(),2);
                    String moneda;
                    switch (eleccion){
                        case 1:
                            moneda="Bolivianos";
                            monedaMap.put(id, moneda);
                            mensajeTipoDeCuenta();
                            usuarios.replace(id,12);
                            break;
                        case 2:
                            moneda="Dolares";
                            monedaMap.put(id, moneda);
                            mensajeTipoDeCuenta();
                            usuarios.replace(id,12);
                            break;
                        default:
                            error();
                            despliegaMenu();
                            usuarios.replace(id,5);
                            break;
                    }
                    break;
                case 10://efectua retiro
                    //monto=Double.parseDouble(mensajeDeUsuario.getText());
                    monto=validaIngresoDouble(mensajeDeUsuario.getText());
                    if(monto>0){
                        c= cuentaMap.get(id);
                        if(c.retirar(monto)){
                            exitoRetiro();
                        }else{
                            falloTransaccion();
                        }
                        usuarios.replace(id,5);
                        despliegaMenu();
                    }else{
                        usuarios.replace(id,5);
                        error();
                        despliegaMenu();
                    }
                    break;
                case 11://efectua deposito
                    //monto=Double.parseDouble(mensajeDeUsuario.getText());
                    monto=validaIngresoDouble(mensajeDeUsuario.getText());
                    if(monto>0){
                        c= cuentaMap.get(id);
                        if(c.depositar(monto)){
                            exitoDeposito();
                        }else{
                            falloTransaccion();
                        }
                        usuarios.replace(id,5);
                        despliegaMenu();
                    }else{
                        usuarios.replace(id,5);
                        error();
                        despliegaMenu();
                    }
                    break;
                case 12://efectua creacion de cuenta
                    int seleccion=validaIngreso(mensajeDeUsuario.getText(), 2);
                    String tipoDeCuenta;
                    switch(seleccion){
                        case 1:
                            tipoDeCuenta="Caja de ahorros";
                            assert clienteActual != null;
                            crearCuenta(monedaMap.get(id), tipoDeCuenta, clienteActual);
                            monedaMap.remove(id);
                            usuarios.replace(id,5);
                            despliegaMenu();
                            break;
                        case 2:
                            tipoDeCuenta="Cuenta corriente";
                            assert clienteActual != null;
                            crearCuenta(monedaMap.get(id), tipoDeCuenta, clienteActual);
                            monedaMap.remove(id);
                            usuarios.replace(id,5);
                            despliegaMenu();
                            break;
                        default:
                            error();
                            despliegaMenu();
                            monedaMap.remove(id);
                            usuarios.replace(id,5);
                            break;
                    }
                    break;
                default:
                    usuarios.replace(id,5);
                    despliegaMenu();
                    break;
            }
        }
    }

    private double validaIngresoDouble(String text) {
        try{
            return Double.parseDouble(text);
        }catch (Exception e){
            return -1;
        }
    }

    private void mensajeTipoDeCuenta(){
        mensaje.setText("Seleccione el tipo de cuenta:\n1.Caja de ahorros\n2.Cuenta corriente");
        ejecutarMensaje();
    }

    private void cuentaNoValida() {
        mensaje.setText("La cuenta seleccionada no existe");
        ejecutarMensaje();
    }

    private void falloTransaccion() {
        mensaje.setText("Fallo en la transacci??n");
        ejecutarMensaje();
    }

    private void salida() {
        mensaje.setText("Gracias por utilizar el servicio de BotATM ??Hasta la proxima!");
        ejecutarMensaje();
    }

    private void clienteSinCuentas() {
        mensaje.setText("Usted no tiene cuentas registradas, puede crear una cuenta en la opcion 4 del men??.");
        ejecutarMensaje();
    }

    private void exitoDeposito(){
        mensaje.setText("Deposito exitoso");
        ejecutarMensaje();
    }

    private void exitoRetiro() {
        mensaje.setText("Retiro exitoso");
        ejecutarMensaje();
    }

    private void mensajeDeposito() {
        mensaje.setText("Por favor, ingrese el monto a depositar. No es necesario especificar el tipo de moneda, envia solamente la cantidad como un numero");
        ejecutarMensaje();
    }

    private void mensajeRetiro() {
        mensaje.setText("Por favor, ingrese el monto a retirar. No es necesario especificar el tipo de moneda, envia solamente la cantidad como un numero");
        ejecutarMensaje();
    }

    private void bienvenida(){
        mensaje.setText("Bienvenido al "+banco.getNombre());
        ejecutarMensaje();
    }

    private void mensajeRegistrarCliente(){
        bienvenida();
        mensaje.setText("He notado que a??n no eres cliente, procedamos a registrarte");
        ejecutarMensaje();
        mensaje.setText("??Cual es tu nombre completo?");
        ejecutarMensaje();
    }

    private void mensajeRegistroPin(){
        mensaje.setText("Por favor elige un Pin de seguridad, este te sera requerido cada que ingreses al sistema");
        ejecutarMensaje();
    }

    private void mensajeRegistroExitoso(){
        mensaje.setText("Te hemos registrado con exito, por favor, envie cualquier mensaje para continuar");
        ejecutarMensaje();
    }

    private void ingresoAlSistema(Cliente clienteActual){
        mensaje.setText("Hola de nuevo "+clienteActual.getNombreCliente());
        ejecutarMensaje();
        mensaje.setText("Solo por seguridad ??cu??l es tu PIN?");
        ejecutarMensaje();
    }

    private void pinIncorrecto(){
        mensaje.setText("Lo siento, el codigo es incorrecto");
        ejecutarMensaje();
    }

    private void despliegaMenu(){
        mensaje.setText("Elige una opci??n:\n\n1. Ver Saldo.\n2. Retirar dinero.\n3. Depositar dinero.\n4. Crear cuenta.\n5. Salir.");
        ejecutarMensaje();
    }

    private void bienvenidaCliente(){
        mensaje.setText("Bienvenid@");
        ejecutarMensaje();
    }

    private void ejecutarMensaje(){
        try{
            execute(mensaje);
            System.out.println("Respondiendo con: "+mensaje.getText());
        }catch(TelegramApiException e){

            e.printStackTrace();
        }
    }

    private void error(){
        mensaje.setText("Algo salio mal (es posible que este ingresando un valor no valido) por favor, intentelo de nuevo");
        ejecutarMensaje();
    }

    private void mostrarSaldo(double saldo, String moneda) {
        mensaje.setText("El saldo de la cuenta seleccionada es: "+saldo+" "+moneda);
        ejecutarMensaje();
    }

    private void crearCuenta(String moneda,String tipoDeCuenta, Cliente clienteActual) {
        clienteActual.agregarCuenta(new Cuenta(moneda,tipoDeCuenta,(banco.numeroDeCuentas()+1)+"",0));
        mensaje.setText("Cuenta creada con exito");
        ejecutarMensaje();
    }

    private void mensajeCrearCuenta() {
        /*mensaje.setText("Por favor ingrese el tipo de moneda y el tipo de cuenta respetando el siguiente formato: \n"+
                "Bolivianos,Cuenta corriente\n" +
                "Dolares,Caja de ahorros\n");*/
        mensaje.setText("Seleccione la moneda:\n1.Bolivianos\n2.Dolares");
        ejecutarMensaje();
    }

    private void mostrarCuentas(Cliente clienteActual){
        mensaje.setText("Seleccione una cuenta:\n"+clienteActual.mostrarCuentas());
        ejecutarMensaje();
    }

    private int validaIngreso(String opcion,int rango){
        try{
            int r=Integer.parseInt(opcion);
            if(r>0 && r<=rango){
                return r;
            }else{
                return -1;
            }
        }catch (Exception e){
            error();
            return -1;
        }
    }

    private void registroCliente(String id,String pin,String nombre){
        Cliente cliente=new Cliente();
        cliente.setNombreCliente(nombre);
        cliente.setId(id);
        cliente.setPin(pin);
        banco.agregarCliente(cliente);
    }

}
