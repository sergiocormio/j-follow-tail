package resources;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class ResourcesFactory {
	
		//APP icon
		public static ImageIcon getAppIcon(){
			return new ImageIcon(ResourcesFactory.class.getResource("log-icon_32.png"));
		}
		
		//Log icon
		public static ImageIcon getActiveLogIcon(){
			return new ImageIcon(ResourcesFactory.class.getResource("log-icon_16.png"));
		}
		
		//Log icon
		public static ImageIcon getInactiveLogIcon(){
			return new ImageIcon(ResourcesFactory.class.getResource("log-icon-gray_16.png"));
		}
		
		//Open icon
		public static Icon getOpenIcon(){
			return new ImageIcon(ResourcesFactory.class.getResource("open-file.png"));
		}
		
		//APP Image
		public static Icon getAppImage() {
			return new ImageIcon(ResourcesFactory.class.getResource("log-icon_128.png"));
		}
		
		//Highlighting
		public static ImageIcon getHighlightingIcon() {
			return new ImageIcon(ResourcesFactory.class.getResource("highlighter-text.png"));
		}
		
		//Find
		public static ImageIcon getFindIcon() {
			return new ImageIcon(ResourcesFactory.class.getResource("find.png"));
		}

		public static Icon getColorEditIcon() {
			return new ImageIcon(ResourcesFactory.class.getResource("color--pencil.png"));
		}
		
}
