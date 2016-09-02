package paifillonaiefamiliaservidor;
import java.awt.Color;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author noemi
 */
public class PaiFilloNaieFamiliaServidor extends Thread implements Observer{
    private final Color cores[]={Color.RED, Color.BLUE, Color.GREEN,Color.YELLOW};
    private static List<PaiFilloNaieFamiliaServidor> listaFios=null;
    private static ServerSocket serverSocket=null;
    private static DatosConexionForm form;
    private static Porto porto;
    private static int contClientes=0;
    private boolean turno=true;
    private int numCliente;
    private String nickXogador;
    private Color corXogador;
    private Socket socket;
    private PrintWriter printWriter;
    private Scanner scanSocket;
    private static final String MENSAXE1="BYE";
    private static final String MENSAXE2="HELLO";
    private static final String MENSAXE3="COR";
    private static final String MENSAXE4="CORB";
    private static final String MENSAXE5="NICK";
    private static final String MENSAXE6="TURNO";
    private static final String MENSAXE7="FICHA";
    private static final String MENSAXE8="READY";
    private static final String MENSAXE9="WIN";
    public boolean getTurno(){
        return turno;
    }
    public void setTurno(boolean turno){
        this.turno=turno;
    }
    public Color getCorXogador(){
        return corXogador;
    }
    public void setCorXogador(Color corXogador){
        this.corXogador=corXogador;
    }
    public void setTurno(Color corXogador){
        this.corXogador=corXogador;
    }
    
    public PrintWriter getPrintWriter(){
        return printWriter;
    }
    
    public PaiFilloNaieFamiliaServidor(String str){
        super(str);
    }
    
