package test2;

import java.io.FileWriter;
import java.io.IOException;

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

public class SpiderBaiDu {
	public static void main(String[] args) {
		int count = 0;
		int pageNum=2;//��Ҫץ����ҳ
		String[] ahref = new String[100];//�ٶ�����ÿҳ��50������
		
		String secondUrl = "http://tieba.baidu.com/f/good?kw=java&cid=0&pn=";//�����java����������
		int secondIndex = 0;//��secondUrl�������ÿҳ��URL
		DefaultHttpClient client2 = new DefaultHttpClient();
		HttpGet get2;
		HttpResponse response2;
		String html2;
		
		for (int i = 0; i < pageNum; i++) {
			get2 = new HttpGet(secondUrl + secondIndex);
			try {
				response2 = client2.execute(get2);
				if (200 == response2.getStatusLine().getStatusCode()) {
					html2 = EntityUtils
							.toString(response2.getEntity(), "utf-8");
					//���Կ�һ�����ɵ�Դ���룬����ÿҳδ��ʾ�Ĳ��ָ�ע���ˣ�ֱ����jsoup���������������������൱�ڰ�ע��ȥ��
					html2 = html2.replaceAll("<!-- <li", "<li");
					html2 = html2.replaceAll("</script> -->", "</script>");
					Document doc = Jsoup.parse(html2);
					Elements elements = doc.select("a[class$=j_th_tit]");//ȡ���������ӣ�������Ҫ�ҹ��ɣ�ÿ����վ����һ��
					//ȡ��ÿ�����ӵ�url
					for (Element element : elements) {
						ahref[count] = element.attr("href");
						count++;
					}
				}
				secondIndex+=50;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		 String savePath = "D:/test2/" +1+ ".txt";
		 //Ϊÿ�����ӿ���һ���߳�
		 for (int i = 0; i < pageNum*50; i++) {
		 System.out.println(ahref[i]);
			
		new Thread(new Spider23(savePath, ahref[i])).start();

		 }
	}
}
/**
 * 
 * ����������Ĺ��̣��������ʵҲ��࣬������ÿ�������������ͼƬԴ�����������Ȼ��ȡ��urlд���ļ�
 *
 */
// ץ��ҳ, ��������ͼƬ��ַ
class Spider23 implements Runnable {
	int num = 0;
	int count=0;
	String id;
	private String firstUrl = "http://tieba.baidu.com";
	String connUrl;
	private int beginIndex = 1;

	private String preHtml;

	public static int failedCount = 0;

	private String mSavePath;
	FileWriter writer;

	public Spider23(String savePath, String id) {
		mSavePath = savePath;
		connUrl = id + "?pn=";
	}

	@Override
	public void run() {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet get;
		HttpResponse response;
		String html;

		client.setParams(setParams(client));
		for (;;) {
			if(count==beginIndex){
				return;
			}
			String imgSrc = "";
			 System.out.println("��ʼ������" + beginIndex + "ҳ");
			try {

				get = new HttpGet(firstUrl + connUrl + beginIndex);//
				response = client.execute(get);
				if (200 == response.getStatusLine().getStatusCode()) {
					html = EntityUtils.toString(response.getEntity(), "utf-8");
					if (html.equals(preHtml)) {
						break;
					}

					preHtml = html;
					html = html.replaceAll("<!-- <div", "<div");
					html = html.replaceAll("</script> -->", "</script>");

					Document doc = Jsoup.parse(html);
					Elements elements = doc.select("img[class$=BDE_Image]");// �ٶ�����
					if (num == 0) {
						
						num++;
						Elements elements2 = doc.select("span[class$=red]");
						count=Integer.parseInt(elements2.last().text())+1;
					}
					// for (Element element : elements2) {
					// // System.out.println(element.attr("href"));
					// System.out.println(element.text());
					// }
					try {
						writer = new FileWriter(mSavePath, true);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					;
					for (Element e : elements) {
						String timgSrc = e.attr("src") + "\r\n";
						// System.out.println(timgSrc);
						imgSrc += timgSrc;

						// System.out.println(imgSrc);
						// new Thread(new DownloadImage332(imgSrc, mSavePath))
						// .start();
					}
				}
				try {

					writer.write(imgSrc);
					writer.flush();
					writer.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
				beginIndex++;
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
	}

	// ����ͷ��Ϣ�ͳ�ʱ
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



