package bo.edu.ucb.est.iu;

import java.util.List;

public class Mensaje {
    private List<String> mensajes;

    public Mensaje(List<String> mensajes) {
        this.mensajes = mensajes;
    }

    public List<String> getMensajes() {
        return mensajes;
    }

    public void setMensajes(List<String> mensajes) {
        this.mensajes = mensajes;
    }
}
