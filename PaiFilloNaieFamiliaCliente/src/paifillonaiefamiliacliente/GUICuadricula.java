package paifillonaiefamiliacliente;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Observable;
import java.util.Observer;
import javax.swing.*;
/**
 *
 * @author noemi
 */
public class GUICuadricula  extends JPanel implements Observer{
    private DatosXogo datosXogo;
    private BoxListener box;
     public void setDatosXogo(DatosXogo datosXogo){
        this.datosXogo=datosXogo;
    }
    public DatosXogo getDatosXogo(){
        return datosXogo;
    }
 
    public void engadirObservadores(){
        for (int i=0;i<this.getComponentCount();i++){
            JPanel panel=(JPanel) this.getComponent(i);
            this.getDatosXogo().addObserver(this);
            MouseListener[] listeners=panel.getListeners(MouseListener.class);
            ((BoxListener) listeners[0]).setDatosXogo(datosXogo);
        }
        box.setDatosXogo(datosXogo);
    }
    @Override
    public void update(Observable o, Object arg) {
        //esperando a que chegue outra mensaxe do servidor
        if (arg instanceof MouseEvent){
            //aquí parece que non entra
        }
        else{
            if ((o instanceof DatosXogo) && (arg instanceof String)){
                //non ten o turno entón ven o movemento do outro xogador
                if ((!datosXogo.getTurno()) && (datosXogo.getMovOutro()!=null)) {
                    String mov=datosXogo.getMovOutro();
                    System.out.println("chega o movemento *"+mov);
                    JPanel jp= (JPanel) this.getComponent(Integer.parseInt(mov));
                    jp.setBackground(datosXogo.getCorOutro());
                    jp.setEnabled(false);
                    if ((jp.getMouseListeners().length)>=1)
                        jp.removeMouseListener(jp.getMouseListeners()[0]);
                    System.out.println("Vai facer comprobación de gañador");
                    if (!comprobarGanhador(jp.getName(),"O")){
                        datosXogo.setTurno(true);
                    }
                }
                //comproba gañador cando ten o turno
                if (datosXogo.getTurno()){
                    String mov=datosXogo.getMovPropio();
                    System.out.println("chega o movemento propio*"+mov);
                    JPanel jp= (JPanel) this.getComponent(Integer.parseInt(mov));
                    if ((jp.getMouseListeners().length)>=1)
                        jp.removeMouseListener(jp.getMouseListeners()[0]);
                    if (comprobarGanhador(jp.getName(),"C")){
                        datosXogo.setGanhador(true);
                    }
                }
            }
        }
    }  
    
    public GUICuadricula(int row, int col) {
        int count = 0 ; // use to give a name to each box so that you can refer to them later
        setLayout(new GridLayout(row, col));
        setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        for (int i = 1; i <= (row * col); i++) {
            JPanel pan = new JPanel();
            pan.setEnabled(true);
            pan.setBackground(Color.WHITE);
            pan.setPreferredSize(new Dimension(30, 30));
            pan.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            box=new BoxListener();
            pan.addMouseListener(box); // add a mouse listener to make the panels clickable
            pan.setName(count+"");
            ++count;
            add(pan);
        }
    }
    
