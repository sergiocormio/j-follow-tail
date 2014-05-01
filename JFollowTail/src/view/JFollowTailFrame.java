package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import model.LogFile;
import resources.ResourcesFactory;
import view.highlightings.Highlighting;
import view.highlightings.HighlightingPersistor;
import view.highlightings.HighlightingsDialog;

public class JFollowTailFrame extends JFrame {

	private static final String APP_TITLE = "JFollowTail";
	/**
	 * 
	 */
	private static final long serialVersionUID = -5891671256906504322L;
	private JFileChooser fileChooser;
	private JTextField pathTextField;
	private JCheckBox followTailCheckBox;
	private LinkedList<Highlighting> highlightings;
	protected LogFilePanel logFilePanel;
	private JTabbedPane tabbedPane;
	
	public JFollowTailFrame() throws IOException{
		loadHighlightings();
		createUI();
		pack();
		this.setLocationRelativeTo(null);
	}

	private void createUI() {
		this.setTitle(APP_TITLE);
		setIconImage(ResourcesFactory.getAppIcon().getImage());
		setLayout(new BorderLayout(0,0));
		setPreferredSize(new Dimension(800, 600));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		createMenu();
		createTopPanel();
		createLogFilePanel();
	}
	
	private void createLogFilePanel() {
		tabbedPane = new JTabbedPane();
		logFilePanel = new LogFilePanel();
		tabbedPane.addTab(logFilePanel.getFileName(),ResourcesFactory.getLogIcon(), logFilePanel, logFilePanel.getPath());
		this.add(tabbedPane,BorderLayout.CENTER);
	}

	private void createTopPanel() {
		fileChooser = new JFileChooser();
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		topPanel.setBorder(new EmptyBorder(2, 0, 2, 2));
		
		JPanel topLeftPanel = new JPanel();
		topLeftPanel.setLayout(new FlowLayout(FlowLayout.CENTER,5,0));
		
		//Open Button
		final JButton openButton = new JButton("Open Log File",ResourcesFactory.getOpenIcon());
		
		openButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				chooseLogFile();
			}
		});
		
		topLeftPanel.add(openButton);
		//Highlighting Button
		final JButton adminHighlightingButton = new JButton("Highlighting",ResourcesFactory.getHighlightingIcon());
		
		adminHighlightingButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				HighlightingsDialog highlightingsDialog = new HighlightingsDialog(JFollowTailFrame.this,highlightings);
				//Listening highlightings changes
				highlightingsDialog.addPropertyChangeListener(HighlightingsDialog.LIST_CHANGED_EVENT, new PropertyChangeListener() {
					
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if(evt.getPropertyName().equals(HighlightingsDialog.LIST_CHANGED_EVENT)){
							JFollowTailFrame.this.logFilePanel.processHighlightings(JFollowTailFrame.this.highlightings);
						}
					}
				});
				highlightingsDialog.setVisible(true);
				JFollowTailFrame.this.logFilePanel.processHighlightings(JFollowTailFrame.this.highlightings);
			}
		});
		
		topLeftPanel.add(adminHighlightingButton);
		
		followTailCheckBox = new JCheckBox("Follow Tail");
		topLeftPanel.add(followTailCheckBox);
		
		topPanel.add(topLeftPanel,BorderLayout.WEST);
		//TextField
		pathTextField = new JTextField();
		pathTextField.setEditable(false);
		topPanel.add(pathTextField,BorderLayout.CENTER);
		this.add(topPanel,BorderLayout.NORTH);
		
	}
	
	/**
	 * creates and load the highlightings
	 */
	private void loadHighlightings() {
		highlightings = new HighlightingPersistor().loadHighlightings();
	}

	
	private void chooseLogFile() {
		int retVal = fileChooser.showOpenDialog(JFollowTailFrame.this);
		if(retVal == JFileChooser.APPROVE_OPTION){
			openLogFile(fileChooser.getSelectedFile());
		}
	}
	
	private void openLogFile(File file){
		try{
			LogFile logFile = new LogFile(file);
			logFilePanel.setLogFile(logFile);
			this.setTitle(file.getName() + " - " + APP_TITLE);
			pathTextField.setText(file.getPath());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(JFollowTailFrame.this, "Error loading log file: " + (file!=null?file.getName():""), "Error in log File", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private void createMenu() {
		JMenuBar menuBar = new JMenuBar();
		//FILE
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		
		//Open
		JMenuItem openMenuItem = new JMenuItem("Open...");
		openMenuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				chooseLogFile();
			}

		});
		fileMenu.add(openMenuItem);
		
		//SEPARATOR
		fileMenu.add(new JSeparator());
		
		//Exit
		JMenuItem exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}

		});
		fileMenu.add(exitMenuItem);
		
		//HELP
		JMenu helpMenu = new JMenu("Help");
		menuBar.add(helpMenu);
		
		//About
		JMenuItem aboutMenuItem = new JMenuItem("About JFollowTail...");
		aboutMenuItem.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				new AboutDialog(JFollowTailFrame.this).setVisible(true);
			}
		});
		helpMenu.add(aboutMenuItem);
		this.setJMenuBar(menuBar);
		
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				try {
					new JFollowTailFrame().setVisible(true);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
