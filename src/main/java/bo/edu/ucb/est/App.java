package bo.edu.ucb.est;

import bo.edu.ucb.est.bot.BotATM;
import bo.edu.ucb.est.modelo.Banco;
import bo.edu.ucb.est.modelo.Cliente;
import bo.edu.ucb.est.modelo.Cuenta;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Banco banco = new Banco("Banco de la Fortuna");
        Cliente cliente = new Cliente("Juan Perez","3333","");
        //Cliente yo= new Cliente("Jose Isai Diaz Soza","12345","809463279");
        Cuenta cuenta1= new Cuenta("Bolivianos","Caja de Ahorros","111122");
        cliente.agregarCuenta(cuenta1);
        banco.agregarCliente(cliente);
        //banco.agregarCliente(yo);
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new BotATM(banco));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
