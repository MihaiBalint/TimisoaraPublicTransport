package ro.mihai.tpt.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
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
		InputStream inp = new FileInputStream("linestations-20110928.csv");
		
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
				) continue; 
				
				if(row.length<13) {
					String d = "";
					for(String s:row) d += ", " + s;
					System.out.println("Row too short: ("+row.length+")"+d+".");
					continue;
				}
				
				data.add(row);
				
				Station st = stMap.get(row[2]);
				if(null==st) {
					st = new Station(row[2], row[3]);
					stMap.put(row[2], st);
					traceMap.put(row[2], "<"+row[1]+"/"+row[2]+">");
				}
				if(nonEmpty(row[4])) {
					if(invalid.contains(row[4].trim()))
						row[9] = "true";
					int pi = row[4].lastIndexOf('(');
					String stName = pi>=0 ? row[4].substring(0, pi).trim() : row[4].trim();
					String stDir = pi>=0 ? row[4].substring(pi).trim() : "";
					if(st.hasNiceName() && !st.getNiceName().trim().equals(stName.trim())) {
						if(dec1.contains(st.getNiceName().trim())) {
							st.setNiceName(stName.trim());
						} else {
							if(dec2.contains(stName.trim())) {
							} else 
								System.out.println("Nice     name conflict: <"+row[1]+"/"+row[2]+"> <"+stName+"> or "+traceMap.get(row[2])+" <"+st.getNiceName()+">");
						}
					} else if(!dec1.contains(row[4].trim()) && !dec2.contains(row[4].trim()))
						st.setNiceName(stName.trim());
					row[4] = stDir;
				}
				if(nonEmpty(row[5])) {
					if(st.hasShortName() && !st.getShortName().trim().equals(row[5].trim())) {
						System.out.println("Short    name conflict: <"+row[1]+"/"+row[2]+"> <"+row[5]+"> or "+traceMap.get(row[2])+" <"+st.getShortName()+">");
					} else 
						st.setShortName(row[5].trim());
				}
				if(nonEmpty(row[6])) {
					if(st.hasJunctionName() && !st.getJunctionName().trim().equals(row[6].trim())) {
						System.out.println("Junction name conflict: <"+row[1]+"/"+row[2]+"> <"+row[6]+"> or "+traceMap.get(row[2])+" <"+st.getJunctionName()+">");
					} else {
						if(st.getJunction()==null) 
							st.setJunction(new Junction(row[5].trim()));
						else 
							st.getJunction().setName(row[6].trim());
					}
				}
			}
			rd.close();
		}
		
		PrintStream csv = new PrintStream(new FileOutputStream("linestations.csv"));
		csv.println("LineID, LineName, StationID, RawStationName, FriendlyStationName, ShortStationName, JunctionName, Lat, Long, Invalid, Verified, Verification Date, Goodle Maps Link");
		
		for(String[] row:data) {
			Station st = stMap.get(row[2]);
			
			row[4] = st.getNiceName()!=null ? st.getNiceName()+" "+row[4] : row[4]; // friendly
			row[5] = st.getShortName()!=null ? st.getShortName() : ""; // short
			row[6] = st.getJunction()!=null ? st.getJunctionName() : ""; // junction
			if(row[10].trim().isEmpty()) {
				row[10] = "dup script"; // who
				row[11] = "15.07.11"; // when
			}

			csv.println(
					row[0]+						"," + 
					"\""+row[1]+"\"" +			"," +
					row[2]+						"," +
					"\""+row[3]+"\"" +			"," +
					"\""+row[4]+"\""+ 			"," + // FriendlyStationName
					"\""+row[5]+"\""+ 			"," + // ShortStationName
					"\""+row[6]+"\""+ 			"," + // JunctionName
					row[7]+ 					"," +
					row[8]+						"," +
					"\""+row[9]+"\""+ 			"," + // Invalid
					"\""+row[10]+"\""+ 			"," + // Verified
					"\""+row[11]+"\""+ 			"," + // Verif. date
					"\""+row[12]+"\""  		 		  // maps link
				);
			
			
		}
		csv.close();
	}
	
	private static boolean nonEmpty(String s) {
		return s!=null && !s.trim().isEmpty();
	}
}
