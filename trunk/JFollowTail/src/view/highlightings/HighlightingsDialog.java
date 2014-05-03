package view.highlightings;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import resources.ResourcesFactory;

public class HighlightingsDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3825981838950942111L;
	public static final String LIST_CHANGED_EVENT = "List_changed_event";
	private JTable highlightingsTable;
	private LinkedList<Highlighting> highlightings;
	private JTextField tokenTextfield;
	private JComboBox backgroundColorComboBox,foregroundColorComboBox;
	private JPanel dialogPanel;
	private ColorItem customBackgroundColorItem, customForegroundColorItem;

	public HighlightingsDialog(JFrame owner,
			LinkedList<Highlighting> highlightings) {
		super(owner);
		this.highlightings = highlightings;
		createUI();
	}

	private void createUI() {
		setTitle("Highlighting");
		setIconImage(ResourcesFactory.getHighlightingIcon().getImage());
		dialogPanel = new JPanel(new BorderLayout());
		dialogPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		createTablePanel();
		createCRUDPanel();
		this.add(dialogPanel);
		pack();
		setPreferredSize(new Dimension(350,400));
		setSize(350, 400);
		setMaximumSize(new Dimension(350,400));
		setResizable(false);
		setLocationRelativeTo(this.getOwner());
		setModal(true);
	}

	private void createCRUDPanel() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		addTopButtonsPanel(mainPanel);
		addIndividualCRUDPanel(mainPanel);
		addBottomButtonsPanel(mainPanel);
		dialogPanel.add(mainPanel, BorderLayout.SOUTH);
	}

	private void addBottomButtonsPanel(JPanel mainPanel) {
		JPanel bottomButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		JButton saveButton = new JButton("OK");
		saveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new HighlightingPersistor().saveHighlightings(highlightings);
				HighlightingsDialog.this.setVisible(false);
				HighlightingsDialog.this.dispose();
			}
		});
		bottomButtonsPanel.add(saveButton);
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//load highlightings again from user preferences
				highlightings.clear();
				highlightings.addAll(new HighlightingPersistor().loadHighlightings());
				HighlightingsDialog.this.setVisible(false);
				HighlightingsDialog.this.dispose();
			}
		});
		
		bottomButtonsPanel.add(cancelButton);
		mainPanel.add(bottomButtonsPanel, BorderLayout.SOUTH);
	}

	private void addIndividualCRUDPanel(JPanel mainPanel) {
		JPanel individualPanel = new JPanel();
		individualPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		individualPanel.setLayout(new BoxLayout(individualPanel, BoxLayout.Y_AXIS));
		individualPanel.add(Box.createRigidArea(new Dimension(0,5)));
		addBackgroundColorItem(individualPanel);
		individualPanel.add(Box.createRigidArea(new Dimension(0,5)));
		addForegroundColorItem(individualPanel);
		individualPanel.add(Box.createRigidArea(new Dimension(0,5)));
		addStringItem(individualPanel);
		individualPanel.add(Box.createRigidArea(new Dimension(0,5)));
		mainPanel.add(individualPanel, BorderLayout.CENTER);
	}

	private void addStringItem(JPanel individualPanel) {
		JPanel stringPanel = new JPanel(new BorderLayout(5,5));
		stringPanel.add(new JLabel("String:"),BorderLayout.WEST);
		tokenTextfield = new JTextField();
		tokenTextfield.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				Highlighting selectedHighlighting = getSelectedHighlighting();
				if(selectedHighlighting!=null){
					if(tokenTextfield.getText().equals(selectedHighlighting.getToken())){
						return;
					}
					selectedHighlighting.setToken(tokenTextfield.getText());
					int selectedRow = highlightingsTable.getSelectedRow();
					reloadTableData();
					highlightingsTable.setRowSelectionInterval(selectedRow, selectedRow);
					firePropertyChange(LIST_CHANGED_EVENT, null, null);
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
		stringPanel.add(tokenTextfield, BorderLayout.CENTER);
		individualPanel.add(stringPanel);
	}

	private void addBackgroundColorItem(JPanel individualPanel) {
		JPanel bgColorPanel = new JPanel(new BorderLayout(5,5));
		bgColorPanel.add(new JLabel("Background Color:"),BorderLayout.WEST);
		//COMBO BOX
		List<ColorItem> elements = new ArrayList<ColorItem>();
		customBackgroundColorItem = new ColorItem(Color.decode("#00FFCC"), "Custom");
		elements.add(customBackgroundColorItem);
		elements.addAll(ColorItem.getDefaultColorItems());
		backgroundColorComboBox = new JComboBox(elements.toArray(new ColorItem[elements.size()]));
		backgroundColorComboBox.setRenderer(new ColorItemComboRenderer());
		backgroundColorComboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setBackgroundSelectedColorToHighlighting();
			}
		});
		bgColorPanel.add(backgroundColorComboBox,BorderLayout.CENTER);
		//Select Custom Color button
		JButton selectCustomColorButton = new JButton(ResourcesFactory.getColorEditIcon());
		selectCustomColorButton.setToolTipText("Click to select Custom Color");
		selectCustomColorButton.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Color initialColor = Color.red;
				if(backgroundColorComboBox.getSelectedItem() != null){
					ColorItem colorItemSelected = (ColorItem) backgroundColorComboBox.getSelectedItem();
					initialColor = colorItemSelected.getColor();
				}
				Color newColor = JColorChooser.showDialog(HighlightingsDialog.this,"Choose Custom Color",initialColor);
				if (newColor != null) {
					customBackgroundColorItem.setColor(newColor);
					backgroundColorComboBox.setSelectedIndex(0);
					backgroundColorComboBox.updateUI();
				}
			}
			
		});
		bgColorPanel.add(selectCustomColorButton,BorderLayout.EAST);
		individualPanel.add(bgColorPanel);
	}
	
	private void addForegroundColorItem(JPanel individualPanel) {
		JPanel fgColorPanel = new JPanel(new BorderLayout(5,5));
		fgColorPanel.add(new JLabel("Foreground Color:"),BorderLayout.WEST);
		//COMBO BOX
		List<ColorItem> elements = new ArrayList<ColorItem>();
		customForegroundColorItem = new ColorItem(Color.decode("#333333"), "Custom");
		elements.add(customForegroundColorItem);
		elements.addAll(ColorItem.getDefaultColorItems());
		foregroundColorComboBox = new JComboBox(elements.toArray(new ColorItem[elements.size()]));
		foregroundColorComboBox.setRenderer(new ColorItemComboRenderer());
		foregroundColorComboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setForegroundSelectedColorToHighlighting();
			}
		});
		fgColorPanel.add(foregroundColorComboBox,BorderLayout.CENTER);
		//Select Custom Color button
		JButton selectCustomColorButton = new JButton(ResourcesFactory.getColorEditIcon());
		selectCustomColorButton.setToolTipText("Click to select Custom Color");
		selectCustomColorButton.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Color initialColor = Color.red;
				if(foregroundColorComboBox.getSelectedItem() != null){
					ColorItem colorItemSelected = (ColorItem) foregroundColorComboBox.getSelectedItem();
					initialColor = colorItemSelected.getColor();
				}
				Color newColor = JColorChooser.showDialog(HighlightingsDialog.this,"Choose Custom Color",initialColor);
				if (newColor != null) {
					customForegroundColorItem.setColor(newColor);
					foregroundColorComboBox.setSelectedIndex(0);
					foregroundColorComboBox.updateUI();
				}
			}
			
		});
		fgColorPanel.add(selectCustomColorButton,BorderLayout.EAST);
		individualPanel.add(fgColorPanel);
	}

	protected void setBackgroundSelectedColorToHighlighting() {
		if(backgroundColorComboBox.getSelectedItem() == null){
			return;
		}
		Highlighting selectedHighlighting = getSelectedHighlighting();
		if(selectedHighlighting == null){
			return;
		}
		ColorItem colorItemSelected = (ColorItem) backgroundColorComboBox.getSelectedItem();
		selectedHighlighting.setBackgroundColor(colorItemSelected.getColor());
		int selectedRow = highlightingsTable.getSelectedRow();
		reloadTableData();
		highlightingsTable.setRowSelectionInterval(selectedRow, selectedRow);
		firePropertyChange(LIST_CHANGED_EVENT, null, null);
	}
	
	protected void setForegroundSelectedColorToHighlighting() {
		if(foregroundColorComboBox.getSelectedItem() == null){
			return;
		}
		Highlighting selectedHighlighting = getSelectedHighlighting();
		if(selectedHighlighting == null){
			return;
		}
		ColorItem colorItemSelected = (ColorItem) foregroundColorComboBox.getSelectedItem();
		selectedHighlighting.setForegroundColor(colorItemSelected.getColor());
		int selectedRow = highlightingsTable.getSelectedRow();
		reloadTableData();
		highlightingsTable.setRowSelectionInterval(selectedRow, selectedRow);
		firePropertyChange(LIST_CHANGED_EVENT, null, null);
	}

	private void addTopButtonsPanel(JPanel mainPanel) {
		JPanel topButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
		//ADD
		JButton addButton = new JButton("Add");
		addButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addNewHighlighting();
				firePropertyChange(LIST_CHANGED_EVENT, null, null);
			}
		});
		topButtonsPanel.add(addButton);
		
		//DELETE
		JButton deleteButton = new JButton("Delete");
		deleteButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteSelectedHighlighting();
				firePropertyChange(LIST_CHANGED_EVENT, null, null);
			}
		});
		topButtonsPanel.add(deleteButton);
		
		//MOVE UP
		JButton moveUpButton = new JButton("Move Up");
		moveUpButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				moveHighlightingSelectedUp();
				firePropertyChange(LIST_CHANGED_EVENT, null, null);
			}

		});
		topButtonsPanel.add(moveUpButton);
		
		//MOVE DOWN
		JButton moveDownButton = new JButton("Move Down");
		moveDownButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				moveHighlightingSelectedDown();
				firePropertyChange(LIST_CHANGED_EVENT, null, null);
			}
		});
		topButtonsPanel.add(moveDownButton);
		
		mainPanel.add(topButtonsPanel, BorderLayout.NORTH);
	}
	
	protected void addNewHighlighting() {
		int position = highlightingsTable.getSelectedRow() + 1;
		ColorItem bgColorItemSelected = (ColorItem) backgroundColorComboBox.getSelectedItem();
		ColorItem fgColorItemSelected = (ColorItem) foregroundColorComboBox.getSelectedItem();
		Highlighting newHighlighting = new Highlighting(tokenTextfield.getText(), bgColorItemSelected.getColor(), fgColorItemSelected.getColor());
		highlightings.add(position, newHighlighting);
		reloadTableData();
		highlightingsTable.setRowSelectionInterval(position, position);
	}

	protected void deleteSelectedHighlighting() {
		int selectedRow = highlightingsTable.getSelectedRow();
		if(selectedRow < 0){
			return;
		}
		highlightings.remove(selectedRow);
		reloadTableData();
		if(highlightings.size()>0){
			if(highlightings.size()>selectedRow){
				highlightingsTable.setRowSelectionInterval(selectedRow, selectedRow);
			}if(highlightings.size()>selectedRow-1){
				highlightingsTable.setRowSelectionInterval(selectedRow-1, selectedRow-1);
			}
		}
	}
	
	private Highlighting getSelectedHighlighting(){
		int selectedRow = highlightingsTable.getSelectedRow();
		if(selectedRow < 0){
			return null;
		}
		return highlightings.get(selectedRow);
	}

	private void moveHighlightingSelectedDown() {
		int selectedRow = highlightingsTable.getSelectedRow();
		if((selectedRow < 0) || (selectedRow == highlightings.size() -1)){
			return;
		}
		
		Highlighting highlighting = highlightings.remove(selectedRow);
		highlightings.add(selectedRow + 1, highlighting);
		reloadTableData();
		highlightingsTable.setRowSelectionInterval(selectedRow + 1, selectedRow + 1);
	}
	
	private void moveHighlightingSelectedUp() {
		int selectedRow = highlightingsTable.getSelectedRow();
		if(selectedRow <= 0){
			return;
		}
		
		Highlighting highlighting = highlightings.remove(selectedRow);
		highlightings.add(selectedRow - 1, highlighting);
		reloadTableData();
		highlightingsTable.setRowSelectionInterval(selectedRow - 1, selectedRow - 1);
	}
	
	private void loadTableData() {
		Object[][] highlightingsAsObjects = convertHighlightingsToRows();
		highlightingsTable.setModel(new DefaultTableModel(
				highlightingsAsObjects, new String[2]) {
			/**
				 * 
				 */
			private static final long serialVersionUID = 8983499024113385242L;

			// set table is read-Only
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
			
			 /*
	         * JTable uses this method to determine the default renderer/
	         * editor for each cell.  If we didn't implement this method,
	         * then the last column would contain text ("true"/"false"),
	         * rather than a check box.
	         */
			@Override
	        public Class getColumnClass(int c) {
	            return getValueAt(0, c).getClass();
	        }
		});
		
	}
	
	private void reloadTableData() {
		Object[][] highlightingsAsObjects = convertHighlightingsToRows();
		DefaultTableModel model = (DefaultTableModel)highlightingsTable.getModel();
		model.setDataVector(highlightingsAsObjects, new String[2]);
		fixColumnsSize();
	}

	private void createTablePanel() {
		JPanel tablePanel = new JPanel(new BorderLayout());
		tablePanel.setBorder(BorderFactory.createLoweredBevelBorder());
		highlightingsTable = new JTable();
		highlightingsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				updateIndividualPanel();
			}
		});
		loadTableData();
		//hide columns
		highlightingsTable.setTableHeader(null);
		highlightingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//adds a custom renderer to first column
		highlightingsTable.setDefaultRenderer(Highlighting.class, new ColorRenderer());
		//Fixed width to the first column...
		fixColumnsSize();
		//hide grid
		highlightingsTable.setShowGrid(false);
		highlightingsTable.setIntercellSpacing(new Dimension(0, 0));
		JScrollPane tableScroll = new JScrollPane(highlightingsTable);
		tableScroll.getViewport().setBackground(Color.WHITE);
		tablePanel.add(tableScroll, BorderLayout.CENTER);
		dialogPanel.add(tablePanel, BorderLayout.CENTER);
	}

	protected void updateIndividualPanel() {
		Highlighting selectedHighlighting = getSelectedHighlighting();
		if(selectedHighlighting == null){
			return;
		}
		tokenTextfield.setText(selectedHighlighting.getToken());
		backgroundColorComboBox.setSelectedItem(new ColorItem(selectedHighlighting.getBackgroundColor(),""));
		//if backgroundcolor there was not in comboBox, change "Custom" color
		if(backgroundColorComboBox.getSelectedItem() != null){
			ColorItem colorItemSelected = (ColorItem) backgroundColorComboBox.getSelectedItem();
			if(!colorItemSelected.getColor().equals(selectedHighlighting.getBackgroundColor())){
				customBackgroundColorItem.setColor(selectedHighlighting.getBackgroundColor());
				backgroundColorComboBox.setSelectedIndex(0);
				backgroundColorComboBox.updateUI();
			}
		}
		
		foregroundColorComboBox.setSelectedItem(new ColorItem(selectedHighlighting.getForegroundColor(),""));
		//if foregroundColor there was not in comboBox, change "Custom" color
		if(foregroundColorComboBox.getSelectedItem() != null){
			ColorItem colorItemSelected = (ColorItem) foregroundColorComboBox.getSelectedItem();
			if(!colorItemSelected.getColor().equals(selectedHighlighting.getForegroundColor())){
				customForegroundColorItem.setColor(selectedHighlighting.getForegroundColor());
				foregroundColorComboBox.setSelectedIndex(0);
				foregroundColorComboBox.updateUI();
			}
		}
	}

	private void fixColumnsSize() {
		TableColumnModel columnModel = highlightingsTable.getColumnModel();
		columnModel.getColumn(0).setMaxWidth(95);
		columnModel.getColumn(0).setPreferredWidth(95);
		highlightingsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
	}

	private Object[][] convertHighlightingsToRows() {
		Object[][] data = new Object[highlightings.size()][2];
		for (int i = 0; i < highlightings.size(); i++) {
			Object[] row = new Object[2];
			row[0] = highlightings.get(i);
			row[1] = highlightings.get(i).getToken();
			data[i] = row;
		}
		return data;
	}

	/**
	 * Renderer of the first column of the table
	 * @author Sergio Cormio
	 *
	 */
	private class ColorRenderer extends JLabel implements TableCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = -1556533882997878731L;
		private Border unselectedBorder = null;
		private Border selectedBorder = null;
	    
		public ColorRenderer() {
			setOpaque(true); // MUST do this for background to show up.
		}

		public Component getTableCellRendererComponent(JTable table,
				Object data, boolean isSelected, boolean hasFocus, int row,
				int column) {
			Highlighting highlighting = (Highlighting) data;
			setBackground(highlighting.getBackgroundColor());
			setForeground(highlighting.getForegroundColor());
			setText(highlighting.getToken());
			// unbold
			Font f = getFont();
			setFont(f.deriveFont(f.getStyle() & ~Font.BOLD));
			if (isSelected) {
				// selectedBorder is a solid border in the color
				// table.getSelectionBackground().
				if (selectedBorder == null) {
                    selectedBorder = BorderFactory. createMatteBorder(1,1,1,1,
                                              table.getSelectionBackground());
                }
                setBorder(selectedBorder);
			} else {
				// unselectedBorder is a solid border in the color
				// table.getBackground().
				if (unselectedBorder == null) {
                    unselectedBorder = BorderFactory.createMatteBorder(1,1,1,1,
                                              table.getBackground());
                }
                setBorder(unselectedBorder);
			}

			return this;
		}
	}

}
