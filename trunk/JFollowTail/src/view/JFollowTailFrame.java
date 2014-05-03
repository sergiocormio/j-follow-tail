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
import java.util.List;

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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import model.LogFile;
import resources.ResourcesFactory;
import view.highlightings.Highlighting;
import view.highlightings.HighlightingPersistor;
import view.highlightings.HighlightingsDialog;

public class JFollowTailFrame extends JFrame implements PropertyChangeListener{

	private static final String APP_TITLE = "JFollowTail";
	/**
	 * 
	 */
	private static final long serialVersionUID = -5891671256906504322L;
	private JFileChooser fileChooser;
	private JTextField pathTextField;
	private JCheckBox followTailCheckBox;
	private LinkedList<Highlighting> highlightings;
	protected LinkedList<LogFilePanel> logFilePanels;
	private JTabbedPane tabbedPane;
	private JButton findButton;
	
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
		createTabbedPanel();
	}
	
	private void createTabbedPanel() {
		tabbedPane = new JTabbedPane();
		tabbedPane.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				changeTabSelected();
			}
		});
		
		logFilePanels = new LinkedList<LogFilePanel>();
		this.add(tabbedPane,BorderLayout.CENTER);
	}

	private void createTopPanel() {
		fileChooser = new JFileChooser();
		fileChooser.setFileHidingEnabled(true);
		fileChooser.setMultiSelectionEnabled(true);
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
							if(getCurrentLogFilePanel()!=null){
								getCurrentLogFilePanel().processHighlightings();
							}
						}
					}
				});
				highlightingsDialog.setVisible(true);
				for(LogFilePanel logFilePanel : logFilePanels){
					logFilePanel.processHighlightings();
				}
			}
		});
		
		topLeftPanel.add(adminHighlightingButton);
		//Find or search button
		findButton = new JButton("Find",ResourcesFactory.getFindIcon());
		findButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				LogFilePanel currentPanel = getCurrentLogFilePanel();
				if(currentPanel!=null){
					currentPanel.showFindDialog();
				}
			}
		});
		topLeftPanel.add(findButton);
		
		//Follow Tail
		followTailCheckBox = new JCheckBox("Follow Tail");
		followTailCheckBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(getCurrentLogFilePanel()!=null){
					getCurrentLogFilePanel().setFollowTail(followTailCheckBox.isSelected());
				}
			}
		});
		topLeftPanel.add(followTailCheckBox);
		
		topPanel.add(topLeftPanel,BorderLayout.WEST);
		//TextField
		pathTextField = new JTextField();
		pathTextField.setEditable(false);
		topPanel.add(pathTextField,BorderLayout.CENTER);
		this.add(topPanel,BorderLayout.NORTH);
		
	}
	
	protected LogFilePanel getCurrentLogFilePanel() {
		//It could have more than one logFilePanels
		int tabIndex = tabbedPane.getSelectedIndex();
		if(tabIndex>-1){
			return logFilePanels.get(tabIndex);
		}else{
			return null;
		}
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
			for (File file : fileChooser.getSelectedFiles()) {
				if(!isAlreadyOpen(file)){
					openLogFile(file);
				}else{
					//selects the already open file
					int index = getIndexFromFilePath(file.getPath());
					tabbedPane.setSelectedIndex(index);
				}
			}
		}
	}
	
	private boolean isAlreadyOpen(File file) {
		for(LogFilePanel logFilePanel : logFilePanels){
			if(file.getPath().equalsIgnoreCase(logFilePanel.getPath())){
				return true;
			}
		}
		return false;
	}
	
	private int getIndexFromFilePath(String path) {
		LogFilePanel logFilePanel = null;
		for(int i = 0; i<logFilePanels.size(); i++){
			logFilePanel = logFilePanels.get(i);
			if(path.equalsIgnoreCase(logFilePanel.getPath())){
				return i;
			}
		}
		return -1;
	}

	private void openLogFile(File file){
		try{
			LogFilePanel logFilePanel = new LogFilePanel(this);
			logFilePanel.addPropertyChangeListener(this);
			logFilePanels.add(logFilePanel);
			LogFile logFile = new LogFile(file);
			logFilePanel.setLogFile(logFile);
			tabbedPane.addTab(logFilePanel.getFileName(),ResourcesFactory.getActiveLogIcon(), logFilePanel, logFilePanel.getPath());
			tabbedPane.setTabComponentAt(logFilePanels.size()-1, new TabCustomTitle(logFilePanel.getFileName(),logFile, tabbedPane,JFollowTailFrame.this));
			//Selects recent file opened
			tabbedPane.setSelectedIndex(logFilePanels.size()-1);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(JFollowTailFrame.this, "Error loading log file: " + (file!=null?file.getName():""), "Error in log File", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	
	private void changeTabSelected(){
		LogFilePanel currentLogFilePanel = getCurrentLogFilePanel();
		if(currentLogFilePanel==null){
			this.setTitle(APP_TITLE);
			pathTextField.setText("");
			return;
		}
		this.setTitle(currentLogFilePanel.getFileName() + " - " + APP_TITLE);
		pathTextField.setText(currentLogFilePanel.getPath());
		followTailCheckBox.setSelected(currentLogFilePanel.isFollowingTail());
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

	public List<Highlighting> getHighlightings() {
		return highlightings;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(LogFilePanel.SCROLL_CHANGED_BY_USER.equals(evt.getPropertyName())){
			LogFilePanel logFilePanelChanged = (LogFilePanel)evt.getSource();
			if(logFilePanelChanged == getCurrentLogFilePanel()){
				followTailCheckBox.setSelected(logFilePanelChanged.isFollowingTail());
			}
		}
	}

	public void closeTab(int i) {
		logFilePanels.get(i).closeLogFile();
		logFilePanels.remove(i);
		tabbedPane.remove(i);
	}
}
