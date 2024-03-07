package propertyCost;

import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class proxy {
	public static void main(String[] args) {
	

		HttpClient http_client = HttpClient.newBuilder().proxy(ProxySelector.of(new InetSocketAddress("proxyhost", 80)))
				.build();

		HttpRequest http_request = HttpRequest.newBuilder().uri(URI.create("https://www.github.com")).build();

		
		try {
			HttpResponse<String> response = http_client.send(http_request, HttpResponse.BodyHandlers.ofString());
			System.out.println("Status Code : " + response.statusCode());
			System.out.println("\n Body: " + response.body());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