    public void escribirSocket(PrintWriter printWriter,String mensaxe){
        printWriter.write(mensaxe);
        printWriter.flush();
    }
    private void cerrarAplicacion(){
        if (printWriter!=null){
            escribirSocket(printWriter,MENSAXE1);
        }
        printWriter.close();
        System.out.println("Novo xogador conectado. Xa hai dous xogadores. O xogador será desconectado");
        contClientes--;
        listaFios.remove(this);
        try{
            socket.close();
            if (socket.isClosed()){
                System.out.println("Socket desconectado para este cliente");
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }
    public void enviarCoresTodosClientes(){
        System.out.println("Comeza a partida");
        try {
            //enviar cor dos outros
            for (int i=0;i<listaFios.size();i++){
                for (int j=0;j<listaFios.size();j++){
                    PaiFilloNaieFamiliaServidor fio=listaFios.get(i);
                    PaiFilloNaieFamiliaServidor fio2=listaFios.get(j);
                    //comprobar se os dous xogadores elixiron a mesma cor
                    if (i!=j){
                        if (fio.getCorXogador().equals(fio2.getCorXogador())){
                           int k=0;
                           if (cores[k].equals(fio.getCorXogador())){
                               fio2.setCorXogador(cores[((k+1)%4)]);
                           }
                        }
                        System.out.println("envía nova cor ao xogador 2");
                        fio2.escribirSocket(fio2.getPrintWriter(),MENSAXE3+" "+fio2.getCorXogador().getRGB()+"\r\n");
                        fio.escribirSocket(fio.getPrintWriter(),MENSAXE4+" "+fio2.getCorXogador().getRGB() +"\r\n");
                    } 
                }
            }
        }catch (Exception e){
            System.out.println(e);
        }    
    }
    public void enviarMovementoOutroCliente(String dato){
        try {
            //enviar cor dos outros
            for (int i=0;i<listaFios.size();i++){
                PaiFilloNaieFamiliaServidor fio=listaFios.get(i);
                if (this!=fio){
                    System.out.println("envia movemento ao outro cliente: "+dato);
                    fio.escribirSocket(fio.getPrintWriter(),dato);
                }
            }
       
        }catch (Exception e){
            System.out.println(e);
        }    
    }
    public void enviarPecheOutroCliente(String dato){
        System.out.println("Remate abrupto da partida");
        try {
            //enviar cor dos outros
            for (int i=0;i<listaFios.size();i++){
                PaiFilloNaieFamiliaServidor fio=listaFios.get(i);
                System.out.println("envia movemento ao outro cliente: "+dato);
                if (!fio.socket.isClosed()){
                    fio.escribirSocket(fio.getPrintWriter(),dato+" \r\n");
                    fio.printWriter.close();
                    fio.socket.close();
                }    
                //}
            }
            for (int i=listaFios.size()-1;i>=0;i--){
                listaFios.remove(i);
                contClientes--;
            }
       
        }catch (Exception e){
            System.out.println(e);
        }    
    }
    
    public void enviarTurnoTodosClientes(){
        System.out.println("Envía o turno aos clientes");
        try {
            //enviar turno
            for (int i=0;i<listaFios.size();i++){
                PaiFilloNaieFamiliaServidor fio=listaFios.get(i);
                if (i==0){
                    fio.setTurno(true);
                }
                fio.escribirSocket(fio.getPrintWriter(),MENSAXE6+" "+fio.getTurno() +"\r\n");
            }
        }catch (Exception e){
            System.out.println(e);
        }
    }
    public void cerrarSockets(){
        System.out.println("Rematou a partida");
        try {
            //enviar turno
            for (int i=0;i<listaFios.size();i++){
                PaiFilloNaieFamiliaServidor fio=listaFios.get(i);
                printWriter.close();
                fio.socket.close();
                contClientes--;
            }
            for (int i=listaFios.size()-1;i>=0;i--){
                listaFios.remove(i);
            }
        }catch (Exception e){
            System.out.println(e);
        }
    }
   
    @Override
    public void run(){
        System.out.println("running server");
        listaFios.add(this);
        try{
            if (scanSocket==null)
                    scanSocket=new Scanner(socket.getInputStream());
            printWriter=new PrintWriter(socket.getOutputStream());
            //o número máximo de clientes para xogar é de 2
            while (scanSocket.hasNextLine()){
                leerSocket();
            }
            scanSocket.close();
        }catch(IOException ioe){
            System.out.println(ioe);
        }
    }
    private void leerSocket(){
            String dato=scanSocket.nextLine();
            String comando=dato.substring(0,dato.indexOf(" "));
            switch(comando){
                case MENSAXE1:
                    enviarPecheOutroCliente(dato);
                    break;
                case MENSAXE3: String corStr=dato.substring(dato.indexOf(" ")+1);
                    corXogador=new Color(Integer.parseInt(corStr));
                    if (contClientes>2){
                        cerrarAplicacion();
                    }else {
                        escribirSocket(printWriter,MENSAXE2+" \r\n");
                        System.out.println("Novo xogador conectado "+contClientes);
                        numCliente=contClientes;
                        if (contClientes==2){
                            enviarCoresTodosClientes();
                        }
                    }
                    break;
                 case MENSAXE5:
                     nickXogador=dato.substring(dato.indexOf(" ")+1);
                     System.out.println("nick xogador:"+nickXogador);
                    if (contClientes==2){
                        enviarTurnoTodosClientes();
                    }
                    System.out.println("Verifica se ten o turno:"+getTurno());
                    if (getTurno()){
                        leerSocket();
                        escribirSocket(printWriter,MENSAXE6+" 1\r\n");
                        //cambiar turno e envialo aos fios
                    }
                     break;
                case MENSAXE7: 
                        enviarMovementoOutroCliente(dato+"\r\n");
                      break;
                case MENSAXE8: System.out.println("Chegou ready do cliente");
                      break;
                case MENSAXE9: System.out.println("Chegou o gañador");
                      enviarMovementoOutroCliente(dato+"\r\n");
                      cerrarSockets();
                    break;
            }
        //}
    }
    @Override
    public void update(Observable o, Object data){
        int portoConexion=((Porto)o).getPorto();
        form.dispose();
        try{
            serverSocket=new ServerSocket(portoConexion);
            System.out.println("Escoitando no porto "+portoConexion);
            listaFios=new ArrayList<PaiFilloNaieFamiliaServidor>();
            this.socket=serverSocket.accept();
            contClientes++;
            this.setTurno(true);//colle o turno o xogador que se conecta primeiro
            this.start();
            escoitarPeticions();
        } catch (BindException be){
            System.out.println(be);
            JOptionPane.showMessageDialog(null,"Xa hai un servidor escoitando nese porto. Esta aplicación pecharase.");
        }catch(Exception e){
            System.out.println(e);
        }
    }
    private void escoitarPeticions(){
        try{
            while (true){
                if (contClientes>=1){
                    PaiFilloNaieFamiliaServidor xogo=new PaiFilloNaieFamiliaServidor("Servidor xogo "+String.valueOf(contClientes));
                    xogo.socket=serverSocket.accept();
                    contClientes++;
                    xogo.setTurno(false);
                    xogo.start();
                }
            }
        }catch (Exception e){
            System.out.println("erro no estado do thread. ServerSocket"+serverSocket.toString()+"socket:"+socket.toString()+"estado Thread:"+Thread.currentThread().getState());
            System.out.println(e);
        }finally{
            
        }
    }
    /**
     * @param args the command line arguments
     */
    private void iniciarDatosConexionForm(){
        form=new DatosConexionForm();
        form.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        form.setPorto(porto);
        form.setTitle("Datos da conexión - Servidor");
        form.setResizable(false);
        ImageIcon iconaForm=new ImageIcon("src/imaxes/iconaMin.png");
        form.setIconImage(iconaForm.getImage());
        form.setVisible(true);
    }

    public static void main(String[] args)  {
        PaiFilloNaieFamiliaServidor serv=new PaiFilloNaieFamiliaServidor("Servidor xogo");
        porto=new Porto();
        porto.addObserver(serv);
        serv.iniciarDatosConexionForm();
    }
}
