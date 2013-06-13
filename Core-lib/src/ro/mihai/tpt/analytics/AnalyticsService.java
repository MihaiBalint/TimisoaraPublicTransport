package ro.mihai.tpt.analytics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class AnalyticsService implements IAnalyticsService {
	private String remoteAddress;
	private int remotePort;
	
	public AnalyticsService(String remoteAddress, int remotePort) {
		this.remoteAddress = remoteAddress;
		this.remotePort = remotePort;
	}
	
	private String getHeaders(String service) {
		return "POST /tpt-analytics/"+service+" HTTP/1.1\r\n"+
				"Host: "+remoteAddress+":"+remotePort+"\r\n"+
				"User-Agent: App\r\n"+
				"Accept: text/html\r\n"+
				"Referer: App\r\n"+
				"Connection: close\r\n";
	}
	
	private String getContent(String localAddress, String data) {
		return localAddress+"\r\n"+data+"\r\n";
	}
	
	public String postData(String service, String data) throws IOException {
		Socket sock = new Socket(remoteAddress, remotePort);
		OutputStreamWriter os = new OutputStreamWriter(sock.getOutputStream());
		InputStream is = sock.getInputStream();
		String local = sock.getLocalAddress().getHostAddress();
		os.write(getHeaders(service));
		os.write("\r\n");
		os.write(getContent(local, data));
		os.flush();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		while (br.readLine().length() > 0);
		String result = br.readLine();
		sock.close();
		return result;
	}
	
	public String generateDeviceId() throws IOException {
		return postData("generate_device_id", "");
	}
	
	public void postTimesBundle(String deviceId, String data) throws IOException {
		postData("post_times_bundle", deviceId+"\r\n"+data);
	}
	
}
