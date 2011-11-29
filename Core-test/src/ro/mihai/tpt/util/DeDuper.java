package ro.mihai.tpt.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ro.mihai.tpt.model.Junction;
import ro.mihai.tpt.model.Station;

import au.com.bytecode.opencsv.CSVReader;

public class DeDuper {

	public static void main(String[] args) throws Exception {
		//String csvURL = "https://spreadsheets.google.com/spreadsheet/pub?hl=en_US&hl=en_US&key=0AtCtEmR70abcdG5ZaWRpRnI5dTFlUXN3U3Y0c0N2Wmc&single=true&gid=0&output=csv";
		// InputStream inp = new URL(csvURL).openStream();
		String fileName = "linestations-20111129-2.csv";
		InputStream inp = new FileInputStream(fileName);
		Helper help = new Helper(fileName);
		
		Set<String> invalid = new HashSet<String>();
		invalid.add("Linie inexistenta");
		invalid.add("Linie desfiintata");
		
		Set<String> dec1 = new HashSet<String>();
		dec1.add("Traseu periodic");
		dec1.add("Linie desfiintata");
		dec1.add("Linie inexistenta");
		dec1.add("Dublura");

		Set<String> dec2 = new HashSet<String>();
		dec2.add("Linie inexistenta");
		dec2.add("Linie desfiintata");
		dec2.add("Dublura");
		dec2.add("Linie personal");
		
		Map<String, Station> stMap = new HashMap<String, Station>();
		Map<String, String> traceMap = new HashMap<String, String>();
		List<String[]> data = new ArrayList<String[]>();
		
		{ // read the data
			CSVReader rd = new CSVReader(new InputStreamReader(inp));
			String[] row;
			while(null!=(row=rd.readNext())) {
				if( (row.length==1 && row[0].trim().isEmpty()) // ignore empty rows 
					|| (row.length>0 && row[0].equalsIgnoreCase("LineID")) // ignore intermediate headers	
					|| (row.length>9 && row[9].equalsIgnoreCase("true")) // ignore invalid rows 
				) {
					data.add(row);
					continue;
				} 
				
				if(row.length<13) {
					String d = "";
					for(String s:row) d += ", " + s;
					System.out.println("Row too short: ("+row.length+")"+d+".");
					continue;
				}
				
				data.add(row);
				
				Station st = stMap.get(row[2]);
				boolean newStation = false;
				if(null==st) {
					st = new Station(row[2], row[3]);
					stMap.put(row[2], st);
					traceMap.put(row[2], "<"+row[1]+"/"+row[2]+">");
					newStation = true;
				}
				if(nonEmpty(row[4])) {
					if(invalid.contains(row[4].trim()))
						row[9] = "true";
					int pi = row[4].lastIndexOf('(');
					String stName = pi>=0 ? row[4].substring(0, pi).trim() : row[4].trim();
					String stDir = pi>=0 ? row[4].substring(pi).trim() : "";
					row[4] = stDir;
					if (newStation) 
						st.setNiceName(selectName("Please select the nicest name for "+row[3]+" "+row[1]+row[4], st.getId(), help.getNiceName(st.getId())));
				}
				if(nonEmpty(row[5])) {
					if (newStation) 
						st.setShortName(selectName("Please select the shortest name for "+st.getNicestNamePossible()+" "+row[1]+"("+row[4]+")", st.getId(), help.getShort(st.getId())));
				}
				if(nonEmpty(row[6])) {
					if (newStation) 
						st.setJunction(new Junction(selectJunction("Please select the junction name for "+st.getNicestNamePossible()+" "+row[1]+"("+row[4]+")", st.getId(), help)));
				}
				if(nonEmpty(row[7]) || nonEmpty(row[8])) {
					if (newStation) 
						selectCoords("Please select coords for "+st.getNicestNamePossible()+" "+row[1]+"("+row[4]+")", st, help);
				}
			}
			rd.close();
		}
		
		PrintStream csv = new PrintStream(new FileOutputStream("linestations.csv"));
		//csv.println("LineID, LineName, StationID, RawStationName, FriendlyStationName, ShortStationName, JunctionName, Lat, Long, Invalid, Verified, Verification Date, Goodle Maps Link");
		
		for(String[] row:data) {
			if( (row.length==1 && row[0].trim().isEmpty()) // ignore empty rows 
					|| (row.length>0 && row[0].equalsIgnoreCase("LineID")) // ignore intermediate headers	
					|| (row.length>9 && row[9].equalsIgnoreCase("true")) // ignore invalid rows 
				) {
				String r = "";
				for(String c : row)
					r+="\""+c+"\",";
				if (row.length>0)
					r = r.substring(0, r.length()-1);
				csv.println(r);
			} else {
				Station st = stMap.get(row[2]);
				
				row[4] = st.getNiceName()!=null ? st.getNiceName()+" "+row[4] : row[4]; // friendly
				row[5] = st.getShortName()!=null ? st.getShortName() : ""; // short
				row[6] = st.getJunction()!=null ? st.getJunctionName() : ""; // junction
				if(row[10].trim().isEmpty()) {
					row[10] = "dup script"; // who
					row[11] = "29.11.11"; // when
				}
				if(nonEmpty(row[7]) || nonEmpty(row[8])) {
					row[7] = st.getLat();
					row[8] = st.getLng();
					row[12] = "http://maps.google.com/maps?q="+st.getNicestNamePossible().trim().replaceAll(" ", "%20")+"@"+st.getLat()+","+st.getLng();
				} else if(!nonEmpty(row[12])) 
					row[12] = "x";
				
				csv.println(
					row[0]+						"," + 
					"\""+row[1]+"\"" +			"," +
					row[2]+						"," +
					"\""+row[3]+"\"" +			"," +
					"\""+row[4]+"\""+ 			"," + // FriendlyStationName
					"\""+row[5]+"\""+ 			"," + // ShortStationName
					"\""+row[6]+"\""+ 			"," + // JunctionName
					"\""+row[7]+"\""+ 			"," + // JunctionName
					"\""+row[8]+"\""+ 			"," + // JunctionName
					"\""+row[9]+"\""+ 			"," + // Invalid
					"\""+row[10]+"\""+ 			"," + // Verified
					"\""+row[11]+"\""+ 			"," + // Verif. date
					"\""+row[12]+"\""  		 		  // maps link
				);
			}
		}
		csv.close();
	}
	
