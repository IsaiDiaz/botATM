package bo.edu.ucb.est.bot;

import bo.edu.ucb.est.iu.Mensaje;
import bo.edu.ucb.est.modelo.Banco;
import bo.edu.ucb.est.modelo.Cliente;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

public class BotATM extends TelegramLongPollingBot {
    private Banco banco;
    private SendMessage mensaje= new SendMessage();
    private Map <String,Integer> usuarios=new HashMap<>();
    private Cliente clienteActual;
    private String nombreRegistro;
    private String pinRegistro;

    public BotATM(Banco banco){
        this.banco=banco;
    }

    @Override
    public String getBotUsername() {
        return "atm_ucb_bot";
    }

    @Override
    public String getBotToken() {
        return "";
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
    }

    private void ejecutarMensaje(){
        try{
            execute(mensaje);
        }catch(TelegramApiException e){
            e.printStackTrace();
        }
    }

    private Cliente registroCliente(String id,String pin,String nombre){
        Cliente cliente=new Cliente();
        cliente.setNombreCliente(nombre);
        cliente.setId(id);
        cliente.setPin(pin);
        banco.agregarCliente(cliente);
        return cliente;
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("LLego mensaje: "+update.getMessage().getText()+" de "+update.getMessage().getFrom().getFirstName());
        int estado;
        if(update.hasMessage()){
            Message mensajeDeUsuario=update.getMessage();
            String id= update.getMessage().getChatId().toString();
            clienteActual= banco.buscarClientePorId(id);
            if(clienteActual==null){
                usuarios.put(id,0);
            }
            mensaje.setChatId(id);
            estado=usuarios.get(id);
            switch (estado){
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
                    pinRegistro=mensajeDeUsuario.getText();
                    registroCliente(id,pinRegistro,nombreRegistro);
                    pinRegistro=null;
                    nombreRegistro=null;
                    mensajeRegistroExitoso();
                    usuarios.replace(id,3);
                    break;
                case 3:

            }
        }
    }

}
