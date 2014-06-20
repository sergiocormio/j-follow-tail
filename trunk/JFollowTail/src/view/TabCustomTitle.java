package view;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import model.LogFile;
import resources.ResourcesFactory;

public class TabCustomTitle extends JPanel implements PropertyChangeListener, ChangeListener{

	private static final long serialVersionUID = -2949872604814437362L;
	private String title;
	private JButton closeButton;
	private JLabel iconLabel;
	private JFollowTailFrame parentFrame;
	private JTabbedPane tabbedPane;
	private int lastSelectedIndex = -1; //last tab selected index

	public TabCustomTitle(String title, LogFile logFile, JTabbedPane tabbedPane, JFollowTailFrame parentFrame){
		this.title = title;
		this.parentFrame = parentFrame;
		this.tabbedPane = tabbedPane;
		tabbedPane.addChangeListener(this);
		logFile.addPropertyChangeListener(this);
		createUI();
	}

	private void createUI() {
		setLayout(new FlowLayout(FlowLayout.CENTER,4,0));
		//align correctly the button with a general border
		setBorder(new EmptyBorder(2, 0, 0, 0));
		setOpaque(false);
		iconLabel = new JLabel(ResourcesFactory.getInactiveLogIcon());
		add(iconLabel);
		add(new JLabel(title));
		closeButton = new TabCloseButton();
		closeButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int i = tabbedPane.indexOfTabComponent(TabCustomTitle.this);
				if(i > -1){
					parentFrame.closeTab(i);
				}
			}
		});
		closeButton.setOpaque(false);
		add(closeButton);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(LogFile.LOG_FILE_CHANGED.equals(evt.getPropertyName())){
			//Puts the active icon
			iconLabel.setIcon(ResourcesFactory.getActiveLogIcon());
		}
	}

	/**
	 * When tabbed changed...
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		int myIndex = tabbedPane.indexOfTabComponent(TabCustomTitle.this);
		int selectedIndex = tabbedPane.getSelectedIndex();
		//return back to inactive icon
		if(lastSelectedIndex == myIndex || selectedIndex == myIndex){
			iconLabel.setIcon(ResourcesFactory.getInactiveLogIcon());
		}
		lastSelectedIndex = selectedIndex;
	}
}
