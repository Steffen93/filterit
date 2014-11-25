package steffen.haertlein.main;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileObject {
	/**
	 * @author Steffen Haertlein
	 * */
	private File f;
	private List<String> lines = new ArrayList<String>();
	private List<Boolean> lineVisible = new ArrayList<Boolean>();

	public FileObject(File _f) {
		f = _f;
	}

	public boolean init() {
		try {
			lines = Files.readAllLines(f.toPath(), Charset.defaultCharset());
			resetLineVisibility();
			return true;
		} catch (IOException e) {
			return false;
		}
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
				to = (i + rule.getAfter() >= lines.size()) ? lines.size()-1 : i
						+ rule.getAfter();
				for (int j = from; j <= to; j++){
					lineVisible.set(j, true);
				}
			}
		}
	}

	public boolean getVisibilityAt(int index){
		if(index < 0 || index >= lineVisible.size()){
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
