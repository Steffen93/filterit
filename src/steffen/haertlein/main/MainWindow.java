package steffen.haertlein.main;

/*
 *
 *   Copyright 2015 Steffen Haertlein
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
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import steffen.haertlein.file.FileObject;
import steffen.haertlein.file.Rule;

public class MainWindow extends JFrame implements PropertyChangeListener {

	/**
	 * @author Steffen Haertlein
	 */
	private final static String SOFTWARE_NAME = "FilterIT";
	private static final long serialVersionUID = 1L;
	private Vector<FileObject> selectedFiles = new Vector<FileObject>();
	private JTree tree;
	private File currentPath;
	private JTextArea textArea;
	private JTextField ruleTextField;
	private JTable filterTable;
	private DefaultTableModel tableModel;
	private String[] columnNames;
	private String[][] tableData = new String[0][];
	private Vector<Rule> rules = new Vector<Rule>();
	private JPanel contentPane;
	private Highlighter hiLighter;
	private Highlighter.HighlightPainter coloredPainter = new DefaultHighlighter.DefaultHighlightPainter(
			Color.LIGHT_GRAY);
	private JButton btnRemove;
	private JTextField txtLinesBefore;
	private JTextField txtLinesAfter;
	private JLabel lblInformation;
	private JPanel mainPanel;
	private JCheckBox chkbxRecursive;
	private JProgressBar progressBar;
	protected ProgressBarTask task;
	private int fileCount;
	private ArrayList<JPanel> pageList = new ArrayList<JPanel>();
	private JPanel outputPanel;
	private JPanel filesPanel;
	private JPanel filterRulesPanel;
	private JButton btnBack;
	private JButton btnContinue;

	public MainWindow() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("FilterIT");
		initGUI();
		pack();
		setMinimumSize(getSize());
		setLocationRelativeTo(null);
	}

	private void initGUI() {
		// TODO: maybe move panels into own classes
		initMainPanel();
		initFilesPanel();
		initFilterRulesPanel();
		initOutputPanel();
		changeToPage(filesPanel);
	}

	private void initOutputPanel() {
		outputPanel = new JPanel(new BorderLayout());
		outputPanel.setName("Output");
		pageList.add(outputPanel);
		outputPanel.setLayout(new BorderLayout(0, 0));
		textArea = new JTextArea();
		textArea.setEditable(false);
		JScrollPane textPane = new JScrollPane(textArea);
		outputPanel.add(textPane, BorderLayout.CENTER);
		textPane.setMinimumSize(new Dimension(300, 200));

		JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton btnSave = new JButton("Save...");
		btnSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveFile();
			}
		});
		southPanel.add(btnSave);
		outputPanel.add(southPanel, BorderLayout.SOUTH);
		hiLighter = textArea.getHighlighter();
	}

	private void initFilterRulesPanel() {
		filterRulesPanel = new JPanel(new BorderLayout(0, 0));
		filterRulesPanel.setName("Filter Rules");
		pageList.add(filterRulesPanel);

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

		columnNames = new String[] { getBoldString("Name"), getBoldString("Lines before"), getBoldString("Lines after") };
		tableModel = new DefaultTableModel(tableData, columnNames);
		filterTable = new JTable(tableModel);
		filterTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPane = new JScrollPane(filterTable);
		centerPanel.add(scrollPane);
		scrollPane.setMinimumSize(new Dimension(150, 200));

		JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		centerPanel.add(southPanel, BorderLayout.SOUTH);

		JButton btnDelRules = new JButton("Delete selected Rule(s)");
		southPanel.add(btnDelRules);
		btnDelRules.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				delRules();
			}
		});

		JButton btnDelAllRules = new JButton("Delete all Rules");
		southPanel.add(btnDelAllRules);
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
	}

	private String getBoldString(String string) {
		return "<html><b>" + string + "</b></html>";
	}

	private void initFilesPanel() {
		filesPanel = new JPanel(new BorderLayout(0, 0));
		filesPanel.setName("Manage Files");
		pageList.add(filesPanel);
		tree = new JTree();
		FileTreeNode root = new FileTreeNode("Files");
		tree.setModel(new DefaultTreeModel(root));
		tree.setRootVisible(true);
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				setRemoveButtonStatus(isTreeHavingNodesButRoot()
						&& !isTreeRootSelected());
			}
		});

		JScrollPane treePane = new JScrollPane(tree);
		filesPanel.add(treePane, BorderLayout.CENTER);
		treePane.setMinimumSize(new Dimension(200, 200));

		JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		filesPanel.add(northPanel, BorderLayout.NORTH);

		JButton btnAddFiles = new JButton(
				"<html><font color=blue size=+1><b>+</b></font></html>");
		btnAddFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				longProcessStarting("Adding files...");
				Runnable r = new Runnable() {
					public void run() {
						// SwingUtilities.invokeLater(new Runnable() {
						// @Override
						// public void run() {
						// }
						// });
						try {
							addFiles();
						} catch (Exception e) {
							longProcessFinished("Finished adding files.");
						}

						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								longProcessFinished("Finished adding files.");
							}
						});
					}
				};
				Thread t = new Thread(r);
				t.start();
			}
		});
		northPanel.add(btnAddFiles);

		chkbxRecursive = new JCheckBox("Recursive");
		chkbxRecursive.setSelected(true);
		northPanel.add(chkbxRecursive);

		btnRemove = new JButton(
				"<html><font color=red size=+1><b>X</b></font></html>");
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				longProcessStarting("Removing Selection...");
				removeSelected();
				longProcessFinished("Finished removing selection1.");
				showFileStatus();
			}
		});
		btnRemove.setEnabled(false);
		northPanel.add(btnRemove);
	}

	private void longProcessStarting(String informationText) {
		MainWindow.this.setCursor(Cursor
				.getPredefinedCursor(Cursor.WAIT_CURSOR));
		lblInformation.setText(informationText);
		progressBar.setIndeterminate(true);
		progressBar.setVisible(true);
	}

	private void longProcessFinished(String informationText) {
		MainWindow.this.setCursor(Cursor
				.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		lblInformation.setText(informationText);
		progressBar.setIndeterminate(false);
		showFileStatus();
	}

	protected void removeSelected() {
		TreePath currentSelection = tree.getSelectionPath();
		if (currentSelection != null) {
			DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) (currentSelection
					.getLastPathComponent());
			DefaultMutableTreeNode parent = (DefaultMutableTreeNode) (currentNode
					.getParent());
			while (currentNode != null
					&& !currentNode.equals(tree.getModel().getRoot())) {
				if (parent != null) {
					((DefaultTreeModel) tree.getModel())
							.removeNodeFromParent(currentNode);
				} else {
					currentSelection = tree.getSelectionPath();
					if (currentSelection == null) {
						break;
					}
				}
				currentNode = (DefaultMutableTreeNode) (currentSelection
						.getLastPathComponent());
				parent = (DefaultMutableTreeNode) (currentNode.getParent());
			}
			setFileCount(getNumberOfFiles(tree.getModel()));
			return;
		}
		setFileCount(getNumberOfFiles(tree.getModel()));
	}

	protected void setRemoveButtonStatus(boolean newVal) {
		btnRemove.setEnabled(newVal);
	}

	protected boolean isTreeHavingNodesButRoot() {
		if (tree != null
				&& ((DefaultMutableTreeNode) tree.getModel().getRoot())
						.getChildCount() > 0 && tree.getSelectionPath() != null) {
			return true;
		}
		return false;
	}

	protected boolean isTreeRootSelected() {
		return !(tree.getSelectionPath().getLastPathComponent() != tree
				.getModel().getRoot());
	}

	private void initMainPanel() {
		mainPanel = new JPanel();
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(new BorderLayout(0, 0));
		contentPane = new JPanel(new BorderLayout(0, 0));
		contentPane.setPreferredSize(new Dimension(800, 600));
		mainPanel.add(contentPane, BorderLayout.CENTER);

		lblInformation = new JLabel("Welcome.");

		progressBar = new JProgressBar(0, 100);
		progressBar.setStringPainted(true);
		progressBar.setString("");

		btnBack = new JButton("< Back");
		btnBack.setEnabled(false);
		btnBack.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				changeToPreviousPage();
			}
		});

		btnContinue = new JButton("Continue >");
		btnContinue.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeToNextPage();
			}
		});

		JPanel southPanel = new JPanel(new BorderLayout());
		JPanel southEastPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JPanel southWestPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		southWestPanel.add(lblInformation);
		southWestPanel.add(progressBar);

		southEastPanel.add(btnBack);
		southEastPanel.add(btnContinue);

		mainPanel.add(southPanel, BorderLayout.SOUTH);
		southPanel.add(southEastPanel, BorderLayout.EAST);
		southPanel.add(southWestPanel, BorderLayout.WEST);
	}

	protected void saveFile() {
		JFileChooser fileSaveDialog = new JFileChooser();
		if (fileSaveDialog.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				if (fileSaveDialog.getSelectedFile().exists()
						&& JOptionPane.showConfirmDialog(this,
								"File already exists. Overwrite existing?",
								"Confirm override", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					Files.write(fileSaveDialog.getSelectedFile().toPath(),
							textArea.getText().getBytes());
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

	protected boolean addFiles() throws IOException, BadLocationException {
		JFileChooser fileChooser = new JFileChooser(currentPath);
		fileChooser.setMultiSelectionEnabled(true);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.setApproveButtonText("Add");
		if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			selectedFiles.clear();
			File[] files = fileChooser.getSelectedFiles();
			if (files.length <= 0) {
				return false;
			}
			/*
			 * FileObject currFile; int invalidFileCount = 0; for (int i = 0; i
			 * < files.length; i++) { if (!files[i].isFile()) { continue; }
			 * currFile = new FileObject(files[i]); if (currFile.init()) {
			 * selectedFiles.add(currFile); } else { invalidFileCount++; } } if
			 * (invalidFileCount > 0) { JOptionPane.showMessageDialog(null,
			 * invalidFileCount + " files could not be read.", "Error",
			 * JOptionPane.ERROR_MESSAGE); } files = new
			 * File[selectedFiles.size()]; for (int i = 0; i <
			 * selectedFiles.size(); i++) { files[i] =
			 * selectedFiles.get(i).getFile(); } currentPath =
			 * fileChooser.getCurrentDirectory();
			 */
			addFilesToTreeModel((FileTreeNode) tree.getModel().getRoot(),
					files, chkbxRecursive.isSelected());
			if (!tree.isExpanded(0)) {
				tree.expandRow(0);
			}
			textArea.setText("");
			return true;
		}
		return false;
	}

	private void addFilesToTreeModel(FileTreeNode root, File[] files,
			boolean recursive) {
		DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
		for (File f : files) {
			if (f.isFile() && f.canRead()) {
				FileTreeNode child = new FileTreeNode(new FileObject(f), false);
				setFileCount(getFileCount() + 1);
				treeModel.insertNodeInto(child, root, root.getChildCount());
			} else if (f.isDirectory() && recursive) {
				FileTreeNode folder = searchNodeByFileName(f.getName());
				if (folder == null) {
					folder = new FileTreeNode(f);
					treeModel
							.insertNodeInto(folder, root, root.getChildCount());
				}
				addFilesToTreeModel(folder, f.listFiles(), recursive);
			}
			else if(f.isFile() && !f.canRead()){
				System.out.println("File not readable.");
			}
		}
	}

	public FileTreeNode searchNodeByFileName(String nodeStr) {
		FileTreeNode node = null;
		Enumeration<?> e = ((FileTreeNode) tree.getModel().getRoot())
				.breadthFirstEnumeration();
		while (e.hasMoreElements()) {
			node = (FileTreeNode) e.nextElement();
			if (nodeStr.equals(node.getUserObject())) {
				return node;
			}
		}
		return null;
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
		if (contentPane.getComponentCount() < 1) {
			return;
		}
		int pageIndex = getPageIndex(contentPane.getComponent(0));
		if (pageIndex < pageList.size() - 1) {
			changeToPage(pageList.get(pageIndex + 1));
			btnBack.setEnabled(true);
			if (pageIndex + 1 == pageList.size() - 1) {
				btnContinue.setEnabled(false);
			}
		}
	}

	private int getPageIndex(Component page) {
		for (int i = 0; i < pageList.size(); i++) {
			if (page.equals(pageList.get(i))) {
				return i;
			}
		}
		return 0;
	}

	/**
	 * Shows the specified page on the content pane and repaints the panel.
	 * Therefore the component 0 (the current page) is removed and replaced by
	 * the given one.
	 * 
	 * @param page
	 *            The page to be shown
	 * */
	private void changeToPage(Component page) {
		if (contentPane.getComponentCount() > 0) {
			contentPane.remove(0);
		}
		contentPane.add(page);
		if (!page.getName().isEmpty()) {
			this.setTitle(SOFTWARE_NAME + " - " + page.getName());
		}
		contentPane.revalidate();
		contentPane.repaint();
	}

	private void changeToPreviousPage() {
		if (contentPane.getComponentCount() < 1) {
			return;
		}
		int pageIndex = getPageIndex(contentPane.getComponent(0));
		if (pageIndex > 0) {
			changeToPage(pageList.get(pageIndex - 1));
			btnContinue.setEnabled(true);
			if (pageIndex - 1 == 0) {
				btnBack.setEnabled(false);
			}
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
			e.printStackTrace();
		}
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MainWindow m = new MainWindow();
				m.setVisible(true);
			}
		});
	}

	class ProgressBarTask extends SwingWorker<Void, Void> {
		private int max = Integer.MAX_VALUE;

		public synchronized void setMax(int m) {
			max = m;
		}

		@Override
		protected Void doInBackground() throws Exception {
			double progress = 0.0;
			progressBar.setValue(0);
			while (progress < 100) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException iex) {
				}
				progress = 100.0 * getNumberOfNodes(tree.getModel()) / this.max;
				// System.out.println("progress = " + progress);
				progressBar.setValue(Math.min((int) (progress), 100));
			}
			return null;
		}

		@Override
		protected void done() {
			System.out.println("max: " + max);
			// setProgress(0);
		}
	}

	public synchronized int getNumberOfNodes(TreeModel model) {
		return getNumberOfNodes(model, model.getRoot());
	}

	private synchronized int getNumberOfNodes(TreeModel model, Object node) {
		int count = 1;
		int nChildren = model.getChildCount(node);
		for (int i = 0; i < nChildren; i++) {
			count += getNumberOfNodes(model, model.getChild(node, i));
		}
		return count;
	}

	public synchronized int getNumberOfFiles(TreeModel model) {
		return getNumberOfFiles(model, model.getRoot());
	}

	private synchronized int getNumberOfFiles(TreeModel model, Object node) {
		int count = 0;
		int nChildren = model.getChildCount(node);
		for (int i = 0; i < nChildren; i++) {
			if (((FileTreeNode) model.getChild(node, i)).getUserObject() instanceof FileObject) {
				count++;
			}
			count += getNumberOfFiles(model, model.getChild(node, i));
		}
		return count;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
			// int progress = (Integer) evt.getNewValue();
			// progressBar.setValue(progress);
			// System.out.println("Progress: " + progress);
		}
	}

	private void showFileStatus() {
		lblInformation.setText(String.valueOf(fileCount) + " Files.");
	}

	private void setFileCount(int newVal) {
		fileCount = newVal;
	}

	private int getFileCount() {
		return fileCount;
	}
}
