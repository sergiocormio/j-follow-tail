package model;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

/**
 * Class encharge of listening file changes from file system
 * 
 * @author Sergio Cormio
 * 
 */
public class FileListener implements Runnable {
	public static final String FILE_WAS_MODIFIED = "File was modified";
	private boolean stop = false;
	private WatchService service;
	private File file;
	private PropertyChangeSupport propertyChangeSupport;

	public FileListener(File file) throws IOException {
		this.file = file;
		propertyChangeSupport = new PropertyChangeSupport(this);
		// Path must be a directory
		Path path = Paths.get(file.getParentFile().toURI());
		service = path.getFileSystem().newWatchService();
		// We register the path to the service
		// We watch for creation events
		path.register(service, ENTRY_CREATE, ENTRY_MODIFY);
		// Start the infinite polling loop
		new Thread(this).start();
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	@Override
	public void run() {
		WatchKey key = null;

		while (!stop) {
			try {
				key = service.take();

				// Dequeueing events
				Kind<?> kind = null;
				for (WatchEvent<?> watchEvent : key.pollEvents()) {
					// Get the type of the event
					kind = watchEvent.kind();
					if (OVERFLOW == kind) {
						continue; // loop
					} else if (ENTRY_CREATE == kind) {
						// A new Path was created
						// Path newPath = ((WatchEvent<Path>)
						// watchEvent).context();
						// Output
						// System.out.println("New path created: " + newPath);
					} else if (ENTRY_MODIFY == kind) {
						// A Modification happened
						Path modifiedPath = ((WatchEvent<Path>) watchEvent)
								.context();
						if (modifiedPath.endsWith(file.getName())) {
							// Output
							System.out.println("Se Modificó!!!" + modifiedPath);
							propertyChangeSupport.firePropertyChange(
									FILE_WAS_MODIFIED, null, file);
						}
					}
				}

				if (!key.reset()) {
					break; // loop
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
