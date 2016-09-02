package paifillonaiefamiliaservidor;
import java.util.Observable;
/**
 *
 * @author noemi
 */
public class Porto extends Observable{
    private int porto;
    public  void setPorto(int porto){
        this.porto=porto;
        changeData(this);
    }
    public int getPorto(){
        return porto;
    }
    Porto() {	
        super();
    }
    void changeData(Object data) {
        setChanged(); // the two methods of Observable class
        notifyObservers(data);
    }
}