package paifillonaiefamiliacliente;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.*;
import java.io.*;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
/**
 *
 * @author noemi
 */
public class PaiFilloNaieFamiliaCliente extends Thread implements Observer{
    private static DatosConexionClienteForm form;
    private static int porto;
    private static String nomeServidor;
    private static Socket socket;
    private static String nick;
    private static Color cor;
    private static boolean turno;
    private static GUICliente pantalla;
    private static DatosXogo datosXogo;
    private static PrintWriter printWriter;
    private static Scanner scanSocket;
    private static ImageIcon iconaForm;
    private static final String MENSAXE1="BYE";
    private static final String MENSAXE2="HELLO";
    private static final String MENSAXE3="COR";
    private static final String MENSAXE4="CORB";
    private static final String MENSAXE5="NICK";
    private static final String MENSAXE6="TURNO";
    private static final String MENSAXE7="FICHA";
    private static final String MENSAXE8="READY";
    private static final String MENSAXE9="WIN";
    
    public PaiFilloNaieFamiliaCliente(String nome){
        super(nome);
    }
    
    @Override
    public void update(Observable o, Object data){
        if (o instanceof DatosConexionCliente ){
            DatosConexionCliente dc=(DatosConexionCliente)o;
            nick=dc.getNickCliente();
            nomeServidor=dc.getNomeServidor();
            porto=dc.getPorto();
            cor=dc.getCorCliente();
            form.dispose();
            try{
                Socket s=new Socket(nomeServidor,porto);
                socket=s;
                System.out.println("Conectado co servidor");
                //enviar a cor xa aquí ao servidor
                xogar();
            }catch(Exception e){
                JOptionPane.showMessageDialog(null,"Non se pode realizar a conexión co servidor");
                System.out.println(e);
            }
        
        }
    
        if (o instanceof DatosXogo){
            DatosXogo dx=(DatosXogo)o;
      
            if ((data instanceof String) && (dx.getTurno())){
                String dato=(String)data;
                String op=dato.substring(0,1);
                if (!op.equals("O")) {
                System.out.println("Envia movemento a servidor: "+dx.getMovPropio());
                if (dx.getGanhador()){
                    System.out.println("Este xogador gañou a partida");
                    escribirSocket(printWriter,MENSAXE9+" "+dx.getMovPropio()+"\r\n");
                    try{
                        printWriter.close();
                        socket.close();
                    }catch (Exception e){
                        System.out.println(e);
                    }
                }else{
                    System.out.println("Este xogador non gañou a partida");
                    escribirSocket(printWriter,MENSAXE7+" "+dx.getMovPropio()+"\r\n");
                }
                dx.setTurno(false);   
                }
            }
        }
    }
    private void iniciarGUICliente(){
        DatosXogo.numXogadores++;
        System.out.println("Benvida do servidor");
        datosXogo=new DatosXogo();
        datosXogo.addObserver(this);
        if (pantalla==null){
            pantalla=new GUICliente();
        }
        pantalla.setDatosXogo(datosXogo);
        pantalla.getDatosXogo().addObserver(pantalla);
        datosXogo.setCorCliente(cor);
        
        pantalla.setNickText("Nick: "+nick);
        pantalla.setServerName("Nome do servidor: "+nomeServidor);
        pantalla.setServerPort("Porto do servidor: "+String.valueOf(porto));
        pantalla.setCorCliente(cor);
        pantalla.setResizable(false);
        pantalla.ponCuadricula();
        pantalla.setIconImage(iconaForm.getImage());
        
        pantalla.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        pantalla.addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
                // Ask for confirmation before terminating the program.
                int option = JOptionPane.showConfirmDialog(
                        pantalla, 
                        "Seguro que queres pechar a aplicación?",
                        "Confirmación de peche", 
                        JOptionPane.YES_NO_OPTION, 
                        JOptionPane.QUESTION_MESSAGE);
                if (option == JOptionPane.YES_OPTION) {
                    try{
                        System.out.println("Enviando BYE ao servidor");
                        escribirSocket(printWriter,MENSAXE1+" \r\n");
                        printWriter.close();
                        socket.close();
                        System.exit(0);
                    }catch (Exception ex){
                        System.out.println(ex);
                    }
                }
        }
        });
        pantalla.setVisible(true);
    }
    private void leerSocket(){
        try{
            String dato=scanSocket.nextLine();
            String comando;
            if (dato.contains(" ")){
                comando=dato.substring(0,dato.indexOf(" "));
            }else
                comando=dato;
            switch (comando){
                case MENSAXE1:
                    socket.close();
                    System.out.println("Desconectado do servidor");
                    if (pantalla==null)
                        System.exit(0);
                    else{
                        pantalla.setJLblMsgServidor("O outro cliente desconectouse. Rematou a partida");
                    }
                    break;
                case MENSAXE2:
                    System.out.println("Envia ao servidor o nick do usuario:"+nick);
                    escribirSocket(printWriter, MENSAXE5+" "+nick+"\r\n");
                    iniciarGUICliente();
                    break;
                case MENSAXE3:
                    System.out.println("Nova cor para este cliente");
                    String corStr=dato.substring(dato.indexOf(" ")+1);
                    datosXogo.setCorCliente(new Color(Integer.parseInt(corStr)));
                    break;
                case MENSAXE4: 
                    corStr=dato.substring(dato.indexOf(" ")+1);
                    datosXogo.setCorOutro(new Color(Integer.parseInt(corStr)));
                    System.out.println("Cor xogador contrario :"+datosXogo.getCorOutro());
                    escribirSocket(printWriter,MENSAXE8+" \r\n");
                    break;
                case MENSAXE6:
                    turno=Boolean.parseBoolean(dato.substring(dato.indexOf(" ")+1));
                    DatosXogo.numXogadores++;
                    datosXogo.setTurno(turno);
                    break;
                case MENSAXE7:
                    String movOutro=dato.substring(dato.indexOf(" ")+1);
                    System.out.println("ficha:"+movOutro);
                    datosXogo.setMovOutro(movOutro);
                    break;
                case MENSAXE9:
                    movOutro=dato.substring(dato.indexOf(" ")+1);
                    System.out.println("win:"+movOutro);
                    datosXogo.setMovOutro(movOutro);
                    datosXogo.setGanhador(true);
                    datosXogo.setTurno(false);
                    break;
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }
    private void escribirSocket(PrintWriter printWriter,String mensaxe){
        printWriter.write(mensaxe);
        printWriter.flush();
    }
    public void xogar(){
        try{
            //aquí vou escribir no servidor
            scanSocket=new Scanner(socket.getInputStream());
            if (!socket.isClosed()){
                printWriter=new PrintWriter(socket.getOutputStream());
                escribirSocket(printWriter,MENSAXE3+" "+cor.getRGB()+"\r\n");
                System.out.println("Nick xogador: "+nick);
                //aqui xa podo abrir o formulario do xogo
                //aqui o start do thread
                PaiFilloNaieFamiliaCliente leitor=new PaiFilloNaieFamiliaCliente("leitor");
                leitor.start();
            }
        }catch (Exception e){
            System.out.println(e);
        }
    }
    public void run(){
        while (scanSocket.hasNextLine()){
            obterDatos();
        }
    }
    private void obterDatos(){
        try{
                leerSocket();
        } catch (Exception e){
            System.out.println(e);
        }
    }
    private void iniciarDatosConexionClienteForm(){
        form=new DatosConexionClienteForm();
        form.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        form.getDatosConexionCliente().addObserver(this);
        form.setTitle("Datos da conexión - Cliente");
        form.setResizable(false);
        iconaForm=new ImageIcon("src/imaxes/iconaMin.png");
        form.setIconImage(iconaForm.getImage());
        form.setVisible(true);
    }
    
    public static void main(String[] args) throws Exception {
            PaiFilloNaieFamiliaCliente xogoCliente=new PaiFilloNaieFamiliaCliente("main");
            xogoCliente.iniciarDatosConexionClienteForm();
    }
}
