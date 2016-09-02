package paifillonaiefamiliacliente;
import java.awt.Color;
import java.util.Observable;
/**
 *
 * @author noemi
 */
public class DatosConexionCliente extends Observable{
    private int porto;
    private String nomeServidor;
    private String nickCliente;
    private Color corCliente;
    public void setCorCliente(Color corCliente){
        this.corCliente=corCliente;
    }
    public Color getCorCliente(){
        return corCliente;
    }
    public void setNickCliente(String nickCliente){
        this.nickCliente=nickCliente;
    }
    public String getNickCliente(){
        return nickCliente;
    }
    public void setNomeServidor(String nomeServidor){
        this.nomeServidor=nomeServidor;
    }
    public String getNomeServidor(){
        return nomeServidor; 
    }
    public  void setPorto(int porto){
        this.porto=porto;
    }
    public int getPorto(){
        return porto;
    }
    DatosConexionCliente() {	
        super();
    }
    void changeData(Object data) {
        setChanged(); // the two methods of Observable class
        notifyObservers(data);
    }
}