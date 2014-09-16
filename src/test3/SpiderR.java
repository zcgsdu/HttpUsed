package test3;

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

public class SpiderR {

	public static void main(String[] args) {
		for(int i=0;i<17;i++){
		String savePath = "D:/test4/p/"+(i+1)+".txt";
		new Thread(new SpiderRR(savePath,i)).start();
		}
	}
}

// 抓网页, 并分析出图片地址
class SpiderRR implements Runnable {
	
	private String firstUrl = "http://rosi.mn/x/album-";
	private String connUrl = ".htm";
	private int beginIndex;
	private String mSavePath;
	
	

	public SpiderRR(String savePath,int i) {
		mSavePath = savePath;
		beginIndex=i*50+1;
	}
	
	@Override
	public void run() {
		
		FileWriter writer = null;
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet get ;
		HttpResponse response ;
		String html ;
		
		client.setParams(setParams(client));
		for(int i=0;i<50;i++) {
			String imgSrc = "";
			System.out.println("开始分析第" + beginIndex + "页");
			try {
				get = new HttpGet(firstUrl + beginIndex + connUrl);
				response = client.execute(get);
				if(200 == response.getStatusLine().getStatusCode()) {
					html = EntityUtils.toString(response.getEntity(), "utf-8");
					Document doc = Jsoup.parse(html);
					Elements elements = doc.select("a[href^=http://img.cdn.rosi.mn]");////a href="http://img.cdn.rosi.mn
					try {
						writer = new FileWriter(mSavePath, true);
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					for (Element e : elements) {
						String timgSrc = e.attr("href") + "\r\n";
//						 System.out.println(timgSrc);
						imgSrc += timgSrc;
					}
//					System.out.println(imgSrc);
					try {
						writer.write(imgSrc);
						writer.flush();
						writer.close();
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
				}
				beginIndex++;
			}catch(Exception e) {
				e.printStackTrace();
				break;
			}
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
