package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;

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
import javax.swing.SwingWorker;
import javax.swing.TransferHandler;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import model.LogFile;
import resources.ResourcesFactory;
import view.highlightings.Highlighting;
import view.highlightings.HighlightingPersistor;
import view.highlightings.HighlightingsDialog;

public class JFollowTailFrame extends JFrame implements PropertyChangeListener{

	private static final String INVALID_FILE_PATH = "<invalid>";
	private static final String APP_TITLE = "JFollowTail";
	/**
	 * 
	 */
	private static final long serialVersionUID = -5891671256906504322L;
	protected static final String LAST_PATH_USED = "LAST_PATH_USED";
	protected static final String QUANTITY_OF_LAST_USED_FILES = "QUANTITY_OF_LAST_USED_FILES";
	protected static final String LAST_USED_FILE_PREXIX = "LAST_USED_FILE_";
	private JFileChooser fileChooser;
	private JTextField pathTextField;
	private JCheckBox followTailCheckBox;
	private LinkedList<Highlighting> highlightings;
	protected LinkedList<LogFilePanel> logFilePanels;
	private JTabbedPane tabbedPane;
	private JButton findButton;
	private Preferences preferences;
	private BusyDialog busyDialog;
	private JButton openButton;
	
	public JFollowTailFrame() throws IOException{
		//User preferences
		preferences = Preferences.userRoot().node(this.getClass().getName());
		loadHighlightings();
		createUI();
		pack();
		this.setLocationRelativeTo(null);
		loadLastOpenedFiles();
	}

	private void createUI() {
		this.setTitle(APP_TITLE);
		setIconImage(ResourcesFactory.getAppIcon().getImage());
		setLayout(new BorderLayout(0,0));
		setPreferredSize(new Dimension(800, 600));
		this.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				exitApplication();
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
			}
		});
		
		
		createMenu();
		createTopPanel();
		createTabbedPanel();
		//prepare to open files via drag and drop
		setDragAndDropFeature();
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
		String lastPathUsed = preferences.get(LAST_PATH_USED,null);
		if(lastPathUsed!=null){
			fileChooser.setCurrentDirectory(new File(lastPathUsed));
		}
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		topPanel.setBorder(new EmptyBorder(2, 0, 2, 2));
		
		JPanel topLeftPanel = new JPanel();
		topLeftPanel.setLayout(new FlowLayout(FlowLayout.CENTER,5,0));
		
		//Open Button
		openButton = new JButton("Open Log File",ResourcesFactory.getOpenIcon());
		
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
		if(tabIndex > -1){
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
			openFiles(fileChooser.getSelectedFiles());
		}
	}

	public synchronized void openFiles(final File[] files) {
		openButton.setEnabled(false);
		SwingWorker<Void, String> worker = new SwingWorker<Void, String>(){

			@Override
			protected Void doInBackground() throws Exception {
				try{
					synchronized (JFollowTailFrame.this) {
						for (File file : files) {
							publish(file.getName());
							if(!isAlreadyOpen(file)){
								openLogFile(file);
							}else{
								//selects the already open file
								int index = getIndexFromFilePath(file.getPath());
								tabbedPane.setSelectedIndex(index);
							}
							//saves last file used in user preferences
							preferences.put(LAST_PATH_USED, file.getAbsolutePath());
						}
					}
				}catch(Throwable t){
					t.printStackTrace();
					SwingUtilities.invokeLater(new Runnable() {
						
						@Override
						public void run() {
							done();
						}
					});
				}
				return null;
			}
			
			@Override
			protected void done() {
				busyDialog.setVisible(false);
				openButton.setEnabled(true);
			}
			
			@Override
			protected void process(List<String> chunks) {
				busyDialog.setText("Loading: " + chunks.get(chunks.size()-1));
				busyDialog.setLocationRelativeTo(JFollowTailFrame.this);
			}
			
		};
		//hides previous busyDialog
		if(busyDialog!=null){
			busyDialog.setVisible(false);
		}
		busyDialog = BusyDialog.showBusyDialog(JFollowTailFrame.this, "Loading...", worker);
		
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
			String errorMessage = "Error loading log file: " + (file!=null?file.getName():"");
			JOptionPane.showMessageDialog(JFollowTailFrame.this, errorMessage, "Error in log File", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			throw new RuntimeException(errorMessage);
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
				exitApplication();
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
	
	private void setDragAndDropFeature() {
		this.setTransferHandler(new TransferHandler(){

			/**
			 * 
			 */
			private static final long serialVersionUID = -6410994042858912735L;
			@Override
	        public boolean canImport(TransferHandler.TransferSupport info) {
	            return true;
	        }

	        @Override
	        public boolean importData(TransferHandler.TransferSupport info) {
	            if (!info.isDrop()) {
	                return false;
	            }

	            // Get the fileList that is being dropped.
	            Transferable t = info.getTransferable();
	            List<File> data;
	            try {
	            	data = (List<File>)t.getTransferData(DataFlavor.javaFileListFlavor);
	            } 
	            catch (Exception e) { 
	            	return false; 
	            }
	           
	            //Open Files
	            openFiles(data.toArray(new File[0]));
	            
	            return true;
	        }

		});
	}
	
	//Tries to load the last opened Files
	private void loadLastOpenedFiles() {
		try{
			int filesUsedQuantity = preferences.getInt(QUANTITY_OF_LAST_USED_FILES,0);
			String filePath = null;
			List<File> filesList = new ArrayList<File>();
			for(int i = 0 ; i < filesUsedQuantity ; i++){
				filePath = preferences.get(LAST_USED_FILE_PREXIX+i, INVALID_FILE_PATH);
				if(filePath != INVALID_FILE_PATH){
					filesList.add(new File(filePath));
				}
			}
			if(filesList.size() > 0){
				openFiles(filesList.toArray(new File[filesList.size()]));
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//Tries to load the last opened Files
	private void saveLastOpenedFiles() {
		try{
			int filesUsedQuantity = tabbedPane.getTabCount();
			preferences.putInt(QUANTITY_OF_LAST_USED_FILES, filesUsedQuantity);
			String filePath = null;
			int i = 0;
			for(LogFilePanel logFilePanel : logFilePanels){
				filePath = logFilePanel.getPath();
				preferences.put(LAST_USED_FILE_PREXIX+i, filePath);
				i++;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	protected void exitApplication(){
		saveLastOpenedFiles();
		System.exit(0);
	}
	
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				try {
					JFollowTailFrame frame = new JFollowTailFrame();
					frame.setVisible(true);
					
					//Open files passed as parameters
					if(args.length > 0){
						File[] files = new File[args.length];
						for(int i=0; i<args.length ; i++){
							files[0] = new File(args[i]);
						}
						frame.openFiles(files);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
