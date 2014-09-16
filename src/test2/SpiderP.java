package test2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Savepoint;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
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

public class SpiderP {
	public static void main(String[] args) throws Exception {
		
		String[] ahref = new String[180];
		int count = 0;
		String secondUrl = "http://eeee99.com/PIC10/list_";
		int secondIndex = 6;
		String connurl = ".html";

		CloseableHttpClient client2 = new DefaultHttpClient();
		HttpGet get2;
		CloseableHttpResponse response2 = null;
		String html2;

		for (int i = 0; i < 6; i++) {
			get2 = new HttpGet(secondUrl + secondIndex + connurl);// + connUrl
			try {
				response2 = client2.execute(get2);
				if (200 == response2.getStatusLine().getStatusCode()) {
					html2 = EntityUtils
							.toString(response2.getEntity(), "utf-8");
					Document doc = Jsoup.parse(html2);
					Elements elements = doc.select("a[href^=/html/PIC]");

					for (Element element : elements) {
						ahref[count] = element.attr("href");
						count++;
//						System.out.println(element.attr("href"));
					}
				}
				secondIndex--;
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				response2.close();
			}
		}
		System.out.println(count);
		String savePath = "D:/test-e/p10-76/" + 8 + ".txt";
		for (int i = 0; i < ahref.length; i++) {
			// System.out.println(savePath);
			
			new Thread(new Spider223(savePath, ahref[i])).start();
			// new Thread(new Spider23(savePath, ahref[6])).start();
		}
	}
}

// 抓网页, 并分析出图片地址
class Spider223 implements Runnable {
	int num = 0;
	private String firstUrl = "http://eeee99.com";
	String connUrl;
	private String mSavePath;
	BufferedWriter bw;
	FileWriter writer;

	public Spider223(String savePath, String id) {
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
		StringBuilder imgSrc = new StringBuilder();

		try {
			get = new HttpGet(firstUrl + connUrl);
			response = client.execute(get);
			if (200 == response.getStatusLine().getStatusCode()) {
				html = EntityUtils.toString(response.getEntity(), "utf-8");
//				System.out.println(html);
				Document doc = Jsoup.parse(html);
				Elements elements = doc.select("a[href^=http://]");
				num = elements.size();
				System.out.println(num);
//				for (Element element : elements) {
//					 System.out.println(element.attr("href"));
//				}
				try {
//					FileInputStream in = new FileInputStream(new File(""));
//					InputStreamReader isr = new InputStreamReader(in, "UTF-8"); 
//					BufferedReader br = new BufferedReader(isr);
					
					writer = new FileWriter(mSavePath, true);
					bw=new BufferedWriter(writer);
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				for (Element e : elements) {
					String timgSrc = e.attr("href") + "\r\n";
//					 System.out.println(timgSrc);
					imgSrc.append(timgSrc);
				}
//				System.out.println(imgSrc);
				try {
					bw.write(imgSrc.toString());
					bw.flush();
					bw.close();
//					writer.write(imgSrc.toString());
//					writer.flush();
//					writer.close();
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
