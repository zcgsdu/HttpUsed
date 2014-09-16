package test;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
//http://tieba.baidu.com/f?kw=c&pn=0,50,100,150,200
public class JsoupParse {

	public static void main(String[] args) {

		try {
			Document document = Jsoup.connect("http://tieba.baidu.com/f?kw=c&pn=0").get();
//			html=html.replaceAll("<!-- <li", "<li");
//			html=html.replaceAll("</script> -->", "</script>");
//			Document doc = Jsoup.parse(html);
			Elements elements = document.select("a[class$=j_th_tit]");
			
			for (Element element : elements) {
				System.out.println(element.attr("href"));
				System.out.println(element.text());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
