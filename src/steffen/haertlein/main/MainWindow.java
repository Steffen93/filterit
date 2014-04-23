package steffen.haertlein.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTable;

public class MainWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Vector<FileObject> selectedFiles = new Vector<FileObject>();
	private JTree tree;
	private DefaultTreeModel treeModel;
	private File currentPath;
	private JTextArea textArea;
	private JTextField ruleTextField;
	private JXTable filterTable;
	private DefaultTableModel tableModel;
	private String[] columnNames;
	private String[][] tableData = new String[0][];
	private Vector<Rule> rules = new Vector<Rule>();
	private JTabbedPane tabbedPane;
	private Highlighter hiLighter;
	private Highlighter.HighlightPainter coloredPainter = new DefaultHighlighter.DefaultHighlightPainter(
			Color.RED);
	private JButton btnWeiter;
	private JTextField txtLinesBefore;
	private JTextField txtLinesAfter;
	private JLabel lblInformation;

	public MainWindow() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("My File Parser");
		initGUI();
		pack();
		setMinimumSize(getSize());
		setLocationRelativeTo(null);
	}

	private void initGUI() {
		JPanel mainPanel = new JPanel();
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(new MigLayout("", "[grow]", "[grow]"));

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		mainPanel.add(tabbedPane, "cell 0 0,grow");

		JPanel panel_8 = new JPanel();
		tabbedPane.addTab("Dateien", null, panel_8, null);
		panel_8.setLayout(new BorderLayout(0, 0));
		tree = new JTree();
		tree.setModel(treeModel);

		JScrollPane treePane = new JScrollPane(tree);
		panel_8.add(treePane, BorderLayout.CENTER);
		treePane.setMinimumSize(new Dimension(200, 200));

		JPanel panel_9 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_9.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel_8.add(panel_9, BorderLayout.NORTH);

		JButton btnChooseFiles = new JButton("Dateien wählen...");

		btnChooseFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (chooseFiles()) {
						tabbedPane.setEnabledAt(1, true);
						btnWeiter.setEnabled(true);
					}
				} catch (IOException | BadLocationException e) {
					e.printStackTrace();
				}
			}
		});
		panel_9.add(btnChooseFiles);

		JPanel panel_10 = new JPanel();
		panel_8.add(panel_10, BorderLayout.SOUTH);
		panel_10.setLayout(new BorderLayout(0, 0));

		JPanel panel_11 = new JPanel();
		panel_10.add(panel_11, BorderLayout.WEST);

		lblInformation = new JLabel("");
		panel_11.add(lblInformation);

		JPanel panel_5 = new JPanel();
		panel_10.add(panel_5, BorderLayout.EAST);

		btnWeiter = new JButton("Weiter");
		panel_5.add(btnWeiter);
		btnWeiter.setEnabled(false);
		btnWeiter.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				tabbedPane.setSelectedIndex((tabbedPane.getSelectedIndex() + 1)
						% tabbedPane.getTabCount());
			}
		});

		JPanel panel = new JPanel();
		tabbedPane.addTab("Filterregeln", null, panel, null);
		tabbedPane.setEnabledAt(1, false);
		panel.setLayout(new BorderLayout(0, 0));

		JPanel panel_1 = new JPanel();
		panel.add(panel_1, BorderLayout.NORTH);

		ruleTextField = new JTextField();
		ruleTextField.setColumns(10);

		JButton btnAddRule = new JButton("Regel hinzufügen");
		btnAddRule.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				addRule();
			}
		});
		panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JLabel lblText = new JLabel("Text:");
		lblText.setLabelFor(ruleTextField);
		panel_1.add(lblText);
		panel_1.add(ruleTextField);

		JLabel lblZeilenDavor = new JLabel("Zeilen davor:");
		panel_1.add(lblZeilenDavor);

		txtLinesBefore = new JTextField();
		lblZeilenDavor.setLabelFor(txtLinesBefore);
		txtLinesBefore.setHorizontalAlignment(SwingConstants.TRAILING);
		txtLinesBefore.setText("0");
		txtLinesBefore.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {

			}

			@Override
			public void focusGained(FocusEvent arg0) {
				txtLinesBefore.selectAll();
			}
		});
		txtLinesBefore.setColumns(3);
		panel_1.add(txtLinesBefore);

		JLabel lblZeilenDanach = new JLabel("Zeilen danach:");
		panel_1.add(lblZeilenDanach);

		txtLinesAfter = new JTextField();
		lblZeilenDanach.setLabelFor(txtLinesAfter);
		txtLinesAfter.setHorizontalAlignment(SwingConstants.TRAILING);
		txtLinesAfter.setText("0");
		txtLinesAfter.setColumns(3);
		txtLinesAfter.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {

			}

			@Override
			public void focusGained(FocusEvent arg0) {
				txtLinesBefore.selectAll();
			}
		});
		panel_1.add(txtLinesAfter);
		panel_1.add(btnAddRule);

		JPanel panel_2 = new JPanel();
		panel.add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new BorderLayout(0, 0));

		columnNames = new String[] { "Name", "Zeilen davor", "Zeilen danach" };
		tableModel = new DefaultTableModel(tableData, columnNames);
		filterTable = new JXTable(tableModel);
		filterTable.setEditable(false);
		filterTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPane = new JScrollPane(filterTable);
		panel_2.add(scrollPane);
		scrollPane.setMinimumSize(new Dimension(150, 200));

		JPanel panel_3 = new JPanel();
		panel_2.add(panel_3, BorderLayout.SOUTH);

		JButton btnDelRules = new JButton("Ausgewählte Regeln löschen");
		panel_3.add(btnDelRules);

		btnDelRules.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				delRules();
			}
		});

		JButton btnDelAllRules = new JButton("Alle Regeln löschen");
		panel_3.add(btnDelAllRules);

		JPanel panel_4 = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) panel_4.getLayout();
		flowLayout_2.setAlignment(FlowLayout.RIGHT);
		panel.add(panel_4, BorderLayout.SOUTH);

		JButton btnZurueck = new JButton("Zurück");
		btnZurueck.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				tabbedPane.setSelectedIndex((tabbedPane.getSelectedIndex() - 1)
						% tabbedPane.getTabCount());
			}
		});
		panel_4.add(btnZurueck);

		JButton btnApplyFilter = new JButton("Filtern");
		panel_4.add(btnApplyFilter);
		btnApplyFilter.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					applyFilters();
					tabbedPane.setEnabledAt(2, true);
				} catch (IOException | BadLocationException e) {
					e.printStackTrace();
				}
			}
		});

		JPanel panel_6 = new JPanel();
		tabbedPane.addTab("Ausgabe", null, panel_6, null);
		tabbedPane.setEnabledAt(2, false);
		panel_6.setLayout(new BorderLayout(0, 0));
		textArea = new JTextArea();
		textArea.setEditable(false);
		JScrollPane textPane = new JScrollPane(textArea);
		panel_6.add(textPane);
		textPane.setMinimumSize(new Dimension(300, 200));

		JPanel panel_7 = new JPanel();
		FlowLayout flowLayout_3 = (FlowLayout) panel_7.getLayout();
		flowLayout_3.setAlignment(FlowLayout.RIGHT);
		panel_6.add(panel_7, BorderLayout.SOUTH);

		JButton btnFilterAnpassen = new JButton("Filter anpassen");
		btnFilterAnpassen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				tabbedPane.setSelectedIndex(tabbedPane.getSelectedIndex() - 1);
			}
		});

		JButton btnDateienAendern = new JButton("Dateien ändern");
		btnDateienAendern.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				tabbedPane.setSelectedIndex(0);
			}
		});
		panel_7.add(btnDateienAendern);
		panel_7.add(btnFilterAnpassen);

		JButton btnSpeichern = new JButton("Speichern...");
		btnSpeichern.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveFile();
			}
		});
		panel_7.add(btnSpeichern);

		btnDelAllRules.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (JOptionPane.YES_OPTION == JOptionPane
						.showConfirmDialog(
								null,
								"Sind Sie sicher, dass Sie alle Regeln löschen wollen?",
								"Alle Regeln löschen",
								JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.WARNING_MESSAGE)) {
					delAllRules();
				}
			}
		});

		hiLighter = textArea.getHighlighter();

	}

	protected void saveFile() {
		JFileChooser saver = new JFileChooser();
		if (saver.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				if (saver.getSelectedFile().exists()
						&& JOptionPane.showConfirmDialog(this,
								"Datei existiert bereits. Überschreiben?",
								"Datei existiert",
								JOptionPane.YES_NO_CANCEL_OPTION) == JOptionPane.YES_OPTION) {
					Files.write(saver.getSelectedFile().toPath(), textArea
							.getText().getBytes());
					JOptionPane.showMessageDialog(this,
							"Speichern erfolgreich.", "Speichern",
							JOptionPane.INFORMATION_MESSAGE);
				}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this,
						"Speichern fehlgeschlagen.", "Fehler",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	protected void applyFilters() throws IOException, BadLocationException {
		openFiles();
		tabbedPane.setSelectedIndex((tabbedPane.getSelectedIndex() + 1)
				% tabbedPane.getTabCount());
	}

	protected void addRule() {
		if (!ruleTextField.getText().trim().isEmpty()) {
			int before = 0, after = 0;
			try {
				before = Integer.parseInt(txtLinesBefore.getText().trim());
				after = Integer.parseInt(txtLinesAfter.getText().trim());
				rules.add(new Rule(ruleTextField.getText(), before, after));
				setTableData();
			} catch (NumberFormatException e) {
				return;
			}
		}
	}

	private void setTableData() {
		tableData = new String[rules.size()][columnNames.length];
		for (int i = 0; i < rules.size(); i++) {
			tableData[i] = new String[] { rules.get(i).getText(),
					String.valueOf(rules.get(i).getBefore()),
					String.valueOf(rules.get(i).getAfter()) };
		}
		tableModel.setDataVector(tableData, columnNames);
	}

	protected void delAllRules() {
		rules.clear();
		setTableData();
	}

	protected void delRules() {
		int selection = filterTable.getSelectedRow();
		rules.remove(selection);
		setTableData();
	}

	protected boolean chooseFiles() throws IOException, BadLocationException {
		JFileChooser fileChooser = new JFileChooser(currentPath);
		fileChooser.setMultiSelectionEnabled(true);
		if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			selectedFiles.clear();
			File[] files = fileChooser.getSelectedFiles();
			FileObject currFile;
			int invalidFileCount = 0;
			for (int i = 0; i < files.length; i++) {
				currFile = new FileObject(files[i]);
				if (currFile.init()) {
					selectedFiles.add(currFile);
				} else {
					invalidFileCount++;
				}
			}
			if (invalidFileCount > 0) {
				JOptionPane.showMessageDialog(null, invalidFileCount
						+ " Dateien konnten nicht gelesen werden.", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
			files = new File[selectedFiles.size()];
			for (int i = 0; i < selectedFiles.size(); i++) {
				files[i] = selectedFiles.get(i).getFile();
			}
			currentPath = fileChooser.getCurrentDirectory();
			if (files.length == 0) {
				JOptionPane
						.showMessageDialog(
								null,
								"Es muss mindestens eine Textdatei ausgewählt werden, um fortzufahren.",
								"Achtung", JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
			lblInformation.setText(files.length + " Datei"
					+ (files.length == 1 ? "" : "en") + " geöffnet.");
			DefaultMutableTreeNode root = new DefaultMutableTreeNode(files[0]
					.getParentFile().getAbsolutePath());
			treeModel = new DefaultTreeModel(root);
			for (int i = 0; i < files.length; i++) {
				treeModel.insertNodeInto(
						new DefaultMutableTreeNode(files[i].getName()), root,
						root.getChildCount());
			}
			tree.setModel(treeModel);
			textArea.setText("");
			return true;
		} else {
			JOptionPane
					.showMessageDialog(
							null,
							"Es muss mindestens eine Textdatei ausgewählt werden, um fortzufahren.",
							"Achtung", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
	}

	private void openFiles() throws IOException, BadLocationException {
		textArea.setText("");
		List<String> lines;
		textArea.append("Angewandte Filterregeln:" + System.lineSeparator());
		for(int z = 0; z < rules.size(); z++){
			textArea.append(rules.get(z).toString() + System.lineSeparator());
		}
		textArea.append(System.lineSeparator() + "Ergebnis der Filterung: " + System.lineSeparator() + System.lineSeparator());
		for (int i = 0; i < selectedFiles.size(); i++) {
			for(int k = 0; k < rules.size(); k++){
				selectedFiles.get(i).applyRule(rules.get(k));
			}
			lines = selectedFiles.get(i).getLines();
			textArea.append(selectedFiles.get(i).getFile().getName() + ":"
					+ System.lineSeparator());
			for (int j = 0; j < lines.size(); j++) {
				if (selectedFiles.get(i).getVisibilityAt(j)) {
					textArea.append(lines.get(j) + System.lineSeparator());
				}
			}
			textArea.append(System.lineSeparator() + System.lineSeparator());
		}
		addHighlights();
	}

	private void addHighlights() throws BadLocationException {
		String textToParse = textArea.getText();
		int index = 0;
		for (int i = 0; i < rules.size(); i++) {
			index = 0;
			int currStrLength = rules.get(i).getText().length();
			while ((index = textToParse.indexOf(rules.get(i).getText(), index)) != -1) {
				hiLighter.addHighlight(index, index + currStrLength,
						coloredPainter);
				index++;
			}
		}
	}

	public static void main(String[] args) {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
		}
		MainWindow m = new MainWindow();
		m.setVisible(true);
	}

}