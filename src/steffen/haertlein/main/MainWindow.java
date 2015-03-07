package steffen.haertlein.main;

/*
 *
 *   Copyright 2014 Steffen Haertlein
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

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
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import steffen.haertlein.file.FileObject;
import steffen.haertlein.file.Rule;

public class MainWindow extends JFrame {

	/**
	 * @author Steffen Haertlein
	 */
	private static final long serialVersionUID = 1L;
	private Vector<FileObject> selectedFiles = new Vector<FileObject>();
	private JTree tree;
	private DefaultTreeModel treeModel;
	private File currentPath;
	private JTextArea textArea;
	private JTextField ruleTextField;
	private JTable filterTable;
	private DefaultTableModel tableModel;
	private String[] columnNames;
	private String[][] tableData = new String[0][];
	private Vector<Rule> rules = new Vector<Rule>();
	private JTabbedPane tabbedPane;
	private Highlighter hiLighter;
	private Highlighter.HighlightPainter coloredPainter = new DefaultHighlighter.DefaultHighlightPainter(
			Color.RED);
	private JButton btnContinue;
	private JTextField txtLinesBefore;
	private JTextField txtLinesAfter;
	private JLabel lblInformation;
	private JPanel mainPanel;

	public MainWindow() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("FilterIT");
		initGUI();
		pack();
		setMinimumSize(getSize());
		setLocationRelativeTo(null);
	}

	private void initGUI() {
		initMainPanel();
		initFilesPanel();
		initFilterRulesPanel();
		initOutputPanel();
	}

	private void initOutputPanel() {
		JPanel outputPanel = new JPanel();
		tabbedPane.addTab("Output", outputPanel);
		tabbedPane.setEnabledAt(2, false);
		outputPanel.setLayout(new BorderLayout(0, 0));
		textArea = new JTextArea();
		textArea.setEditable(false);
		JScrollPane textPane = new JScrollPane(textArea);
		outputPanel.add(textPane);
		textPane.setMinimumSize(new Dimension(300, 200));

		JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		outputPanel.add(southPanel, BorderLayout.SOUTH);

		JButton btnAdaptFilter = new JButton("Back");
		btnAdaptFilter.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				changeToPreviousPage();
			}
		});

		JButton btnChooseFiles = new JButton("Choose Files");
		btnChooseFiles.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				tabbedPane.setSelectedIndex(0);
			}
		});
		southPanel.add(btnAdaptFilter);

		JButton btnSave = new JButton("Save...");
		btnSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveFile();
			}
		});
		southPanel.add(btnSave);

		hiLighter = textArea.getHighlighter();
	}

	private void initFilterRulesPanel() {
		JPanel filterRulesPanel = new JPanel(new BorderLayout(0, 0));
		tabbedPane.addTab("Filter Rules", filterRulesPanel);
		tabbedPane.setEnabledAt(1, false);

		JPanel northPanel = new JPanel();
		filterRulesPanel.add(northPanel, BorderLayout.NORTH);

		ruleTextField = new JTextField();
		ruleTextField.setColumns(15);

		JButton btnAddRule = new JButton("Add Rule");
		btnAddRule.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				addRule();
			}
		});
		northPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JLabel lblText = new JLabel("Text:");
		lblText.setLabelFor(ruleTextField);
		northPanel.add(lblText);
		northPanel.add(ruleTextField);

		JLabel lblLinesBefore = new JLabel("Lines before:");
		northPanel.add(lblLinesBefore);

		txtLinesBefore = new JTextField();
		lblLinesBefore.setLabelFor(txtLinesBefore);
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
		northPanel.add(txtLinesBefore);

		JLabel lblLinesAfter = new JLabel("Lines after:");
		northPanel.add(lblLinesAfter);

		txtLinesAfter = new JTextField();
		lblLinesAfter.setLabelFor(txtLinesAfter);
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
		northPanel.add(txtLinesAfter);
		northPanel.add(btnAddRule);

		JPanel centerPanel = new JPanel(new BorderLayout(0, 0));
		filterRulesPanel.add(centerPanel, BorderLayout.CENTER);

		columnNames = new String[] { "Name", "Lines before", "Lines after" };
		tableModel = new DefaultTableModel(tableData, columnNames);
		filterTable = new JTable(tableModel);
		filterTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPane = new JScrollPane(filterTable);
		centerPanel.add(scrollPane);
		scrollPane.setMinimumSize(new Dimension(150, 200));

		JPanel centerSouthPanel = new JPanel();
		centerPanel.add(centerSouthPanel, BorderLayout.SOUTH);

		JButton btnDelRules = new JButton("Delete selected Rule(s)");
		centerSouthPanel.add(btnDelRules);
		btnDelRules.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				delRules();
			}
		});

		JButton btnDelAllRules = new JButton("Delete all Rules");
		centerSouthPanel.add(btnDelAllRules);
		btnDelAllRules.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(
						null, "Do you really want to delete all rules?",
						"Delete All Rules?", JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.WARNING_MESSAGE)) {
					delAllRules();
				}
			}
		});

		JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		filterRulesPanel.add(southPanel, BorderLayout.SOUTH);

		JButton btnBack = new JButton("Back");
		btnBack.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				changeToPreviousPage();
			}
		});
		southPanel.add(btnBack);

		JButton btnApplyFilter = new JButton("Continue");
		southPanel.add(btnApplyFilter);
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
	}

	private void initFilesPanel() {
		JPanel filesPanel = new JPanel(new BorderLayout(0, 0));
		tabbedPane.addTab("Files", filesPanel);
		tree = new JTree();
		tree.setModel(treeModel);

		JScrollPane treePane = new JScrollPane(tree);
		filesPanel.add(treePane, BorderLayout.CENTER);
		treePane.setMinimumSize(new Dimension(200, 200));

		JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		filesPanel.add(northPanel, BorderLayout.NORTH);

		JButton btnChooseFiles = new JButton("Choose Files...");
		btnChooseFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (chooseFiles()) {
						tabbedPane.setEnabledAt(1, true);
						btnContinue.setEnabled(true);
					}
				} catch (IOException | BadLocationException e) {
					e.printStackTrace();
				}
			}
		});
		northPanel.add(btnChooseFiles);

		JPanel southPanel = new JPanel(new BorderLayout(0, 0));
		filesPanel.add(southPanel, BorderLayout.SOUTH);

		JPanel westPanel = new JPanel();
		southPanel.add(westPanel, BorderLayout.WEST);

		lblInformation = new JLabel("");
		westPanel.add(lblInformation);

		JPanel eastPanel = new JPanel();
		southPanel.add(eastPanel, BorderLayout.EAST);

		btnContinue = new JButton("Continue");
		eastPanel.add(btnContinue);
		btnContinue.setEnabled(false);
		btnContinue.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				changeToNextPage();
			}
		});
	}

	private void initMainPanel() {
		mainPanel = new JPanel();
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(new BorderLayout(0, 0));

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		mainPanel.add(tabbedPane);
	}

	protected void saveFile() {
		JFileChooser saver = new JFileChooser();
		if (saver.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				if (saver.getSelectedFile().exists()
						&& JOptionPane.showConfirmDialog(this,
								"File already exists. Overwrite existing?",
								"Confirm override",
								JOptionPane.YES_NO_CANCEL_OPTION) == JOptionPane.YES_OPTION) {
					Files.write(saver.getSelectedFile().toPath(), textArea
							.getText().getBytes());
					JOptionPane.showMessageDialog(this, "Save successful.",
							"File saved", JOptionPane.INFORMATION_MESSAGE);
				}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "Error saving file.",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	protected void applyFilters() throws IOException, BadLocationException {
		openFiles();
		changeToNextPage();
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
						+ " files could not be read.", "Error",
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
								"Please choose at least one readable file to continue.",
								"Caution", JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
			lblInformation.setText(files.length + " file"
					+ (files.length == 1 ? "" : "s") + " opened.");
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
			JOptionPane.showMessageDialog(null,
					"Please choose at least one readable file to continue..",
					"Caution", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
	}

	private void openFiles() throws IOException, BadLocationException {
		textArea.setText("");
		List<String> lines;
		textArea.append("Filter Rules applied:" + System.lineSeparator());
		for (int z = 0; z < rules.size(); z++) {
			textArea.append(rules.get(z).toString() + System.lineSeparator());
		}
		textArea.append(System.lineSeparator() + "Filtering result: "
				+ System.lineSeparator() + System.lineSeparator());
		for (int i = 0; i < selectedFiles.size(); i++) {
			for (int k = 0; k < rules.size(); k++) {
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

	private void changeToNextPage() {
		if (tabbedPane.getSelectedIndex() < (tabbedPane.getTabCount() - 1)) {
			tabbedPane.setSelectedIndex(tabbedPane.getSelectedIndex() + 1);
		}
	}

	private void changeToPreviousPage() {
		if (tabbedPane.getSelectedIndex() > 0) {
			tabbedPane.setSelectedIndex(tabbedPane.getSelectedIndex() - 1);
		}
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
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MainWindow m = new MainWindow();
		m.setVisible(true);
	}

}
