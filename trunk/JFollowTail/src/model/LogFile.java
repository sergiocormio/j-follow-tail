package model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LogFile implements PropertyChangeListener {
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

	private void stopPreviousFileListener() {
		if(fileListener != null){
			fileListener.stop();
		}
	}

	private synchronized void loadFile() throws IOException {
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
		byte fileContentAsBytes[] = new byte[(int)file.length()];
		bis.read(fileContentAsBytes);
		bis.close();
		//load lines
		lines.clear();
		lines.addAll(Arrays.asList(new String(fileContentAsBytes).split("\n")));
	}

	private void createFileListener() throws IOException {
		//Stop previous file listener if exists
		stopPreviousFileListener();
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
				//TODO Try to avoid loading all the file again
				System.out.println("File was modified");
				loadFile();
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
