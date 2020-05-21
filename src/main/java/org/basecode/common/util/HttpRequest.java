package org.basecode.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

/**
 * java原生的http请求
 * @author charles
 *
 */
public class HttpRequest {
	private static Logger logger = LoggerFactory.getLogger(HttpRequest.class);
	public static String sendGet(String url, String param){
		return sendGet(url, param, 3000, 2000);
	}
	public static String sendGet(String url, String param, int connectTimeout){
		return sendGet(url, param, connectTimeout, 2000);
	}
	
	public static String sendPost(String url, String param){
		return sendPost(url, param, 3000, 2000);
	}
	public static String sendPost(String url, String param, int connectTimeout){
		return sendPost(url, param, connectTimeout, 2000);
	}
    /**
     * <b>向指定URL发送GET方法的请求</b>
     * 
     * @param url 发送请求的URL
     * @param param  请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param, int connectTimeout, int readTimeout) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("Accept-Charset", "utf-8");
            connection.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
            connection.setRequestProperty("charset","UTF-8");
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.setConnectTimeout(connectTimeout);//设置连接超时
            connection.setReadTimeout(readTimeout);//设置读取数据超时
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

	/**
	 * <b>向指定 URL 发送POST方法的请求</b>
	 * @param url   发送请求的 URL
	 * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 */
    public static String sendPost(String url, String param, int connectTimeout, int readTimeout) {
    	String result = "";
    	if(url.startsWith("http")) {
    		PrintWriter out = null;
    		BufferedReader in = null;
    		try {
    			URL realUrl = new URL(url);
    			// 打开和URL之间的连接
    			URLConnection conn = realUrl.openConnection();
    			// 设置通用的请求属性
    			conn.setRequestProperty("Accept-Charset", "utf-8");
    			conn.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
    			conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
    			conn.setRequestProperty("charset","UTF-8");
    			conn.setRequestProperty("accept", "*/*");
    			conn.setRequestProperty("connection", "Keep-Alive");
    			conn.setRequestProperty("user-agent",
    					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
    			conn.setConnectTimeout(connectTimeout);//设置连接超时
    			conn.setReadTimeout(readTimeout);//设置读取数据超时
    			// 发送POST请求必须设置如下两行
    			conn.setDoOutput(true);
    			conn.setDoInput(true);
    			// 获取URLConnection对象对应的输出流
    			out = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(),"UTF-8"));
    			// 发送请求参数
    			out.print(param);
    			//out.write(param);
    			System.out.println("发送POST请求:" + url +"参数："+ param);
    			// flush输出流的缓冲
    			out.flush();
    			// 定义BufferedReader输入流来读取URL的响应
    			in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
    			String line;
    			while ((line = in.readLine()) != null) {
    				result += line;
    			}
    		} catch (Exception e) {
    			System.out.println("发送 POST 请求出现异常！"+e);
    			e.printStackTrace();
    		}
    		//使用finally块来关闭输出流、输入流
    		finally{
    			try{
    				if(out!=null){
    					out.close();
    				}
    				if(in!=null){
    					in.close();
    				}
    			}
    			catch(IOException ex){
    				ex.printStackTrace();
    			}
    		}
    	}else {
//    		throw new BusinessException("20000500", "发送网络请求错误，URL："+url+"不是正确的网络地址！");
    		logger.debug("发送网络请求错误，URL："+url+"不是正确的网络地址！");
    	}
        return result;
    }    
}