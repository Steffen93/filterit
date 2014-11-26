package steffen.haertlein.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import steffen.haertlein.main.Rule;

public class FileObject {
	private File f;
	private List<String> lines = new ArrayList<String>();
	private List<Boolean> lineVisible = new ArrayList<Boolean>();

	public FileObject(File _f) {
		f = _f;
	}

	public boolean init() {
		try {
			if (getFileEnding().equals("docx")) {
				readWordDocument();
			} else if (getFileEnding().equals("xlsx")) {
				readExcelDocument();
			} else if (getFileEnding().equals("pdf")) {
				readPDFDocument();
			} else {
				lines = Files
						.readAllLines(f.toPath(), Charset.defaultCharset());
			}
			resetLineVisibility();
			return true;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Konnte Datei nicht öffnen",
					"Fehler", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

	private void readPDFDocument() {
		try {
			FileInputStream fs = new FileInputStream(f);
			String text = "";
			PDFParser parser = new PDFParser(fs);
			parser.parse();
			COSDocument cosDoc = parser.getDocument();
			PDFTextStripper pdfStripper = new PDFTextStripper();
			PDDocument pdDoc = new PDDocument(cosDoc);
			text = pdfStripper.getText(pdDoc);
			String[] docxLines = text.split(System.lineSeparator());
			for (String line : docxLines) {
				lines.add(line);
			}
			fs.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Fehler in readPDFDocument",
					"Fehler", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private void readExcelDocument() {
		try {
			FileInputStream fs = new FileInputStream(f);
			XSSFWorkbook wb = new XSSFWorkbook(fs);
			XSSFSheet sh;
			String text = "";
			for (int i = 0; i < wb.getNumberOfSheets(); i++) {
				sh = wb.getSheetAt(i);
				for (int j = sh.getFirstRowNum(); j <= sh.getLastRowNum(); j++) {
					XSSFRow currRow = sh.getRow(j);
					if (currRow == null || currRow.getFirstCellNum() == -1) {
						continue;
					} else {
						for (int k = currRow.getFirstCellNum(); k < currRow
								.getLastCellNum(); k++) {
							if (currRow.getCell(k, Row.RETURN_BLANK_AS_NULL) == null) {
								continue;
							} else {
								text += currRow.getCell(k) + "; ";
							}
						}
						text += System.lineSeparator();
					}
				}
			}
			fs.close();
			String[] xlsxLines = text.split(System.lineSeparator());
			for (String line : xlsxLines) {
				lines.add(line);
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Fehler in readExcelDocument",
					"Fehler", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private void readWordDocument() {
		try {
			FileInputStream fs = new FileInputStream(f);
			XWPFDocument document;
			document = new XWPFDocument(OPCPackage.open(fs));
			XWPFWordExtractor docxReader = new XWPFWordExtractor(document);
			String text = docxReader.getText();
			docxReader.close();
			String[] docxLines = text.split(System.lineSeparator());
			for (String line : docxLines) {
				lines.add(line);
			}
			fs.close();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null,
					"InvalidFormatException in readWordDocument", "Fehler",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null,
					"FileNotFoundException in readWordDocument", "Fehler",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null,
					"IOException in readWordDocument", "Fehler",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private String getFileEnding() {
		int index = f.getName().lastIndexOf(".");
		System.out.println(f.getName());
		return f.getName().substring(index + 1);
	}

	private void resetLineVisibility() {
		lineVisible.clear();
		for (int i = 0; i < lines.size(); i++) {
			lineVisible.add(false);
		}
	}

	public void applyRule(Rule rule) {
		resetLineVisibility();
		String text = rule.getText();
		int from = 0, to = 0;
		for (int i = 0; i < lines.size(); i++) {
			from = to = 0;
			if (lines.get(i).contains(text)) {
				from = (i - rule.getBefore() < 0) ? 0 : i - rule.getBefore();
				to = (i + rule.getAfter() >= lines.size()) ? lines.size() - 1
						: i + rule.getAfter();
				for (int j = from; j <= to; j++) {
					lineVisible.set(j, true);
				}
			}
		}
	}

	public boolean getVisibilityAt(int index) {
		if (index < 0 || index >= lineVisible.size()) {
			return false;
		}
		return lineVisible.get(index);
	}

	public List<String> getLines() {
		return lines;
	}

	public File getFile() {
		return f;
	}
}
