package view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXLabel;

import resources.ResourcesFactory;

public class AboutDialog extends JDialog {

	private static final String CREATOR_EMAIL = "sergiocormio@gmail.com";
	private static final String YEAR_AND_CREATOR = "2014 - Sergio A. Cormio";
	private static final String DESCRIPTION = "Another free real-time log file monitoring tool. Made in Java.";
	private static final String APP_NAME= "JFollowTail";
	private static final String NAME_AND_VERSION = APP_NAME + " 1.0";
	private static final String LOGO_PROVIDER_LINK = "http://www.pelfusion.com";
	private static final String APP_WEB_PAGE_LINK = "https://code.google.com/p/j-follow-tail";
	private static final String SPECIAL_THANKS_TEXT = "Special thanks to Andrés Gutierrez Camacho, Pablo Cúneo and the Argentinian development team of 911 Emergencies system.";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2566945927276159128L;
	
	public AboutDialog(JFrame owner){
		super(owner);
		createUI();
	}

	private void createUI() {
		setTitle("About JFollowTail");
		createMainPanel();
		this.addMouseListener(new MouseListener() {
			
			public void mouseReleased(MouseEvent e) {
			}
			public void mousePressed(MouseEvent e) {
			}
			public void mouseExited(MouseEvent e) {
			}
			public void mouseEntered(MouseEvent e) {
			}
			
			public void mouseClicked(MouseEvent e) {
				//Close the app
				AboutDialog.this.setVisible(false);
			}
		});
		setUndecorated(true);
		setModal(true);
		setResizable(false);
		pack();
		setLocationRelativeTo(this.getOwner());
	}

	private void createMainPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		Font boldFont = new Font("Arial", Font.BOLD,14);
		Font normalFont = new Font("Courier", Font.PLAIN,12);
		//Image
		JLabel appImageLabel = new JLabel(ResourcesFactory.getAppImage());
		appImageLabel.setAlignmentX(CENTER_ALIGNMENT);
		panel.add(appImageLabel);
		//Name and version
		JLabel nameAndVersionLabel = new JLabel(NAME_AND_VERSION);
		nameAndVersionLabel.setAlignmentX(CENTER_ALIGNMENT);
		nameAndVersionLabel.setFont(boldFont);
		panel.add(nameAndVersionLabel);
		//Web page
		addAppWebPage(panel, normalFont);
		//Description
		JLabel descriptionLabel = new JLabel(DESCRIPTION);
		descriptionLabel.setAlignmentX(CENTER_ALIGNMENT);
		descriptionLabel.setFont(normalFont);
		panel.add(descriptionLabel);
		//space filler
		panel.add(Box.createRigidArea(new Dimension(0,12)));
		//special thanks
		addSpecialThanks(panel, normalFont);
		//space filler
		panel.add(Box.createRigidArea(new Dimension(0,12)));
		//logo provider
		addLogoProviderText(panel, normalFont);
		//space filler
		panel.add(Box.createRigidArea(new Dimension(0,12)));
		//year and creator
		JLabel yearAndCreatorLabel = new JLabel(YEAR_AND_CREATOR);
		yearAndCreatorLabel.setAlignmentX(CENTER_ALIGNMENT);
		yearAndCreatorLabel.setFont(normalFont);
		panel.add(yearAndCreatorLabel);
		//email
		addCreatorEmail(panel, normalFont);
		this.add(panel);
	}

	private void addSpecialThanks(JPanel panel, Font normalFont) {
		JXLabel specialThanksLabel = new JXLabel(SPECIAL_THANKS_TEXT);
		specialThanksLabel.setAlignmentX(CENTER_ALIGNMENT);
		specialThanksLabel.setTextAlignment(JXLabel.TextAlignment.CENTER);
		specialThanksLabel.setFont(normalFont);
		specialThanksLabel.setLineWrap(true);
		specialThanksLabel.setPreferredSize(new Dimension(450, 40));
		panel.add(specialThanksLabel);
	}

	private void addAppWebPage(JPanel panel, Font normalFont) {
		JXHyperlink webLink = new JXHyperlink();
		try {
			webLink.setURI(new URI(APP_WEB_PAGE_LINK));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		webLink.setAlignmentX(CENTER_ALIGNMENT);
		webLink.setFont(normalFont);
		panel.add(webLink);
	}

	private void addCreatorEmail(JPanel panel, Font normalFont) {
		JXHyperlink emailLink = new JXHyperlink();
		try {
			emailLink.setURI(new URI("mailto://"+CREATOR_EMAIL));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		emailLink.setText(CREATOR_EMAIL);
		emailLink.setAlignmentX(CENTER_ALIGNMENT);
		emailLink.setFont(normalFont);
		panel.add(emailLink);
	}

	private void addLogoProviderText(JPanel panel, Font normalFont) {
		JPanel logoProviderPanel = new JPanel(new FlowLayout());
		JLabel logoProviderSentence = new JLabel(APP_NAME + "'s logo powered by");
		logoProviderSentence.setFont(normalFont);
		logoProviderPanel.add(logoProviderSentence);
		JXHyperlink hypelink = new JXHyperlink();
		hypelink.setFont(normalFont);
		try {
			hypelink.setURI(new URI(LOGO_PROVIDER_LINK));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		logoProviderPanel.add(hypelink);
		panel.add(logoProviderPanel);
	}

}