	private static String selectName(String msg, String id, Collection<String> options) {
		List<String> opts = new ArrayList<String>(options);
		if (opts.size()==1)
			return opts.get(0).trim();
		
		String message = msg;
		for(int i=0;i<opts.size();i++)
			message += "\n " + i +". ---"+opts.get(i)+"---";
		
		String out = javax.swing.JOptionPane.showInputDialog(message);
		try {
			return opts.get(Integer.parseInt(out)).trim();
		} catch(NumberFormatException e) {
			return out.trim();
		}
	}

	private static String selectJunction(String msg, String id, Helper help) {
		List<String> opts = new ArrayList<String>(help.getJunction(id));
		if (opts.size()==1)
			return opts.get(0).trim();
		
		String message = msg+" #"+id;
		for(int i=0;i<opts.size();i++) {
			String junction = opts.get(i);
			String junctionStations="";
			Collection<String> junctionStationsList = help.getJunctionStations(junction);
			if (junctionStationsList.size()<10) 
				for(String s:junctionStationsList)
					junctionStations+="\n    "+s;
			else
				junctionStations+="\n    "+junctionStationsList.size()+" stations.";
			message += "\n " + i +". ---"+junction+"---"+junctionStations;
		}
		
		String out = javax.swing.JOptionPane.showInputDialog(message);
		try {
			return opts.get(Integer.parseInt(out)).trim();
		} catch(NumberFormatException e) {
			return out.trim();
		}
	}
	
	private static void selectCoords(String msg, Station st, Helper help) {
		List<Helper.Coords> opts = new ArrayList<Helper.Coords>(help.getCoords(st.getId()));
		Helper.Coords c;
		if (opts.size()==1) {
			c = opts.get(0);
			st.setCoords(c.getLat().trim(), c.getLng().trim());
			return;
		}
		Set<Helper.Coords> xml = help.getXMLCoords(st.getId());
		
		String message = msg+" #"+st.getId();
		for(int i=0;i<opts.size();i++) {
			c = opts.get(i);
			message += "\n "+i+". ---"+c+"--- "+xml.contains(c);
		}
		
		message += "\n----------X--M--L-----------------";
		for(Helper.Coords xc : xml) 
			message += "\n    ---"+xc+"---";
		String out = javax.swing.JOptionPane.showInputDialog(message);
		
		c = opts.get(Integer.parseInt(out));
		st.setCoords(c.getLat(), c.getLng());
	}
	
	
	private static boolean nonEmpty(String s) {
		return s!=null && !s.trim().isEmpty();
	}
}
