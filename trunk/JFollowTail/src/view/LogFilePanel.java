package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import model.LogFile;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.PatternPredicate;
import org.jdesktop.swingx.search.SearchFactory;

import view.highlightings.Highlighting;

public class LogFilePanel extends JPanel implements PropertyChangeListener {

	public static final String SCROLL_CHANGED_BY_USER = "Scroll changed by user";
	private static final long serialVersionUID = 1L;
	private JXTable table;
	private JScrollPane viewLogScrollPane;
	private LogFile logFile;
	private DefaultTableModel tableModel;
	private JFollowTailFrame parentFrame;
	
	public LogFilePanel(JFollowTailFrame parentFrame){
		this.parentFrame = parentFrame;
		createUI();
	}
	
	private void createUI() {
		setLayout(new BorderLayout());
		initTable();
		viewLogScrollPane = new JScrollPane(table);
		viewLogScrollPane.getViewport().setBackground(Color.WHITE);
		viewLogScrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				if(logFile==null){
					return;
				}
				boolean oldValue = logFile.isFollowTail();
				//if reaches the maximum of scroll -> set follow Tail
				if(e.getValue() == e.getAdjustable().getMaximum()-viewLogScrollPane.getVerticalScrollBar().getVisibleAmount()){
					logFile.setFollowTail(true);
				}else{
					logFile.setFollowTail(false);
				}
				firePropertyChange(SCROLL_CHANGED_BY_USER, oldValue, logFile.isFollowTail());
			}
		});
		
		add(viewLogScrollPane, BorderLayout.CENTER);
	}

	private void initTable() {
		table = new JXTable();
	    table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
	    //hide columns
	  	table.setTableHeader(null);
	  	//hide grid
	  	table.setShowGrid(false);
	  	table.setIntercellSpacing(new Dimension(0, 0));
	    tableModel = new DefaultTableModel(null, new String[]{"text"});
	    table.setModel(tableModel);
	    table.setHorizontalScrollEnabled(true);
	}

	public void processHighlightings(){
		List<Highlighting> highlightings = parentFrame.getHighlightings();
		//removes all the previous highlighters
		for(Highlighter h: table.getHighlighters()){
			table.removeHighlighter(h);
		}
		
		PatternPredicate patternPredicate = null;
		ColorHighlighter highlighter = null;
		List<Highlighter> highlighters = new LinkedList<Highlighter>();
		for(Highlighting highlighting : highlightings){
			//TODO set case sensitive and insensitive (now it's only case insensitive)
		    patternPredicate = new PatternPredicate(Pattern.compile(highlighting.getToken(),Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE));
		    highlighter = new ColorHighlighter(patternPredicate, highlighting.getBackgroundColor(), highlighting.getForegroundColor());
		    highlighters.add(highlighter);
		}
		//Reverse order to work properly
		Collections.reverse(highlighters);
		table.setHighlighters(highlighters.toArray(new Highlighter[0]));
	}

	public String getPath() {
		if(logFile!=null){
			return logFile.getPath();
		}
		return "No log file";
	}

	public String getFileName() {
		if(logFile!=null){
			return logFile.getFileName();
		}
		return "No log file";
	}

	public synchronized void setLogFile(LogFile logFile) {
		this.logFile = logFile;
		logFile.addPropertyChangeListener(this);
		loadInitialData(logFile);
		processHighlightings();
	}

	private void loadInitialData(LogFile logFile) {
		removeAllRows();
		String[] auxStr = new String[1];
		Iterator<String> it = logFile.getLines().iterator();
		for(;it.hasNext() ;){
			auxStr[0] = it.next();
			tableModel.addRow(auxStr);
		}
		table.packAll();
	}
	
	private void removeAllRows(){
		if (tableModel.getRowCount() > 0) {
		    for (int i = tableModel.getRowCount() - 1; i > -1; i--) {
		    	tableModel.removeRow(i);
		    }
		}
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(LogFile.LOG_FILE_CHANGED.equals(evt.getPropertyName())){
			SwingUtilities.invokeLater(new Runnable(){

				@Override
				public void run() {
					//TODO try to add the new lines only
					removeAllRows();
					String[] auxStr = new String[1];
					Iterator<String> it = logFile.getLines().iterator();
					for(;it.hasNext() ;){
						auxStr[0] = it.next();
						tableModel.addRow(auxStr);
					}
					table.packAll();
					//follow Tail
					if(logFile.isFollowTail()){
						table.scrollRowToVisible(tableModel.getRowCount()-1);
					}
					
				}
			});
		}
	}

	public void setFollowTail(boolean followTail) {
		if(logFile == null){
			return;
		}
		logFile.setFollowTail(followTail);
		//follow Tail
		if(followTail){
			table.scrollRowToVisible(tableModel.getRowCount()-1);
		}
	}
	
	public boolean isFollowingTail(){
		return logFile.isFollowTail();
	}
	
	public void showFindDialog(){
		SearchFactory.getInstance().showFindInput(LogFilePanel.this, table.getSearchable());
	}
	
	public void closeLogFile(){
		logFile.stopCurrentFileListener();
	}
}
