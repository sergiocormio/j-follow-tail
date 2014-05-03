package model;


import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;

//These imports works in JAVA 7
//import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
//import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
//import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.WatchEvent;
//import java.nio.file.WatchEvent.Kind;
//import java.nio.file.WatchKey;
//import java.nio.file.WatchService;

/**
 * Class in charge of listening file changes from file system
 * 
 * @author Sergio Cormio
 * 
 */
public class FileListener implements Runnable {
	public static final String FILE_WAS_MODIFIED = "File was modified";
	private boolean stop = false;
//	private WatchService service; //Only for JAVA7
	private File file;
	private PropertyChangeSupport propertyChangeSupport;
	private long lastLength = 0l;

	public FileListener(File file) throws IOException {
		this.file = file;
		lastLength = file.length();
		propertyChangeSupport = new PropertyChangeSupport(this);
//		setWatcherService();
		// Start the infinite polling loop
		new Thread(this).start();
	}

//Only for JAVA7
//	@SuppressWarnings("unused")
//	private void setWatcherService() throws IOException {
//		// Path must be a directory
//		Path path = Paths.get(file.getParentFile().toURI());
//		service = path.getFileSystem().newWatchService();
//		// We register the path to the service
//		// We watch for creation events
//		path.register(service, ENTRY_CREATE, ENTRY_MODIFY);
//	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	@Override
	public void run() {
		doRunManualMode();
//		doRunWithWatcherService();
	}
	
	private void doRunManualMode() {
		while (!stop) {
			//A file changes when his length changes
			if(file.length()!=lastLength){
				propertyChangeSupport.firePropertyChange(FILE_WAS_MODIFIED, lastLength, file.length());
				lastLength = file.length();
			}
			try {
				//TODO make this value variable
				Thread.sleep(250L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	//Only for JAVA7
//	@SuppressWarnings("unused")
//	private void doRunWithWatcherService() {
//		WatchKey key = null;
//
//		while (!stop) {
//			try {
//				key = service.take();
//
//				// Dequeueing events
//				Kind<?> kind = null;
//				for (WatchEvent<?> watchEvent : key.pollEvents()) {
//					// Get the type of the event
//					kind = watchEvent.kind();
//					if (OVERFLOW == kind) {
//						continue; // loop
//					} else if (ENTRY_CREATE == kind) {
//						// A new Path was created
//						// Path newPath = ((WatchEvent<Path>)
//						// watchEvent).context();
//						// Output
//						// System.out.println("New path created: " + newPath);
//					} else if (ENTRY_MODIFY == kind) {
//						// A Modification happened
//						Path modifiedPath = ((WatchEvent<Path>) watchEvent)
//								.context();
//						if (modifiedPath.endsWith(file.getName())) {
//							// Output
//							propertyChangeSupport.firePropertyChange(FILE_WAS_MODIFIED, null, file);
//						}
//					}
//				}
//
//				if (!key.reset()) {
//					break; // loop
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}

	public synchronized void stop() {
		stop = true;
	}
}
