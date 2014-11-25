package steffen.haertlein.file;

public class Rule {
	/**
	 * @author Steffen Haertlein
	 * */
	
	private String text;
	private int before, after;
	public Rule(String _text, int _before, int _after){
		text = _text;
		before = _before;
		after = _after;
	}
	
	public String getText(){
		return text;
	}
	
	public int getBefore(){
		return before;
	}
	
	public int getAfter(){
		return after;
	}
	
	public String toString(){
		return "Text: '" + getText() + "'; " + getBefore() + " Zeilen davor, " + getAfter() + " Zeilen danach";
	}
}
