package download;



public class MultiThreadDownloader {

	public static void main(String[] args) throws Exception {
		int threadCount = 3;	//ָ���߳�����
		String path = "http://mse.dlservice.microsoft.com/download/1/E/D/1ED80C09-218B-44D7-B72D-E1451634E72D/zhcn/amd64/mseinstall.exe";	//ָ�������ļ�·��
		MultiThreadDownloadUtil.download(path, threadCount);

	}
}
