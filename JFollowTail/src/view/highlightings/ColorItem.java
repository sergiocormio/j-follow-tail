package view.highlightings;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class ColorItem {
	private Color color;
	private String name;
	
	public ColorItem(Color color, String name) {
		this.color = color;
		this.name = name;
	}
	
	public static List<ColorItem> getDefaultColorItems(){
		List<ColorItem> defaultColors = new ArrayList<ColorItem>();
		defaultColors.add(new ColorItem(Color.BLACK,"Black"));
		defaultColors.add(new ColorItem(Color.WHITE,"White"));
		defaultColors.add(new ColorItem(Color.RED,"Red"));
		defaultColors.add(new ColorItem(Color.GREEN,"Green"));
		defaultColors.add(new ColorItem(Color.BLUE,"Blue"));
		defaultColors.add(new ColorItem(Color.YELLOW,"Yellow"));
		defaultColors.add(new ColorItem(Color.CYAN,"Cyan"));
		defaultColors.add(new ColorItem(Color.MAGENTA,"Magenta"));
		defaultColors.add(new ColorItem(Color.LIGHT_GRAY,"Light Gray"));
		defaultColors.add(new ColorItem(Color.GRAY,"Gray"));
		defaultColors.add(new ColorItem(Color.DARK_GRAY,"Dark Gray"));
		return defaultColors;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ColorItem){
			ColorItem otherObj = (ColorItem) obj;
			return color.equals(otherObj.getColor());
		}else if(obj instanceof Color){
			Color otherObj = (Color) obj;
			return color.equals(otherObj);
		}else{
			return false;
		}
	}
}