    private int comprobarNS(String movP){
        int cont=1;
        //móvome cara ao sur
        int intMovP=Integer.parseInt(movP);
        int fila=intMovP/10;
        int columna=intMovP%10;
        int posIni=(fila+cont)*10+columna;
        if (fila+cont<=9){
            while ((posIni<=9*10+columna)&&(this.getComponent(intMovP).getBackground().equals(getComponent(posIni).getBackground()))){
                cont++;
                if (fila+cont<=9)
                    posIni=(fila+cont)*10+columna;
                else
                    posIni=9*10+columna+1;
            }
        }
        //móvome cara ao norte
        int cont1=1;
        if (fila-cont1>=0){
            posIni=(fila-cont1)*10+columna;
        
            while ((posIni>=columna)&&(this.getComponent(intMovP).getBackground().equals(getComponent(posIni).getBackground()))){
                cont1++;
                if (fila-cont1>=0)
                    posIni=(fila-cont1)*10+columna;
                else
                    posIni=columna-1;
            }
        }
        return cont+cont1-1;
    }
    private int comprobarEW(String movP){
        int cont=1;
        //móvome cara ao este
        int intMovP=Integer.parseInt(movP);
        int fila=intMovP/10;
        int columna=intMovP%10;
        int posIni=(fila*10)+(columna+cont);
        if (columna+cont<=9){
            while ((posIni>=fila*10)&&(this.getComponent(intMovP).getBackground().equals(getComponent(posIni).getBackground()))){
                cont++;
                if (columna+cont<=9)
                    posIni=(fila*10)+(columna+cont);
                else
                    posIni=(fila*10)-1;
            }
        }
        //móvome cara ao oeste
        int cont1=1;
        if (columna-cont1>=0){
            posIni=(fila*10)+(columna-cont1);
        
            while ((posIni<(fila+1)*10)&&(this.getComponent(intMovP).getBackground().equals(getComponent(posIni).getBackground()))){
                cont1++;
                if (columna-cont1>=0)
                    posIni=(fila*10)+(columna-cont1);
                else
                    posIni=(fila+1)*10;
            }
        }
        return cont+cont1-1;
    }
    private int comprobarNE_SW(String movP){
        int cont=1;
        //móvome cara ao NE
        int intMovP=Integer.parseInt(movP);
        int fila=intMovP/10;
        int columna=intMovP%10;
        int posIni=(fila-cont)*10+(columna+cont);
        while ((posIni>=0)&&(this.getComponent(intMovP).getBackground().equals(getComponent(posIni).getBackground()))){
            cont++;
            if ((fila-cont>=0) && (columna+cont<=9))
                posIni=(fila-cont)*10+(columna+cont);
            else
                posIni=-1;
        }
        //móvome cara ao SW
        int cont1=1;
        posIni=(fila+cont1)*10+(columna-cont1);
        while ((posIni<100)&&(this.getComponent(intMovP).getBackground().equals(getComponent(posIni).getBackground()))){
            cont1++;
            if ((fila+cont1<=9) && (columna-cont1>=0))
                posIni=(fila+cont1)*10+(columna-cont1);
            else
                posIni=100;
        }
        return cont+cont1-1;  
    }
    private int comprobarNW_SE(String movP){
        int cont=1;
        //móvome cara ao NW
        int intMovP=Integer.parseInt(movP);
        int fila=intMovP/10;
        int columna=intMovP%10;
        int posIni=(fila-cont)*10+(columna-cont);
        while ((posIni>=0)&&(this.getComponent(intMovP).getBackground().equals(getComponent(posIni).getBackground()))){
            cont++;
            if ((fila-cont>=0) && (columna-cont>=0))
                posIni=(fila-cont)*10+(columna-cont);
            else
                posIni=-1;
        }
        //móvome cara ao SE
        int cont1=1;
        posIni=(fila+cont1)*10+(columna+cont1);
        while ((posIni<100)&&(this.getComponent(intMovP).getBackground().equals(getComponent(posIni).getBackground()))){
            cont1++;
            if ((fila+cont1<=9) && (columna+cont1<=9))
                posIni=(fila+cont1)*10+(columna+cont1);
            else
                posIni=100;
        }
        return cont+cont1-1; 
    }
    private boolean comprobarGanhador(String movP,String axuda){
        boolean win=false;
        if ((comprobarNS(movP)>=5) ||(comprobarEW(movP)>=5)||(comprobarNE_SW(movP)>=5)||(comprobarNW_SE(movP)>=5)){
            win=true;
        }
        System.out.println("Comprobar gañador:"+win);
        if (win){
            if (axuda.equals("O"))
                datosXogo.setCorGanhador(datosXogo.getCorOutro());
            else
                datosXogo.setCorGanhador(datosXogo.getCorCliente());
            datosXogo.setGanhador(true);
        }
        return win;
    }
}
class BoxListener extends MouseAdapter 
{
    private DatosXogo datosXogo;
     public void setDatosXogo(DatosXogo datosXogo){
        this.datosXogo=datosXogo;
    }
    public DatosXogo getDatosXogo(){
        return datosXogo;
    }
   
    @Override
    public void mouseClicked(MouseEvent me)
    {
        JPanel clickedBox =(JPanel)me.getSource(); // get the reference to the box that was clicked
        if (this.datosXogo.getTurno()){
            System.out.println("Pinchaches na cuadricula:"+clickedBox.getName());
            clickedBox.setBackground(this.datosXogo.getCorCliente());
            clickedBox.setEnabled(false);
            clickedBox.removeMouseListener(this);
            this.datosXogo.setMovPropio(clickedBox.getName());
            this.datosXogo.setTurno(false);
        }
    }   
}