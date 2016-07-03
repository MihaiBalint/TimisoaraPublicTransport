package ro.mihai.tpt.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.*;

import ro.mihai.tpt.analytics.AnalyticsService;

public class AnalyticsTest implements Runnable {
	private static final int PORT = 8888;
	private ServerSocket ss;
	private String sentAddress, postedDevice, postedData;
	
	@Before
	public synchronized void setUp() throws IOException, InterruptedException {
		new Thread(this).start();
		this.wait();
	}
	
	@After
	public void tearDown() throws IOException {
		ss.close();
	}
	
	private synchronized void createSocket() throws IOException {
		ss = new ServerSocket(PORT);
		this.notify();
	}
	
	private void unsafeRun() throws IOException {
		String headers = "HTTP/1.1 200 OK\r\n"+
				"Date: Thu, 13 Jun 2013 20:56:12 GMT\r\n"+
				"Server: Apache/2.2.22 (Ubuntu)\r\n"+
				"Content-Length: 123\r\n"+
				"Connection: close\r\n"+
				"Content-Type: text/html;charset=UTF-8\r\n"+
				"\r\n";
		createSocket();
		Socket c = ss.accept();
		BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
		OutputStreamWriter wr = new OutputStreamWriter(c.getOutputStream());
		String post = br.readLine();
		while (br.readLine().length() > 0);
		sentAddress = br.readLine();
		
		wr.write(headers);
		if (post.contains("generate_device_id")) {
			wr.write("1234567890");
		} else if (post.contains("post_times_bundle")) {
			postedDevice = br.readLine();
			postedData = br.readLine();
			wr.write("");
		} else {
			wr.write("<html>");
		}
		wr.flush();
		c.close();
	} 
	
	@Override
	public void run() {
		try {
			unsafeRun();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testPostData() throws IOException {
		AnalyticsService as = new AnalyticsService("http://localhost:"+PORT+"/tpt-analytics");
		assertEquals("<html>", as.postData("zuzu", ""));
		assertNotNull(sentAddress);
		assertTrue(sentAddress.startsWith("v02;")); // this actually only works on my laptop :)
		assertTrue(sentAddress.length() > 3);
	}
	
	@Test
	public void testGenerateDeviceId() throws IOException {
		AnalyticsService as = new AnalyticsService("http://localhost:"+PORT+"/tpt-analytics");
		assertEquals("1234567890", as.generateDeviceId());
	} 

	@Test
	public void testPostTimesData() throws IOException {
		AnalyticsService as = new AnalyticsService("http://localhost:"+PORT+"/tpt-analytics");
		as.postTimesBundle("1234", "bla");
		assertEquals("1234", postedDevice);
		assertEquals("bla", postedData);
	}
}
