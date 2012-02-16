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
		// String fileName = "linestations-20111129-3.csv";
		String fileName = "Lines Stations and Junctions - Timisoara Public Transport - Denumiri-20120215.csv";
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
		Map<String, Junction> juMap = new HashMap<String, Junction>();
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
				if (newStation) {
					if(!nonEmpty(row[6])) 
						row[6]=nonEmpty(row[5])?row[5]:"";
					String juName = selectJunction("Please select the junction name for "+st.getNicestNamePossible()+" "+row[1]+"("+row[4]+")", st.getId(), help); 
					Junction j = juMap.get(juName);
					if (null==j) {
						j = new Junction(juName, null);
						juMap.put(juName, j);
					}
					st.setJunction(j);
					j.addStation(st);
				}
				if(nonEmpty(row[7]) || nonEmpty(row[8])) {
					if (newStation) 
						selectCoords("Please select coords for "+st.getNicestNamePossible()+" "+row[1]+"("+row[4]+")", st, help);
				}
			}
			rd.close();
		}
		
		// Junction merger
		Set<String> checked = new HashSet<String>();
		for(Station a:stMap.values()) 
			for(Station b:stMap.values()) {
				if (a==b) continue;
				String pair = a.getId()+"-"+b.getId();
				if (checked.contains(pair)) continue;
				
				checked.add(pair);
				checked.add(b.getId()+"-"+a.getId());
				if(nonEmpty(a.getLat()) && nonEmpty(a.getLng()) && nonEmpty(b.getLat()) && nonEmpty(b.getLng())) {
					int d = a.distanceTo(b);
					
					if (d>=225 && a.getJunctionName().equals(b.getJunctionName())) {
						// TODO in same junction, but too far
					} else if (d<125 && !a.getJunctionName().equals(b.getJunctionName())) {
						// TODO in different junctions, but too close
						selectJunctionMerger(a, b, d);
					}
				}
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
	
	private static void selectJunctionMerger(Station a, Station b, int dist) {
		String message = 
				"The following junctions will be merged because they have stations that are near each-other.\n";
		List<Station> stations = new ArrayList<Station>();
		stations.add(a); stations.add(b);
		message+="Distance "+dist+"m";
		for(Station s : stations)
			message += "\n    "+s.getId()+"-"+s.getName()+"-"+s.getNiceName();
		message+="\nSelect a name for the merged junction\n";				

		
		List<Junction> opts = new ArrayList<Junction>();
		for(Station s : stations)
			opts.add(s.getJunction());
		
		for(int i=0;i<opts.size();i++) {
			Junction junction = opts.get(i);
			String junctionStations="";
			if (junction.getStations().size()<10) 
				for(Station s:junction.getStations()) {
					junctionStations+="\n    "+s.getId()+"-"+s.getName()+"-"+s.getNiceName();
				}
			else
				junctionStations+="\n    "+junction.getStations().size()+" stations.";
			message += "\n " + i +". ---"+junction.getName()+"---"+junctionStations;
		}
		
		
		String out = javax.swing.JOptionPane.showInputDialog(message);
		String name;
		try {
			name = opts.get(Integer.parseInt(out)).getName().trim();
		} catch(NumberFormatException e) {
			name = out.trim();
		}
		Junction merged = new Junction(name, null);
		merged.getStations().addAll(stations);
		for(Junction j:opts)
			if (nonEmpty(j.getName()))
				merged.getStations().addAll(j.getStations());
		for(Station s:merged.getStations())
			s.setJunction(merged);
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
