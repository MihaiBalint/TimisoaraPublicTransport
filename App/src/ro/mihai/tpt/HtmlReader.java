package ro.mihai.tpt;

import java.io.IOException;

public class HtmlReader {
	private FormattedTextReader rd;
	
	public HtmlReader(FormattedTextReader rd) {
		this.rd = rd;
		rd.setCaseSensitive(false);
	}
	
	public String[] readSelectOption() throws IOException {
		if (!rd.skipUntil("<option", true)) return null;

		if (!rd.skipAfter("value=\"", true)) return null;
		String value = rd.readUntil("\"");
		if(null==value) return null;

		if (!rd.skipAfter(">", true)) return null;
		String option = rd.readUntil("</option>");
		if(null==option) return null;
		
		return new String[]{value, option};
	}

}
