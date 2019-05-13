package com.zhang.download;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class MutilDownLoad {

	
	//[1]定义下载的路径 
//	private  static String path = "http://192.168.0.148:8080/feiq.exe";
	private  static String path = "http://183.222.102.64/cache/appdlc.hicloud.com/dl/appdl/application/apk/02/02d70ba0d3a5429fb58bbd6f21a21761/com.smile.gifmaker.1905071530.apk?sign=portal@portal1557722852263&source=portalsite&ich_args2=383-13124711011688_4371627eb448315e9234b7df9eeeb369_10091001_9c89612bd0c1f3d99239518939a83798_a7e61156ab8f04fd787b38761cdff771";
	
	private static final int threadCount = 3; //假设开三个线程 
	
	public static void main(String[] args) {
		
		
		//[一 ☆☆☆☆]获取服务器文件的大小   要计算每个线程下载的开始位置和结束位置
		
		try {

			//(1) 创建一个url对象 参数就是网址 
			URL url = new URL(path);
			//(2)获取HttpURLConnection 链接对象
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			//(3)设置参数  发送get请求
			conn.setRequestMethod("GET"); //默认请求 就是get  要大写
			//(4)设置链接网络的超时时间 
			conn.setConnectTimeout(30000);
			//(5)获取服务器返回的状态码 
			int code = conn.getResponseCode(); //200  代表获取服务器资源全部成功  206请求部分资源    
			if (code == 200) {

				//(6)获取服务器文件的大小
				int length = conn.getContentLength();
				
				System.out.println("length:"+length);
				
				//[二☆☆☆☆ ] 创建一个大小和服务器一模一样的文件 目的提前把空间申请出来 
				RandomAccessFile rafAccessFile = new RandomAccessFile("kuaishou.apk", "rw");
				rafAccessFile.setLength(length);
				
				//(7)算出每个线程下载的大小 
				int blockSize = length /threadCount;
				
				//[三☆☆☆☆  计算每个线程下载的开始位置和结束位置 ]
				for (int i = 0; i < threadCount; i++) {
					int startIndex = i * blockSize;   //每个线程下载的开始位置 
					int endIndex = (i+1)*blockSize - 1;
					//特殊情况 就是最后一个线程 
					if (i == threadCount - 1) {
						//说明是最后一个线程 
						endIndex = length - 1;
						
					}
					
					
					//四 开启线程去服务器下载文件 
					DownLoadThread downLoadThread = new DownLoadThread(startIndex, endIndex, i);
					downLoadThread.start();
					
				}
				
				
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	
	//定义线程去服务器下载文件  
	private static class DownLoadThread extends Thread{
		//通过构造方法把每个线程下载的开始位置和结束位置传递进来 
		
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
			//四  实现去服务器下载文件的逻辑  
			
			try {

				//(1) 创建一个url对象 参数就是网址 
				URL url = new URL(path);
				//(2)获取HttpURLConnection 链接对象
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				//(3)设置参数  发送get请求
				conn.setRequestMethod("GET"); //默认请求 就是get  要大写
				//(4)设置链接网络的超时时间 
				conn.setConnectTimeout(5000);
				
				//[4.1]设置一个请求头Range (作用告诉服务器每个线程下载的开始位置和结束位置)
				conn.setRequestProperty("Range", "bytes="+startIndex+"-"+endIndex);
				
				//(5)获取服务器返回的状态码 
				int code = conn.getResponseCode(); //200  代表获取服务器资源全部成功  206请求部分资源 成功  
				if (code == 206) {
					//[6]创建随机读写文件对象 
					RandomAccessFile raf = new RandomAccessFile("kuaishou.apk", "rw");
					//[6]每个线程要从自己的位置开始写 
					raf.seek(startIndex);
					
					InputStream in = conn.getInputStream(); //存的是feiq.exe 
					
					//[7]把数据写到文件中
					int len = -1;
					byte[] buffer = new byte[1024];
					while((len = in.read(buffer))!=-1){
						raf.write(buffer, 0, len);
					}
					raf.close();//关闭流  释放资源
					
					System.out.println("线程id:"+threadId + "---下载完毕了");
					
					
					
					
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			
		}
	}
	
	
	
}
