package test;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TestJsoup {
	public static void main(String args[]) throws ClientProtocolException, IOException{
		DefaultHttpClient client = new DefaultHttpClient();
	HttpResponse response;
	HttpGet get = new HttpGet("http://news.so.com/ns?q=李长春参观少林寺&ie=utf-8&tn=news&src=se6_newtab");
	response = client.execute(get);
	String html = EntityUtils.toString(response.getEntity(), "utf-8");
	Document doc = Jsoup.parse(html);
	
	Elements links = doc.getElementsByTag("a");
	for(Element link : links) {
		String linkHref = link.attr("href");
		String linkText = link.text();
		System.out.println(linkHref+" : "+linkText);
	}
}
}
