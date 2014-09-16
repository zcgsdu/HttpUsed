package test2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Savepoint;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SpiderV {
	public static void main(String[] args) {
		String secondUrl = "http://eeee99.com/VOD01/list_";
		String[] ahref = new String[450];
		int count = 0;
		
		
		
		
		
		int secondIndex = 60;
		String connurl = ".html";

		DefaultHttpClient client2 = new DefaultHttpClient();
		HttpGet get2;
		HttpResponse response2;
		String html2;

		for (int i = 0; i < 10; i++) {
			get2 = new HttpGet(secondUrl + secondIndex + connurl);// + connUrl
			try {
				response2 = client2.execute(get2);
				if (200 == response2.getStatusLine().getStatusCode()) {
					html2 = EntityUtils
							.toString(response2.getEntity(), "gbk");
					Document doc = Jsoup.parse(html2);
					Elements elements = doc.select("a[href^=/html/VOD]");

					for (Element element : elements) {
						ahref[count] = element.attr("href");
						count++;
//						System.out.println(element.attr("href"));
					}
				}
				secondIndex--;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
//		System.out.println(count);
		
		for (int i = 0; i < ahref.length; i++) {
			// System.out.println(savePath);
			String savePath = "D:/" + 1+ ".txt";
			new Thread(new Spider2223(savePath, ahref[i])).start();
			// new Thread(new Spider23(savePath, ahref[6])).start();
			i+=2;
		}
		System.out.println(count);
	}
}

// 抓网页, 并分析出图片地址
class Spider2223 implements Runnable {
	int num = 0;
	private String firstUrl = "http://eeee99.com";
	String connUrl;
	private String mSavePath;
	FileWriter writer;

	public Spider2223(String savePath, String id) {
		mSavePath = savePath;
		connUrl = id;
	}

	@Override
	public void run() {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet get;
		HttpResponse response;
		String html;

		client.setParams(setParams(client));
		String imgSrc = "";

		try {
			get = new HttpGet(firstUrl + connUrl);
			response = client.execute(get);
			if (200 == response.getStatusLine().getStatusCode()) {
				html = EntityUtils.toString(response.getEntity(), "gbk");
//				System.out.println(html);
				Document doc = Jsoup.parse(html);
				Elements elements = doc.select("a[href$=.torrent]");
				num = elements.size();
				System.out.println(num);
//				for (Element element : elements) {
//					 System.out.println(element.attr("href"));
//				}
				try {
					writer = new FileWriter(mSavePath, true);
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				for (Element e : elements) {
					String timgSrc = "http://eeee99.com"+e.attr("href") + "\r\n";
//					 System.out.println(timgSrc);
					imgSrc += timgSrc;
				}
//				System.out.println(imgSrc);
				try {
					writer.write(imgSrc);
					writer.flush();
					writer.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 设置头信息和超时
	public static HttpParams setParams(DefaultHttpClient client) {
		HttpParams params = client.getParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
		HttpProtocolParams.setUseExpectContinue(params, true);
		HttpProtocolParams
				.setUserAgent(
						params,
						"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1941.0 Safari/537.36");
		HttpConnectionParams.setConnectionTimeout(params, 50000);
		HttpConnectionParams.setSoTimeout(params, 50000);
		return params;
	}
}
//http://eeee99.com/VOD01/list_219.html