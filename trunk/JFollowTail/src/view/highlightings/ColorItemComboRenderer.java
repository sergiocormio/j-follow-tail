package view.highlightings;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

public class ColorItemComboRenderer extends JPanel implements ListCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -15391756102100290L;
	private JLabel colorNameLabel;
	private JLabel colorLabel;
	
	public ColorItemComboRenderer(){
		setLayout(new BorderLayout(5,5));
		setBorder(new EmptyBorder(1,5,1,0));
		colorNameLabel = new JLabel();
		colorLabel = new JLabel();
		colorLabel.setPreferredSize(new Dimension(30,10));
		colorLabel.setOpaque(true);
		add(colorLabel, BorderLayout.WEST);
		add(colorNameLabel, BorderLayout.CENTER);
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		if(!(value instanceof ColorItem)){
			return this;
		}
		ColorItem colorItem = (ColorItem) value;
		colorLabel.setBackground(colorItem.getColor());
		colorNameLabel.setText(colorItem.getName());
		
		if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
		
		return this;
	}

}


/*
* This method finds the image and text corresponding
* to the selected value and returns the label, set up
* to display the text and image.
*/
//public Component getListCellRendererComponent(
//                JList list,
//                Object value,
//                int index,
//                boolean isSelected,
//                boolean cellHasFocus) {
////Get the selected index. (The index param isn't
////always valid, so just use the value.)
//int selectedIndex = ((Integer)value).intValue();
//
//if (isSelected) {
//setBackground(list.getSelectionBackground());
//setForeground(list.getSelectionForeground());
//} else {
//setBackground(list.getBackground());
//setForeground(list.getForeground());
//}
//
////Set the icon and text.  If icon was null, say so.
//ImageIcon icon = images[selectedIndex];
//String pet = petStrings[selectedIndex];
//setIcon(icon);
//if (icon != null) {
//setText(pet);
//setFont(list.getFont());
//} else {
//setUhOhText(pet + " (no image available)",
// list.getFont());
//}
//
//return this;
//}
//. . .
//}