package steffen.haertlein.file;

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

public class Rule {
	/**
	 * @author Steffen Haertlein
	 * */

	private String text, name;
	private int before, after;

	public Rule(String _name, String _text, int _before, int _after) {
		name = _name;
		text = _text;
		before = _before;
		after = _after;
	}

	public String getName() {
		return name;
	}

	public String getText() {
		return text;
	}

	public int getBefore() {
		return before;
	}

	public int getAfter() {
		return after;
	}

	public String toString() {
		return "Text: '" + getText() + "'; " + getBefore() + " Zeilen davor, "
				+ getAfter() + " Zeilen danach";
	}
}
