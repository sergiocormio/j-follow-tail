package model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LogFile implements PropertyChangeListener {
	private static final String TAB_IN_SPACES = "        ";
	public static final String LOG_FILE_CHANGED = "Log File Changed";
	private File file;
	private boolean followTail = false;
	private List<String> lines;
	private FileListener fileListener;
	private PropertyChangeSupport propertyChangeSupport;
	
	public LogFile(File file) throws IOException{
		propertyChangeSupport = new PropertyChangeSupport(this);
		lines = new ArrayList<String>();
		setFile(file);
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) throws IOException{
		this.file = file;
		loadFile();
		createFileListener();
	}

	public void stopCurrentFileListener() {
		if(fileListener != null){
			fileListener.stop();
		}
	}

	private synchronized void loadFile() throws IOException {
		try{
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			byte fileContentAsBytes[] = new byte[(int)file.length()];
			bis.read(fileContentAsBytes);
			bis.close();
			//load lines
			lines.clear();
			//TODO Charset should be configurable
			String[] newLines= new String(fileContentAsBytes,"ISO-8859-1").split("\n");
			//add the rest of the new lines
			for(int i=0 ; i<newLines.length ; i++){
				lines.add(processLine(newLines[i]));
			}
			//add the last enter if exist
			addLastEnter(fileContentAsBytes);
		}catch(FileNotFoundException e){
			//file was deleted or renamed
			//clear lines
			lines.clear();
		}
	}
	
	private String processLine(String line){
		//replaces tabs by spaces
		return line.replaceAll("\t", TAB_IN_SPACES);
	}

	private void addLastEnter(byte[] fileContentAsBytes) {
		if(fileContentAsBytes.length > 0){
			byte[] lastByte = new byte[]{fileContentAsBytes[fileContentAsBytes.length-1]};
			String lastChar = new String(lastByte);
			//last char is enter...
			if("\n".equals(lastChar)){
				lines.add("");
			}
		}
	}
	
	private synchronized void loadUpdatesFromFile(int oldLength, int newLength) throws IOException {
		int bytesToRead = newLength - oldLength;
		//special case, the file is smaller than before
		if(newLength < oldLength){
			loadFile();
			return;
		}
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
		byte fileContentAsBytes[] = new byte[bytesToRead];
		//skips old length
		bis.skip(oldLength);
		bis.read(fileContentAsBytes);
		bis.close();
		//get lastLine
		String lastLine = "";
		if(lines.size()>0){
			lastLine = lines.get(lines.size()-1);
		}
		//add new lines
		String[] newLines = new String(fileContentAsBytes,"ISO-8859-1").split("\n");
		
		if(newLines.length==0){
			return;
		}
		
		//updates lastLine
		lastLine = lastLine.concat(newLines[0]);
		lastLine = processLine(lastLine);
		if(lines.size()>0){
			lines.set(lines.size()-1, lastLine);
		}else{
			lines.add(lastLine);
		}
		
		//add the rest of the new lines
		for(int i=1 ; i<newLines.length ; i++){
			lines.add(processLine(newLines[i]));
		}
		addLastEnter(fileContentAsBytes);
		
	}
	

	private void createFileListener() throws IOException {
		//Stop previous file listener if exists
		stopCurrentFileListener();
		fileListener = new FileListener(file);
		fileListener.addPropertyChangeListener(this);
	}

	public boolean isFollowTail() {
		return followTail;
	}

	public void setFollowTail(boolean followTail) {
		this.followTail = followTail;
	}

	public String getFileName() {
		if(file != null){
			return file.getName();
		}
		return "No log file";
	}

	public String getPath() {
		if(file != null){
			return file.getPath();
		}
		return "No log file";
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(FileListener.FILE_WAS_MODIFIED.equals(evt.getPropertyName())){
			try {
//				System.out.println("File was modified");
				//Read only the updates
				long lastLength = (Long)evt.getOldValue();
				long newLength = (Long)evt.getNewValue();
				loadUpdatesFromFile((int)lastLength,(int) newLength);
				propertyChangeSupport.firePropertyChange(LOG_FILE_CHANGED, null, this);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public synchronized List<String> getLines() {
		return lines;
	}

}
