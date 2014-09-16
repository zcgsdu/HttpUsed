package test2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
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

public class JanDan {
	public static void main(String[] args) {
		System.out.print("填个目录吧：");
		String savePath = new Scanner(System.in).next();
		new Thread(new Spider(savePath)).start();
	}
}

// 抓网页, 并分析出图片地址
class Spider implements Runnable {
	private String firstUrl = "http://jandan.net/ooxx/page-"; //1118#comments
	private String connUrl = "#comments";
	private int beginIndex = 1022;
	private String preHtml;
	
	public static int failedCount = 0;
	
	private String mSavePath;
	
	public Spider(String savePath) {
		mSavePath = savePath;
	}
	
	@Override
	public void run() {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet get ;
		HttpResponse response ;
		String html ;
		
		client.setParams(setParams(client));
		for(;;) {
			if(Spider.failedCount > 14) {
				System.out.println("没有更多了，停止抓取");
				return;
			}
			
			System.out.println("开始分析第" + beginIndex + "页");
			try {
				get = new HttpGet(firstUrl + beginIndex + connUrl);
				response = client.execute(get);
				if(200 == response.getStatusLine().getStatusCode()) {
					html = EntityUtils.toString(response.getEntity(), "utf-8");
					if(html.equals(preHtml)) {
						break;
					}
					
					preHtml = html;
					
					Document doc = Jsoup.parse(html);
					Elements elements = doc.select(".row img");
					for(Element e : elements) {
						String imgSrc = e.attr("src");
						new Thread(new DownloadImage(imgSrc, mSavePath)).start();
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

// 判断图片是否已经下载，并下载图片
// 没张图片都单独开一个线程下载
// 是不是太消耗资源？
// 使用线程池？
class DownloadImage implements Runnable {
	private String imageSrc;
	private String imageName;
	
	private String mSavePath;
	
	public DownloadImage(String imageSrc, String savePath) {
		this.imageSrc = imageSrc;
		mSavePath = savePath;
	}
	
	private boolean isImageExists() {
		File dir = new File(mSavePath);
		if(!dir.exists()) {
			dir.mkdirs();
		}
		
		String[] splits = imageSrc.split("/");
		imageName = splits[splits.length - 1];
		
		return new File(dir + File.separator + imageName).exists();
	}
	
	@Override
	public void run() {
		if(isImageExists()) {
			Spider.failedCount++;
			return ;
		}
		
		Spider.failedCount = 0;
		
		DefaultHttpClient client = null;
		HttpGet get ;
		HttpResponse response ;
		System.out.println("开始下载：" + imageName);
		
		InputStream in = null;
		FileOutputStream fos = null;
		
		try {
			client = new DefaultHttpClient();
			get= new HttpGet(imageSrc);
			response= client.execute(get);
			if(200 == response.getStatusLine().getStatusCode()) {
				in = response.getEntity().getContent();
				byte[] by = new byte[1024];
				int len = -1;
				fos = new FileOutputStream(mSavePath + File.separator + imageName);
				while(-1 != (len = in.read(by))) {
					fos.write(by, 0, len);
				}
				
				fos.flush();
				in.close();
				fos.close();
			}
			
			System.out.println(" " + imageName + "下载完成");
			client.getConnectionManager().shutdown();
		}catch(Exception e) {
			try {
				in.close();
				fos.close();
			} catch (Exception e1) {
			}
			client.getConnectionManager().shutdown();
			System.err.println("抛出异常并不可怕， 这个我不要了！\n删除" + imageName);
			new File(mSavePath + File.separator + imageName).delete();
			return;
		}
	}
}
