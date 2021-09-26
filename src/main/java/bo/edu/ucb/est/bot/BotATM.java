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
    //private Map <String,Cliente> clienteMap= new HashMap<>();
    //private Cliente clienteActual;
    private String nombreRegistro;

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
        return "2040972733:AAHasVnCXBYPkH8hRzYNxaNz80_x5PzQPcE";
    }

    private void bienvenida(){
        mensaje.setText("Bienvenido al "+banco.getNombre());
        ejecutarMensaje();
    }

    private void mensajeRegistrarCliente(){
        bienvenida();
        mensaje.setText("He notado que aún no eres cliente, procedamos a registrarte");
        ejecutarMensaje();
        mensaje.setText("¿Cual es tu nombre completo?");
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
        mensaje.setText("Solo por seguridad ¿cuál es tu PIN?");
        ejecutarMensaje();
    }

    private void pinIncorrecto(){
        mensaje.setText("Lo siento, el codigo es incorrecto");
        ejecutarMensaje();
    }

    private void despliegaMenu(){
        mensaje.setText("Elige una opción:\n\n1. Ver Saldo.\n2. Retirar dinero.\n3. Depositar dinero.\n4. Crear cuenta.\n5. Salir.");
        ejecutarMensaje();
    }

    private void bienvenidaCliente(){
        mensaje.setText("Bienvenido");
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

    private void mostrarSaldo(double saldo) {
        mensaje.setText("El saldo de la cuenta seleccionada es: "+saldo);
        ejecutarMensaje();
    }

    private void crearCuenta(String nuevaCuenta, Cliente clienteActual) {
        String [] datosDeCuenta=nuevaCuenta.split(",");
        clienteActual.agregarCuenta(new Cuenta(datosDeCuenta[0],datosDeCuenta[1],(banco.numeroDeCuentas()+1)+"",0));
        mensaje.setText("Cuenta creada con exito");
        ejecutarMensaje();
    }

    private void mensajeCrearCuenta() {
        mensaje.setText("Por favor ingrese el tipo de moneda y el tipo de cuenta de la siguiente manera: Bolivianos,Cuenta corriente");
        ejecutarMensaje();
    }

    private void mostrarCuentas(Cliente clienteActual){
        mensaje.setText(clienteActual.mostrarCuentas());
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
            despliegaMenu();
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

    @Override
    public void onUpdateReceived(Update update) {
        int cuenta;
        double monto;
        Cuenta c;
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
                case 0:
                    mensajeRegistrarCliente();
                    usuarios.replace(id,1);
                    break;
                case 1:
                    nombreRegistro=mensajeDeUsuario.getText();
                    mensajeRegistroPin();
                    usuarios.replace(id,2);
                    break;
                case 2:
                    String pinRegistro = mensajeDeUsuario.getText();
                    registroCliente(id, pinRegistro,nombreRegistro);
                    nombreRegistro=null;
                    mensajeRegistroExitoso();
                    usuarios.replace(id,3);
                    break;
                case 3:
                   ingresoAlSistema(clienteActual);
                   usuarios.replace(id,4);
                   break;
                case 4:
                    String pin=mensajeDeUsuario.getText();
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
                case 5:
                    int opcion=validaIngreso(mensajeDeUsuario.getText(),5);
                    if(opcion>=1 && opcion<=3){
                        mostrarCuentas(clienteActual);
                        int estado=usuarios.get(id);
                        usuarios.replace(id,estado+opcion);
                    }else if(opcion==4){
                        mensajeCrearCuenta();
                        usuarios.replace(id,9);
                    }else if(opcion==5){
                        usuarios.replace(id,3);
                    }
                    break;
                case 6://ver saldo
                    cuenta=validaIngreso(mensajeDeUsuario.getText(),clienteActual.getCuentas().size());
                    Cuenta cuentaActual = clienteActual.buscarCuenta(cuenta - 1);
                    mostrarSaldo(cuentaActual.getSaldo());
                    usuarios.replace(id,5);
                    break;
                case 7: //retiro
                    cuenta=validaIngreso(mensajeDeUsuario.getText(),clienteActual.getCuentas().size());
                    cuentaActual =clienteActual.buscarCuenta(cuenta-1);
                    cuentaMap.put(id, cuentaActual);
                    mensajeRetiro();
                    usuarios.replace(id,10);
                    break;
                case 8: //deposito
                    cuenta=validaIngreso(mensajeDeUsuario.getText(),clienteActual.getCuentas().size());
                    cuentaActual =clienteActual.buscarCuenta(cuenta-1);
                    cuentaMap.put(id, cuentaActual);
                    mensajeDeposito();
                    usuarios.replace(id,11);
                    break;
                case 9:
                    String nuevaCuenta=mensajeDeUsuario.getText();
                    crearCuenta(nuevaCuenta,clienteActual);
                    usuarios.replace(id,5);
                    break;
                case 10://efectua retiro
                    monto=Double.parseDouble(mensajeDeUsuario.getText());
                    c= cuentaMap.get(id);
                    c.retirar(monto);
                    exitoRetiro();
                    usuarios.replace(id,5);
                    break;
                case 11://efectua deposito
                    monto=Double.parseDouble(mensajeDeUsuario.getText());
                    c= cuentaMap.get(id);
                    c.depositar(monto);
                    exitoDeposito();
                    usuarios.replace(id,5);
                    break;
                /*case 12://efectua creacion de cuenta
                    String nuevaCuenta=mensajeDeUsuario.getText();
                    crearCuenta();
                    usuarios.replace(id,5);*/
                default:
                    usuarios.replace(id,5);
                    break;
            }
        }
    }

    private void exitoDeposito(){
        mensaje.setText("Deposito exitoso, envie cualquier mensaje para continuar");
        ejecutarMensaje();
    }

    private void exitoRetiro() {
        mensaje.setText("Retiro exitoso, envie cualquier mensaje para continuar");
        ejecutarMensaje();
    }

    private void mensajeDeposito() {
        mensaje.setText("Por favor, ingrese el monto a depositar");
        ejecutarMensaje();
    }

    private void mensajeRetiro() {
        mensaje.setText("Por favor, ingrese el monto a retirar");
        ejecutarMensaje();
    }


}
