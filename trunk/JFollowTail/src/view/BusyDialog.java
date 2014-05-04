package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.util.PaintUtils;

public class BusyDialog extends JDialog {
	private static final long serialVersionUID = 1121077526401665245L;
	private String text;
	private JLabel textLabel;
	private JXPanel mainPanel;
	
	private BusyDialog(Frame owner, String text){
		super(owner);
		this.text = text;
		createUI();
	}

	private void createUI() {
		mainPanel = new JXPanel(new BorderLayout());
		mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		JXBusyLabel busyLabel = new JXBusyLabel(new Dimension(50, 50));
		busyLabel.setDelay(100);
		busyLabel.getBusyPainter().setHighlightColor(new Color(44, 61, 146).darker()); 
		busyLabel.getBusyPainter().setBaseColor(new Color(168, 204, 241).brighter());
		busyLabel.getBusyPainter().setPoints(15);
		busyLabel.getBusyPainter().setTrailLength(15);
		busyLabel.setBusy(true);
		JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		northPanel.add(busyLabel);
		northPanel.setOpaque(false);
		mainPanel.add(northPanel,BorderLayout.NORTH);
		
		textLabel = new JLabel(text);
		textLabel.setForeground(Color.WHITE);
		mainPanel.add(textLabel,BorderLayout.SOUTH);
		mainPanel.setBackgroundPainter(new MattePainter(PaintUtils.MAC_OSX, true)); 
		add(mainPanel);
//		setModal(true);
		setUndecorated(true);
		pack();
		setLocationRelativeTo(this.getOwner());
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		textLabel.setText(this.text);
		this.pack();
		textLabel.updateUI();
		mainPanel.updateUI();
	}

	public synchronized static BusyDialog showBusyDialog(Frame owner, String text,SwingWorker<?, ?> worker) {
		BusyDialog bd = new BusyDialog(owner, text);
		bd.setVisible(true);
		worker.execute();
		return bd;
	}

}
