package view;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import model.LogFile;
import view.highlightings.Highlighting;

public class LogFilePanel extends JPanel implements PropertyChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextArea logText;
	private JScrollPane viewLogScrollPane;
	private LogFile logFile;
	
	public LogFilePanel(){
		createUI();
	}
	
	private void createUI() {
		setLayout(new BorderLayout());
		logText = new JTextArea();
		logText.setEditable(false);
		viewLogScrollPane = new JScrollPane(logText);
		add(viewLogScrollPane, BorderLayout.CENTER);
	}

	public void processHighlightings(List<Highlighting> highlightings){
		Highlighter highlighter = logText.getHighlighter();
		highlighter.removeAllHighlights();
		//if completeText is empty
		if(logText.getText() == null || logText.getText().trim().length() == 0){
			return;
		}
		//Complete text in upper case
		String text = logText.getText().toUpperCase();
		for(Highlighting highlighting : highlightings){
			int lastIndex = -1;
			try {
				if(highlighting.getToken() != null && highlighting.getToken().trim().length()>0){
					do{
						lastIndex = text.indexOf(highlighting.getToken().trim().toUpperCase(), lastIndex+1);
						if(lastIndex>=0){
							highlighter.addHighlight(getPreviousEnterIndex(text,lastIndex), getNextEnterIndex(text, lastIndex), new DefaultHighlighter.DefaultHighlightPainter(highlighting.getBackgroundColor()));
						}
					}while(lastIndex>=0);
				}
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}
	
	private int getNextEnterIndex(String text, int index) {
		int nextEnterIndex = text.indexOf("\n", index);
		if(nextEnterIndex==-1){
			nextEnterIndex = text.length();
		}
		return nextEnterIndex;
	}

	/**
	 * Returns the index of "\n" previous to index parameter in text
	 * @param text
	 * @param index
	 * @return 
	 */
	private int getPreviousEnterIndex(String text, int index) {
		int previousEnterIndex = -1;
		int currentIndex = -1;
		while(currentIndex < index){
			previousEnterIndex = currentIndex;
			currentIndex = text.indexOf("\n",currentIndex+1);
			if(currentIndex==-1){ //There is no enter previous
				break;
			}
		}
		if(previousEnterIndex==-1){
			return 0;
		}
		return previousEnterIndex;
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

	public void setLogFile(LogFile logFile) {
		this.logFile = logFile;
		logFile.addPropertyChangeListener(this);
		logText.setText(logFile.getFileContent().toString());
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(LogFile.LOG_FILE_CHANGED.equals(evt.getPropertyName())){
			SwingUtilities.invokeLater(new Runnable(){

				@Override
				public void run() {
					logText.setText(logFile.getFileContent().toString());
					logText.updateUI();
//					processHighlightings(highlightings);
				}
				
			});
		}
	}

}
