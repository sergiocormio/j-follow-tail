package view.highlightings;

import java.awt.Color;

public class Highlighting {
	private String token;
	private Color backgroundColor;
	
	public Highlighting(String token, Color backgroundColor) {
		this.token = token;
		this.backgroundColor = backgroundColor;
	}
	
	public Color getBackgroundColor() {
		return backgroundColor;
	}
	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
	@Override
	public boolean equals(Object obj) {
		Highlighting otherObj = (Highlighting) obj;
		return token.equals(otherObj.getToken()) && backgroundColor.equals(otherObj.getBackgroundColor());
	}
}
