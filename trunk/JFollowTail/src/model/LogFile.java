package model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class LogFile implements PropertyChangeListener {
	public static final String LOG_FILE_CHANGED = "Log File Changed";
	private File file;
	private boolean followTail = true;
	private StringBuilder fileContent;
	private FileListener fileListener;
	private PropertyChangeSupport propertyChangeSupport;
	
	public LogFile(File file) throws IOException{
		propertyChangeSupport = new PropertyChangeSupport(this);
		this.setFile(file);
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) throws IOException{
		this.file = file;
		loadFile();
		createFileListener();
	}

	private void loadFile() throws IOException {
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
		byte fileContentAsBytes[] = new byte[(int)file.length()];
		bis.read(fileContentAsBytes);
		bis.close();
		
		fileContent = new StringBuilder(new String(fileContentAsBytes));
	}

	private void createFileListener() throws IOException {
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

	public StringBuilder getFileContent() {
		return fileContent;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		System.out.println("Name = " + evt.getPropertyName());
		if(FileListener.FILE_WAS_MODIFIED.equals(evt.getPropertyName())){
			try {
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

}
