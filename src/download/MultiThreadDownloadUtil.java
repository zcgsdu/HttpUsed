package download;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;


public class MultiThreadDownloadUtil{
	private static int filesize;
	private static int block;
	private static String filename;
	private static int downloadsize;
	private class MyThread extends Thread{
		private int i;			//线程ID
		private String path;	//下载文件的URL

		public MyThread(int i, String path) {
			this.i = i;
			this.path = path;
			downloadsize = 0;
		}
		@Override
		public void run() {
			try{
				System.out.println("线程"+(i+1)+"开始下载");
				//1.打开文件，并定位位置
				RandomAccessFile raf = new RandomAccessFile(new File(filename), "rwd");
				raf.seek(i*block);			//定位到此线程要负责下载的位置
				int start = i*block;
				int end = (i+1)*block-1;
				//2.连接服务器
				URL url = new URL(path);
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("range", "bytes="+start+"-"+end);	//发出请求头
				
				if(conn.getResponseCode()==206){		//注意：分段下载的返回码为206，而不是200
					InputStream in = conn.getInputStream();
					int length = 0;
					byte[]data = new byte[1024];
					while((length=in.read(data))!=-1){
						raf.write(data,0,length);		//写入本地文件
					}
				}
				//显示下载进度
				downloadsize += (end-start);
				System.out.println("已下载"+downloadsize/1024.0+"k,共"+filesize/1024.0+"k");
				//3.关闭文件
				raf.close();
				System.out.println("线程"+(i+1)+"结束下载...");
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	/**
	 * 下载文件
	 * @param path			URL
	 * @param threadcount   线程数
	 * @throws Exception
	 */
	public static void download(String path,int threadcount) throws Exception{
		filename = path.substring(path.lastIndexOf('/')+1);
		System.out.println(filename);
		filesize = getFileSize(path);
		block = getBlockSize(filesize,threadcount);
		createLocalRandomFile(filesize);
		MultiThreadDownloadUtil mdu = new MultiThreadDownloadUtil();
		for(int i=0;i<threadcount;i++){
			mdu.new MyThread(i,path).start();
		}
	}
	/**
	 * 创建一个本地文件，并设置文件的大小
	 * @param filesize
	 * @throws Exception
	 */
	private static void createLocalRandomFile(int filesize) throws Exception {
		RandomAccessFile raf = new RandomAccessFile(new File(filename), "rwd");
		raf.setLength(filesize);
		raf.close();
	}
	/**
	 * 根据文件总大小和线程数求出每个线程要下载的数据量
	 * @param filesize
	 * @param threadcount
	 * @return
	 */
	private static int getBlockSize(int filesize, int threadcount) {
		return filesize%threadcount==0?filesize/threadcount:(filesize/threadcount+1);
	}
	/**
	 * 求出文件总大小
	 * @param path
	 * @return
	 * @throws Exception
	 */
	private static int getFileSize(String path) throws Exception{
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setRequestMethod("GET");
		if(conn.getResponseCode()==200){
			return conn.getContentLength();
		}
		else{
			return 0;
		}
	}
}