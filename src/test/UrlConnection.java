package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class UrlConnection {

	public static void main(String[] args) {
		int count=0;
		String url="http://tieba.baidu.com/f/good";
		
		UrlConnection uc= new UrlConnection();
		for (int i = 0; i < 3; i++) {
			String parm="kw=ͼƬ&pn="+i;
			String html=uc.GetHTMLFromURI(url,parm);
			HashSet<String> hs = new HashSet<String>();
			hs=uc.getURIFromString(html);
			for (String string : hs) {
				if(string.length()==42){
					count++;
					System.out.println(string);
				}
			}
		}
		
		System.out.println(count++);
	}
	public  String GetHTMLFromURI(String url, String param) {
		
			StringBuffer result = new StringBuffer();
			BufferedReader in = null;
			String html = null;
			RandomAccessFile afile = null;
			try {
				String urlName = url + "?" + param;
				URL realUrl = new URL(urlName);
				URLConnection conn = realUrl.openConnection();
				conn.setRequestProperty("accept", "*/*");
				conn.setRequestProperty("connection", "Keep-Alive");
				conn.setRequestProperty(
						"user-agent",
						"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.154 Safari/537.36");
				conn.connect();
				in = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
				String line;
				while ((line = in.readLine()) != null) {
					result.append(line);
				}
				html = new String(result);

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {

				try {
					if (in != null) {
						in.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			return html;

	}
	public HashSet<String> getURIFromString(String html) {
		HashSet<String> uris = new HashSet<String>();
		if (html != null) {
			// �ַ��������ͣ�<a href=""></a>
			// ������ʽ��
			// Pattern apattern = Pattern
			// .compile("(?is)(?<URL><a\\s*href=\"[^\"]*\"[^>]*>.*?</a>)");
			Pattern apattern = Pattern
					.compile("(<a[\\s+]*([^>h]|h(?!ref\b))*href[\\s+]*=[\\s+]*[('|\")]?)([^(\\s+|'|\")]*)([^>]*>)");

			Matcher matcher = apattern.matcher(html);
			// ����ҵ��˳����ӵĻ����͵ݹ����
			while (matcher.find()) {
				// System.out.println(matcher.group());
				//������õ�һ��jar����jsoup-1.7.3.jar ����String ����ȡhref=""�е�ֵ
				Document doc = Jsoup.parseBodyFragment(matcher.group());
				Elements achors = doc.select("a");
				Element a = achors.first();
				String href = a.attr("href");

				//Լ��������վ�����������·���ģ�Ҳ����û�������ģ�һ�������ȫ�������п���
				if (href.startsWith("tel:")) {//

				} else if (href.startsWith("http://")) {//�����վ�ĳ�����

				} else if (href.contains("javascript:void(0)")) {//<a href="javscript:void(0)"></a>

				} else if (href.contains("history.go(-1);")) {//...

				} else {//����վ������
					// ���Ǹ������µ�����ʱ���͵ݹ����
					String href2 = "http://tieba.baidu.com/f/good" + href;
					// getURIFromString(GetDataFromURL.sendGet(href2, null));
					if (!uris.contains(href2)) {
						uris.add(href2);
					}

				}
			}
		}
		return uris;

	}
}
