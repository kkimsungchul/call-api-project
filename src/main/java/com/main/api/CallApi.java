package com.main.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.util.JsonUtils;

//Java 로 외부 URL호출하여 데이터 가져오는 클래스
public class CallApi {

	/**
	 * @param method - Http method (Get / Post / Header - 개발중)
	 * @param url - 호출할 URL
	 * @param jsonData - 보낼 데이터 (Post방식에 사용)
	 * @return Map<String,Object> 헤더에 포함할 API 인증 key값
	 * */
	public Map<String,Object> callRestAPI(String method ,String url , String jsonData , Map<String,String>keyMap) {
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String,Object> dataMap = new HashMap<String, Object>();
		String body = "";

		if(method.equalsIgnoreCase("get")) {
			body = get(url,keyMap);
		}else if(method.equalsIgnoreCase("post")) {
			body = post(url,keyMap,jsonData);
		}else if(method.equalsIgnoreCase("header")) {
			//body = getHeader(url);
		}

		try {
			dataMap = JsonUtils.convertJsonToMap(body);
		}catch (Exception e) {
			System.out.println("## data parsing error, json to map");
			e.printStackTrace();
		}
		return dataMap;
	}
	

	/**
	 * @param strUrl 호출 URL
	 * @return return 값
	 */
	private Map<String,List<String>> getHeader(String strUrl , Map<String,String>keyMap) {
    	Map<String,List<String>> resultMap = new HashMap<String,List<String>>();
        BufferedReader br = null;
        String returnString="";
        try {
            URL url = new URL(strUrl);
            HttpsURLConnection.setDefaultSSLSocketFactory(sslErrorPass().getSocketFactory());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(5000); //서버에 연결되는 Timeout 시간 설정
            con.setReadTimeout(5000); // InputStream 읽어 오는 Timeout 시간 설정
            con.setRequestMethod("GET");
            con.setRequestProperty(keyMap.get("keyName"), keyMap.get("keyValue"));
//            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(false);

            StringBuffer sb = new StringBuffer();
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK){
                
            	return con.getHeaderFields();
                //System.out.println("GET Rest API Url="+strUrl+", responseStr=" + sb.toString());
            }else{
            	System.out.println("GET Rest API Url="+strUrl+", responseCode="+con.getResponseCode()+", responseMessage=" + con.getResponseMessage());
            }
        }catch(IOException ioe){
        	System.out.println("GET Rest API Url="+strUrl+", exceptionMessage="+ioe.getMessage());
        }finally{
            if(br != null){try{br.close();}catch(IOException e){}}
        }
        
        return resultMap;
    }
	
	
	/**
	 * @param strUrl - 호출 URL
	 * @return String  - 응답 받은 json 데이터
	 */
    private String get(String strUrl , Map<String,String>keyMap){

        String returnString="";
        try{
            URL url = new URL(strUrl);


            HttpsURLConnection.setDefaultSSLSocketFactory(sslErrorPass().getSocketFactory());             
            //HTTP 커넥션 - 시작
            HttpURLConnection con = (HttpURLConnection) url.openConnection(); 
            con.setConnectTimeout(50000); //서버에 연결되는 Timeout 시간 설정
            con.setReadTimeout(50000); // InputStream 읽어 오는 Timeout 시간 설정
            con.setRequestMethod("GET");
            con.setRequestProperty(keyMap.get("keyName"), keyMap.get("keyValue"));
//            con.setRequestProperty("Content-Type", "application/json");

            /*
             * URLConnection에 대한 doOutput 필드값을 지정된 값으로 설정한다.
             * URL 연결은 입출력에 사용될 수 있다. URL 연결을 출력용으로 사용하려는 경우 DoOutput 플래그를 true로 설정하고,
             * 그렇지 않은 경우는 false로 설정해야 한다. 기본값은 false이다.
             */
            con.setDoOutput(false); 

            StringBuffer sb = new StringBuffer();
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK){
                try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"))) {
                    String line = null;
                    while ((line = br.readLine()) != null){
                        sb.append(line).append("\n");
                    }
                    returnString = sb.toString();
                }
            }else{
            	System.out.println("error");
            }
        }catch(IOException ioe){
        	System.out.println("GET Rest API Url="+strUrl+", exceptionMessage="+ioe.getMessage());
        }
        return returnString;
    }
	
    
    /**
     * @param strUrl - 호출 URL 
     * @param jsonMessage - 전달할 데이터
     * @return String  - 응답 받은 json 데이터
     */
    private String post(String strUrl, Map<String,String>keyMap , String jsonMessage){
		String returnString="";
        try {
            URL url = new URL(strUrl);
            HttpsURLConnection.setDefaultSSLSocketFactory(sslErrorPass().getSocketFactory());
            
            
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(5000); //서버에 연결되는 Timeout 시간 설정
            con.setReadTimeout(5000); // InputStream 읽어 오는 Timeout 시간 설정
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
//            con.setDoInput(true);
            con.setDoOutput(true); //POST 데이터를 OutputStream으로 넘겨 주겠다는 설정 
//            con.setUseCaches(false);
//            con.setDefaultUseCaches(false);
            try (OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream())) {
                wr.write(jsonMessage); //json 형식의 message 전달
                wr.flush();
                StringBuffer sb = new StringBuffer();
                if(con.getResponseCode() == HttpURLConnection.HTTP_OK){
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"))) {

                        String line = null;
                        while ((line = br.readLine()) != null) {
                            System.out.println("## line : " + line);
                            System.out.println(con.getResponseMessage());
                            sb.append(line).append("\n");
                        }
                        returnString = sb.toString();
                        System.out.println("POST Rest API Url=" + strUrl + ", responseStr=" + sb.toString());
                    }
                }else{
                    System.out.println("POST Rest API Url="+strUrl+", responseCode="+con.getResponseCode()+", responseMessage=" + con.getResponseMessage());
                }
            }
        }catch(IOException ioe){
        	System.out.println("POST Rest API Url="+strUrl+", exceptionMessage="+ioe.getMessage());
        }
        
        return returnString;
    }
	
	
    //SSL 인증서 오류 무시
	public SSLContext sslErrorPass() {
		SSLContext sslContext = null;
		try {
			
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}
				
				public void checkClientTrusted(X509Certificate[] certs, String authType){
				}
				
				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			} };

            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
		}catch (Exception e) {
			e.printStackTrace();
		}
		return sslContext;
	}
	
}