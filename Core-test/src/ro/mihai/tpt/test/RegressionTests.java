package ro.mihai.tpt.test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import ro.mihai.tpt.JavaCityLoader;
import ro.mihai.tpt.RATT;
import ro.mihai.tpt.model.City;
import ro.mihai.util.NullMonitor;
import junit.framework.Test;
import junit.framework.TestSuite;

public class RegressionTests {

	public static Test suite() throws IOException {
		City cExpected = JavaCityLoader.loadCachedCityOrDownloadAndCache();
		//City cActual = JavaCityLoader.loadCachedCityOrDownloadAndCache();
		City cActual = RATT.downloadCity(new NullMonitor());
		
		
		TestSuite suite = new TestSuite("Test for ro.mihai.tpt.test");
		Regressions.addTests(suite, cActual, cExpected);
		LineRegressions.addTests(suite, cActual, cExpected);
		
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

}
