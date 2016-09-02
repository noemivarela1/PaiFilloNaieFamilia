package paifillonaiefamiliacliente;
import java.awt.Color;
import java.util.Observable;
/**
 *
 * @author noemi
 */
public class DatosXogo extends Observable{
    public static int numXogadores=0;
    public DatosXogo(){
       
    }
    private boolean turno;
    private Color corCliente;
    private Color corOutro;
    private String movPropio;
    private String movOutro;
    private boolean ganhador=false;
    private Color corGanhador=Color.WHITE;
    public void setCorGanhador(Color corGanhador){
        this.corGanhador=corGanhador;
    }
    public Color getCorGanhador(){
        return corGanhador;
    }
    public void setGanhador(boolean ganhador){
        this.ganhador=ganhador;
        setChanged();
        notifyObservers(ganhador);
    }
    public boolean getGanhador(){
        return ganhador;
    }
    public void setMovPropio(String movPropio){
        this.movPropio=movPropio;
        setChanged();
        notifyObservers(movPropio);
    }
    public String getMovPropio(){
        return movPropio;
    }
    public void setMovOutro(String movOutro){
        //System.out.println("entra en setMovOutro con "+movOutro);
        this.movOutro=movOutro;
        setChanged();
        notifyObservers("O"+movOutro);
    }
    public String getMovOutro(){
        return movOutro;
    }
    public void setTurno(boolean turno){
        //System.out.println("entra en setTurno con turno a:"+turno);
        this.turno=turno;
        setChanged();
        notifyObservers(turno);
    }
    public boolean getTurno(){
        return turno;
    }
    public void setCorCliente(Color corCliente){
        this.corCliente=corCliente;
        setChanged();
        notifyObservers(corCliente);
    }
    public Color getCorCliente(){
        return corCliente;
    }
    public void setCorOutro(Color corOutro){
        this.corOutro=corOutro;
        setChanged();
        notifyObservers(corOutro);
    }
    public Color getCorOutro(){
        return corOutro;
    }
}


