package com.zhang.download;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class MutilDownLoad {

	
	//[1]�������ص�·�� 
//	private  static String path = "http://192.168.0.148:8080/feiq.exe";
	private  static String path = "http://183.222.102.64/cache/appdlc.hicloud.com/dl/appdl/application/apk/02/02d70ba0d3a5429fb58bbd6f21a21761/com.smile.gifmaker.1905071530.apk?sign=portal@portal1557722852263&source=portalsite&ich_args2=383-13124711011688_4371627eb448315e9234b7df9eeeb369_10091001_9c89612bd0c1f3d99239518939a83798_a7e61156ab8f04fd787b38761cdff771";
	
	private static final int threadCount = 3; //���迪�����߳� 
	
	public static void main(String[] args) {
		
		
		//[һ �����]��ȡ�������ļ��Ĵ�С   Ҫ����ÿ���߳����صĿ�ʼλ�úͽ���λ��
		
		try {

			//(1) ����һ��url���� ����������ַ 
			URL url = new URL(path);
			//(2)��ȡHttpURLConnection ���Ӷ���
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			//(3)���ò���  ����get����
			conn.setRequestMethod("GET"); //Ĭ������ ����get  Ҫ��д
			//(4)������������ĳ�ʱʱ�� 
			conn.setConnectTimeout(30000);
			//(5)��ȡ���������ص�״̬�� 
			int code = conn.getResponseCode(); //200  �����ȡ��������Դȫ���ɹ�  206���󲿷���Դ    
			if (code == 200) {

				//(6)��ȡ�������ļ��Ĵ�С
				int length = conn.getContentLength();
				
				System.out.println("length:"+length);
				
				//[������� ] ����һ����С�ͷ�����һģһ�����ļ� Ŀ����ǰ�ѿռ�������� 
				RandomAccessFile rafAccessFile = new RandomAccessFile("kuaishou.apk", "rw");
				rafAccessFile.setLength(length);
				
				//(7)���ÿ���߳����صĴ�С 
				int blockSize = length /threadCount;
				
				//[�������  ����ÿ���߳����صĿ�ʼλ�úͽ���λ�� ]
				for (int i = 0; i < threadCount; i++) {
					int startIndex = i * blockSize;   //ÿ���߳����صĿ�ʼλ�� 
					int endIndex = (i+1)*blockSize - 1;
					//������� �������һ���߳� 
					if (i == threadCount - 1) {
						//˵�������һ���߳� 
						endIndex = length - 1;
						
					}
					
					
					//�� �����߳�ȥ�����������ļ� 
					DownLoadThread downLoadThread = new DownLoadThread(startIndex, endIndex, i);
					downLoadThread.start();
					
				}
				
				
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	
	//�����߳�ȥ�����������ļ�  
	private static class DownLoadThread extends Thread{
		//ͨ�����췽����ÿ���߳����صĿ�ʼλ�úͽ���λ�ô��ݽ��� 
		
		private int startIndex;
		private int endIndex;
		private int threadId;
		public DownLoadThread(int startIndex,int endIndex,int threadId){
			this.startIndex = startIndex;
			this.endIndex  = endIndex;
			this.threadId = threadId;
		}
		
		@Override
		public void run() {
			//��  ʵ��ȥ�����������ļ����߼�  
			
			try {

				//(1) ����һ��url���� ����������ַ 
				URL url = new URL(path);
				//(2)��ȡHttpURLConnection ���Ӷ���
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				//(3)���ò���  ����get����
				conn.setRequestMethod("GET"); //Ĭ������ ����get  Ҫ��д
				//(4)������������ĳ�ʱʱ�� 
				conn.setConnectTimeout(5000);
				
				//[4.1]����һ������ͷRange (���ø��߷�����ÿ���߳����صĿ�ʼλ�úͽ���λ��)
				conn.setRequestProperty("Range", "bytes="+startIndex+"-"+endIndex);
				
				//(5)��ȡ���������ص�״̬�� 
				int code = conn.getResponseCode(); //200  �����ȡ��������Դȫ���ɹ�  206���󲿷���Դ �ɹ�  
				if (code == 206) {
					//[6]���������д�ļ����� 
					RandomAccessFile raf = new RandomAccessFile("kuaishou.apk", "rw");
					//[6]ÿ���߳�Ҫ���Լ���λ�ÿ�ʼд 
					raf.seek(startIndex);
					
					InputStream in = conn.getInputStream(); //�����feiq.exe 
					
					//[7]������д���ļ���
					int len = -1;
					byte[] buffer = new byte[1024];
					while((len = in.read(buffer))!=-1){
						raf.write(buffer, 0, len);
					}
					raf.close();//�ر���  �ͷ���Դ
					
					System.out.println("�߳�id:"+threadId + "---���������");
					
					
					
					
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			
		}
	}
	
	
	
}
