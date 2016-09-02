package paifillonaiefamiliacliente;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author noemi
 */
class CorRenderer extends JLabel implements ListCellRenderer<Object> { 

    public CorRenderer() {  

    }
    
    @Override
     public Component getListCellRendererComponent(  
         JList list,  
         Object value,  
         int index,  
         boolean isSelected,  
         boolean cellHasFocus)  
     {   
        this.setOpaque(true);
         if (value instanceof Color){
            Color cor=(Color) value;
            list.setSelectionBackground(cor);
            setText(" ");
            setBackground(cor);
        }
            
        return this;  
     }  
}
