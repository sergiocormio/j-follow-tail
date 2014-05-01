package view.highlightings;

import java.awt.Color;
import java.util.LinkedList;
import java.util.prefs.Preferences;

public class HighlightingPersistor {
	private static final String HIGHLIGHTING_QUANTITY = "HIGHLIGHTING_QUANTITY";
	private static final String HIGHLIGHTING_PREFIX = "HIGHLIGHTING_";
	private static final String HIGHLIGHTING_BACKGROUND_COLOR_PREFIX = HIGHLIGHTING_PREFIX + "COLOR_";
	private static final String HIGHLIGHTING_TOKEN_PREFIX = HIGHLIGHTING_PREFIX + "TOKEN_";
	private Preferences preferences;
	
	public HighlightingPersistor(){
		preferences = Preferences.userRoot().node(this.getClass().getName());
	}
	
	/**
	 * Save the highlightings in user preferences
	 * @param highlightings
	 */
	public void saveHighlightings(LinkedList<Highlighting> highlightings){
		preferences.putInt(HIGHLIGHTING_QUANTITY, highlightings.size());
		Highlighting highlighting = null;
		for(int i = 0; i <  highlightings.size() ; i++){
			highlighting = highlightings.get(i);
			preferences.putInt(HIGHLIGHTING_BACKGROUND_COLOR_PREFIX + i, highlighting.getBackgroundColor().getRGB());
			preferences.put(HIGHLIGHTING_TOKEN_PREFIX + i, highlighting.getToken());
		}
	}
	
	/**
	 * Load the highlightings from user preferences
	 * @return
	 */
	public LinkedList<Highlighting> loadHighlightings(){
		LinkedList<Highlighting> highlightings = new LinkedList<Highlighting>();
		int size = preferences.getInt(HIGHLIGHTING_QUANTITY, 0);
		if(size>0){
			int backgroundColorRGB = 0;
			String token = null;
			for(int i = 0; i < size ; i++){
				backgroundColorRGB = preferences.getInt(HIGHLIGHTING_BACKGROUND_COLOR_PREFIX + i, Color.WHITE.getRGB());
				token = preferences.get(HIGHLIGHTING_TOKEN_PREFIX + i,"");
				highlightings.add(new Highlighting(token, new Color(backgroundColorRGB)));
			}
		}
		
		return highlightings;
	}
	
	public static void main(String[] args) {
		//TODO delete this mock lines
		LinkedList<Highlighting> highlightings = new LinkedList<Highlighting>();
		highlightings.add(new Highlighting("ar.com.tsoluciones", Color.LIGHT_GRAY));
		highlightings.add(new Highlighting("ERROR", Color.RED));
		highlightings.add(new Highlighting("Exception", Color.RED));
		highlightings.add(new Highlighting("BLOCKED", Color.RED));
		highlightings.add(new Highlighting("WARN", Color.YELLOW));
		highlightings.add(new Highlighting("WAITING", Color.YELLOW));
		highlightings.add(new Highlighting("INFO", Color.GREEN));
		highlightings.add(new Highlighting("runnable", Color.GREEN));
		new HighlightingPersistor().saveHighlightings(highlightings);
	}
}
