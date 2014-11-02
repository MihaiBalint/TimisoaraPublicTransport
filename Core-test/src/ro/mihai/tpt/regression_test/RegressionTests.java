package ro.mihai.tpt.regression_test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ro.mihai.tpt.JavaCityLoader;
import ro.mihai.tpt.RATT;
import ro.mihai.tpt.model.City;
import ro.mihai.tpt.model.INamedEntity;
import ro.mihai.tpt.util.TestPrefs;
import ro.mihai.util.NullMonitor;
import junit.framework.Test;
import junit.framework.TestSuite;

public class RegressionTests {
	

	public static Test suite() throws IOException {
		TestPrefs prefs = new TestPrefs();
		City cExpected = JavaCityLoader.loadCachedCityOrDownloadAndCache(prefs);
		//City cActual = JavaCityLoader.loadCachedCityOrDownloadAndCache(prefs);
		City cActual = RATT.downloadCity(prefs, new NullMonitor());
		
		
		TestSuite suite = new TestSuite("Test for ro.mihai.tpt.test");
		RegressionStationTests.addTests(suite, cActual, cExpected);
		RegressionLineTests.addTests(suite, cActual, cExpected);
		
		return suite;
	}
	
	public static List<String> testMethods(Class<?> cls) {
		List<String> tests = new ArrayList<String>();
		
		for (Method each : cls.getDeclaredMethods()) {
			if(each.getName().startsWith("test")) 
				tests.add(each.getName());
		}
		
		return tests;
	} 

	public static <T extends INamedEntity> String diffEntities(Collection<T> expected, Collection<T> actual) {
		return diffEntities(expected, actual, new ArrayList<T>(), new ArrayList<T>());
	}

	public static <T extends INamedEntity> String diffEntities(Collection<T> expected, Collection<T> actual, 
			Collection<? extends INamedEntity> bl_expected, Collection<? extends INamedEntity> bl_actual) {
		
		TreeMap<String,T> expectedMap = new TreeMap<String, T>(), actualMap = new TreeMap<String, T>();
		
		for(T s : expected) 
			if (!BlackListed.isIdListed(s, bl_expected)) 
				expectedMap.put(s.getExtId(),s);
		for(T s : actual) 
			if (!BlackListed.isIdListed(s, bl_actual)) 
				actualMap.put(s.getExtId(),s);
		
		// are expected but not actual
		Collection<T> onlyExpected = new ArrayList<T>();
		for(Map.Entry<String, T> e : expectedMap.entrySet()) {
			if (actualMap.containsKey(e.getKey())) continue;
			onlyExpected.add(e.getValue());
		}
		
		// are actual but not expected
		Collection<T> onlyActual = new ArrayList<T>();
		// are actual and expected but modified name
		Collection<String> renamed = new ArrayList<String>();
		
		for(Map.Entry<String, T> a : actualMap.entrySet()) {
			if (expectedMap.containsKey(a.getKey())) {
				T e = expectedMap.get(a.getKey());
				if(!a.getValue().getName().trim().equals(e.getName().trim()))
					renamed.add(e.getExtId());
				continue;
			}
			onlyActual.add(a.getValue());
		}
		
		String summary = "";
		if(!onlyExpected.isEmpty()) {
			summary += "\nExpected but not found: ";
			for(T e:onlyExpected)
				summary += "["+e.getExtId()+" "+e.getName()+"] ";
		}
		if(!onlyActual.isEmpty()) {
			summary += "\nFound but not expected: ";
			for(T e:onlyActual)
				summary += "["+e.getExtId()+" "+e.getName()+"] ";
		}
		if(!renamed.isEmpty()) {
			summary += "\nFound renamed: ";
			for(String e:renamed)
				summary += "["+e+" "+expectedMap.get(e).getName()+" to "+actualMap.get(e).getName()+"] ";
		}
		return summary;
	}
}
