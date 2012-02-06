package ro.mihai.tpt.regression_test;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ro.mihai.tpt.model.INamedEntity;

public class BlackListed implements INamedEntity {
	private String id, name;
	
	public BlackListed(String id, String name) {
		this.id = id;
		this.name = name;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}
	
	public static <T extends INamedEntity> boolean isIdListed(INamedEntity entity, Collection<T> list) {
		for(T b : list)
			if (entity.getId().equals(b.getId())) return true;
		return false;
	}

	public static <T extends INamedEntity> boolean isNameListed(INamedEntity entity, Collection<T> list) {
		for(T b : list)
			if (entity.getName().equals(b.getName())) return true;
		return false;
	}

	public static BlackListed bl(String id, String name) {
		return new BlackListed(id, name);
	}

	public static Collection<BlackListed> blEmpty = Arrays.asList(new BlackListed[]{});

	public static Collection<BlackListed> blExpectedLines = Arrays.asList(new BlackListed[]{
	});
	public static Collection<BlackListed> blActualLines = Arrays.asList(new BlackListed[]{
		bl("2086", "P3"), bl("2106", "P4-d"), bl("2126", "P4-a"), bl("2146", "P1-d"), 
		bl("2166", "P1-a"), bl("2186", "P2-d"), bl("2207", "P2-a")
	});
	
	private static Map<String, Collection<BlackListed>> blExpectedLineStations;
	private static Map<String, Collection<BlackListed>> blActualLineStations;
	
	
	public static Collection<BlackListed> blExpectedLineStations(String lineName) {
		if(blExpectedLineStations==null) {
			blExpectedLineStations = new HashMap<String, Collection<BlackListed>>();
			blExpectedLineStations.put("BMW Z5", Arrays.asList(new BlackListed[]{
				bl("11111111", "Coco Jambo") 
			}));
			blExpectedLineStations.put("Porche 911", Arrays.asList(new BlackListed[]{
				bl("I do hope you realise", "that this is only an example") 
			}));
		}
		Collection<BlackListed> r = blExpectedLineStations.get(lineName);
		return r!=null ? r : blEmpty;
	}

	public static Collection<BlackListed> blActualLineStations(String lineName) {
		if(blActualLineStations==null) {
			blActualLineStations = new HashMap<String, Collection<BlackListed>>();
			blActualLineStations.put("33", Arrays.asList(new BlackListed[]{
				bl("3200", "Ab_Catedrala 2") // does not have time estimates
			}));
			blActualLineStations.put("E7", Arrays.asList(new BlackListed[]{
				bl("6200", "T.Grozavescu p") // does not have time estimates
			}));
			blActualLineStations.put("Tb11", Arrays.asList(new BlackListed[]{
				bl("2808", "ILSA 1tb.") // Optional station, removed from app by Cristi
			}));
			blActualLineStations.put("28", Arrays.asList(new BlackListed[]{
				bl("2664", "Titeica") // suprapunere cu alta statie - Cristi
			}));
		}
		Collection<BlackListed> r = blActualLineStations.get(lineName);
		return r!=null ? r : blEmpty;
	}
}
