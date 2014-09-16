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
		private int i;			//�߳�ID
		private String path;	//�����ļ���URL

		public MyThread(int i, String path) {
			this.i = i;
			this.path = path;
			downloadsize = 0;
		}
		@Override
		public void run() {
			try{
				System.out.println("�߳�"+(i+1)+"��ʼ����");
				//1.���ļ�������λλ��
				RandomAccessFile raf = new RandomAccessFile(new File(filename), "rwd");
				raf.seek(i*block);			//��λ�����߳�Ҫ�������ص�λ��
				int start = i*block;
				int end = (i+1)*block-1;
				//2.���ӷ�����
				URL url = new URL(path);
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("range", "bytes="+start+"-"+end);	//��������ͷ
				
				if(conn.getResponseCode()==206){		//ע�⣺�ֶ����صķ�����Ϊ206��������200
					InputStream in = conn.getInputStream();
					int length = 0;
					byte[]data = new byte[1024];
					while((length=in.read(data))!=-1){
						raf.write(data,0,length);		//д�뱾���ļ�
					}
				}
				//��ʾ���ؽ���
				downloadsize += (end-start);
				System.out.println("������"+downloadsize/1024.0+"k,��"+filesize/1024.0+"k");
				//3.�ر��ļ�
				raf.close();
				System.out.println("�߳�"+(i+1)+"��������...");
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	/**
	 * �����ļ�
	 * @param path			URL
	 * @param threadcount   �߳���
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
	 * ����һ�������ļ����������ļ��Ĵ�С
	 * @param filesize
	 * @throws Exception
	 */
	private static void createLocalRandomFile(int filesize) throws Exception {
		RandomAccessFile raf = new RandomAccessFile(new File(filename), "rwd");
		raf.setLength(filesize);
		raf.close();
	}
	/**
	 * �����ļ��ܴ�С���߳������ÿ���߳�Ҫ���ص�������
	 * @param filesize
	 * @param threadcount
	 * @return
	 */
	private static int getBlockSize(int filesize, int threadcount) {
		return filesize%threadcount==0?filesize/threadcount:(filesize/threadcount+1);
	}
	/**
	 * ����ļ��ܴ�С
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