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

    private static final int CONNECTION_TIMEOUT = 50000; // 50 seconds
    private static final int READ_TIMEOUT = 50000; // 50 seconds

    //2023.10.12 - get 개선
    /**
     * 지정된 HTTP 메소드로 REST API를 호출하고 응답을 처리하는 메소드
     *
     * @param method   - Http 메소드 (Get / Post / Header - 개발 중)
     * @param url      - 호출할 URL
     * @param jsonData - 보낼 데이터 (Post 방식에 사용)
     * @param keyMap   - 헤더에 포함할 API 인증 key 값
     * @return Map<String, Object> 응답 데이터를 담은 Map
     */
    public Map<String, Object> callRestAPI(String method, String url, String jsonData, Map<String, String> keyMap) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> dataMap = new HashMap<>();
        String responseBody = "";

        switch (method.toLowerCase()) {
            case "get":
                responseBody = get(url, keyMap);
                break;
            case "post":
                responseBody = post(url, keyMap, jsonData);
                break;
            case "header":
                // 헤더를 가져오는 작업 추가
                // responseBody = getHeader(url);
                break;
            default:
                System.out.println("지원하지 않는 HTTP 메소드입니다.");
                break;
        }
        // JSON 데이터를 Map으로 변환
        dataMap = JsonUtils.convertJsonToMap(responseBody);
        return dataMap;
    }

    //2023.10.12 - get 개선
    private String get(String strUrl, Map<String, String> keyMap) {
        BufferedReader br = null;
        String returnString = "";

        try {
            // Create URL object
            URL url = new URL(strUrl);

            // Set SSL socket factory
            HttpsURLConnection.setDefaultSSLSocketFactory(sslErrorPass().getSocketFactory());

            //HTTP 커넥션 - 시작
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(CONNECTION_TIMEOUT);
            con.setReadTimeout(READ_TIMEOUT);
            con.setRequestMethod("GET");
            con.setRequestProperty(keyMap.get("keyName"), keyMap.get("keyValue"));

            // Set doOutput to false for GET request
            con.setDoOutput(false);

            StringBuffer sb = new StringBuffer();
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // 리턴값 파싱
                br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                returnString = sb.toString();
            } else {
                // HTTP 오류 발생
                System.out.println("Error: HTTP response code - " + con.getResponseCode());
            }
        } catch (IOException ioe) {
            System.out.println("GET Rest API Url=" + strUrl + ", exceptionMessage=" + ioe.getMessage());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println("Error closing BufferedReader: " + e.getMessage());
                }
            }
        }

        return returnString;
    }

    //2023.10.12 - post 개선
    private String post(String strUrl, Map<String, String> keyMap, String jsonMessage) {
        String returnString = "";

        try {
            // URL 객체 생성
            URL url = new URL(strUrl);

            // HttpURLConnection을 이용하여 연결 설정
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(CONNECTION_TIMEOUT); // 서버 연결 Timeout 설정
            con.setReadTimeout(READ_TIMEOUT); // InputStream 읽어 오는 Timeout 설정
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty(keyMap.get("keyName"), keyMap.get("keyValue"));
            con.setDoOutput(true); // POST 데이터를 OutputStream으로 전달

            // JSON 메시지 전송
            try (OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream())) {
                wr.write(jsonMessage);
                wr.flush();
            }
            // 응답 처리
            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println("## line: " + line);
                    System.out.println(con.getResponseMessage());
                    sb.append(line).append("\n");
                }
            }

            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                returnString = sb.toString();
                System.out.println("POST Rest API Url=" + strUrl + ", responseStr=" + sb.toString());
            } else {
                System.out.println("POST Rest API Url=" + strUrl + ", responseCode=" + con.getResponseCode() + ", responseMessage=" + con.getResponseMessage());
            }
        } catch (IOException ioe) {
            System.out.println("POST Rest API Url=" + strUrl + ", exceptionMessage=" + ioe.getMessage());
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



    /*2023.10.12 - 코드 리팩토링으로 아래의 내용은 사용하지 않으나,  제거하진 않았음*/
    
//    /**
//     * @param method - Http method (Get / Post / Header - 개발중)
//     * @param url - 호출할 URL
//     * @param jsonData - 보낼 데이터 (Post방식에 사용)
//     * @return Map<String,Object> 헤더에 포함할 API 인증 key값
//     * */
//    public Map<String,Object> callRestAPI(String method ,String url , String jsonData , Map<String,String>keyMap) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        Map<String,Object> dataMap = new HashMap<String, Object>();
//        String body = "";
//
//        if(method.equalsIgnoreCase("get")) {
//            body = get(url,keyMap);
//        }else if(method.equalsIgnoreCase("post")) {
//            body = post(url,keyMap,jsonData);
//        }else if(method.equalsIgnoreCase("header")) {
//            //body = getHeader(url);
//        }
//
//        try {
//            dataMap = JsonUtils.convertJsonToMap(body);
//        }catch (Exception e) {
//            System.out.println("## data parsing error, json to map");
//            e.printStackTrace();
//        }
//        return dataMap;
//    }
//
//
//    /**
//     * @param strUrl 호출 URL
//     * @return return 값
//     */
//    private Map<String,List<String>> getHeader(String strUrl , Map<String,String>keyMap) {
//        Map<String,List<String>> resultMap = new HashMap<String,List<String>>();
//        BufferedReader br = null;
//        String returnString="";
//        try {
//            URL url = new URL(strUrl);
//            HttpsURLConnection.setDefaultSSLSocketFactory(sslErrorPass().getSocketFactory());
//            HttpURLConnection con = (HttpURLConnection) url.openConnection();
//            con.setConnectTimeout(CONNECTION_TIMEOUT); //서버에 연결되는 Timeout 시간 설정
//            con.setReadTimeout(READ_TIMEOUT); // InputStream 읽어 오는 Timeout 시간 설정
//            con.setRequestMethod("GET");
//            con.setRequestProperty(keyMap.get("keyName"), keyMap.get("keyValue"));
////            con.setRequestProperty("Content-Type", "application/json");
//            con.setDoOutput(false);
//
//            StringBuffer sb = new StringBuffer();
//            if (con.getResponseCode() == HttpURLConnection.HTTP_OK){
//
//                return con.getHeaderFields();
//                //System.out.println("GET Rest API Url="+strUrl+", responseStr=" + sb.toString());
//            }else{
//                System.out.println("GET Rest API Url="+strUrl+", responseCode="+con.getResponseCode()+", responseMessage=" + con.getResponseMessage());
//            }
//        }catch(IOException ioe){
//            System.out.println("GET Rest API Url="+strUrl+", exceptionMessage="+ioe.getMessage());
//        }finally{
//            if(br != null){try{br.close();}catch(IOException e){}}
//        }
//
//        return resultMap;
//    }
//
//
//    /**
//     * @param strUrl - 호출 URL
//     * @return String  - 응답 받은 json 데이터
//     */
//    private String get(String strUrl , Map<String,String>keyMap){
//        BufferedReader br = null;
//        String returnString="";
//        try{
//            URL url = new URL(strUrl);
//
//
//            HttpsURLConnection.setDefaultSSLSocketFactory(sslErrorPass().getSocketFactory());
//            //HTTP 커넥션 - 시작
//            HttpURLConnection con = (HttpURLConnection) url.openConnection();
//            con.setConnectTimeout(CONNECTION_TIMEOUT); //서버에 연결되는 Timeout 시간 설정
//            con.setReadTimeout(READ_TIMEOUT); // InputStream 읽어 오는 Timeout 시간 설정
//            con.setRequestMethod("GET");
//            con.setRequestProperty(keyMap.get("keyName"), keyMap.get("keyValue"));
////            con.setRequestProperty("Content-Type", "application/json");
//
//            /*
//             * URLConnection에 대한 doOutput 필드값을 지정된 값으로 설정한다.
//             * URL 연결은 입출력에 사용될 수 있다. URL 연결을 출력용으로 사용하려는 경우 DoOutput 플래그를 true로 설정하고,
//             * 그렇지 않은 경우는 false로 설정해야 한다. 기본값은 false이다.
//             */
//            con.setDoOutput(false);
//
//            StringBuffer sb = new StringBuffer();
//            if (con.getResponseCode() == HttpURLConnection.HTTP_OK){
//                br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
//                String line = null;
//                while ((line = br.readLine()) != null){
//
//                    sb.append(line).append("\n");
//                }
//                returnString = sb.toString();
//            }else{
//                System.out.println("error");
//            }
//        }catch(IOException ioe){
//            System.out.println("GET Rest API Url="+strUrl+", exceptionMessage="+ioe.getMessage());
//        }finally{
//            if(br != null){try{br.close();}catch(IOException e){}}
//        }
//
//        return returnString;
//    }
//
//
//
//
//
//    /**
//     * @param strUrl - 호출 URL
//     * @param jsonMessage - 전달할 데이터
//     * @return String  - 응답 받은 json 데이터
//     */
//    private String post(String strUrl, Map<String,String>keyMap , String jsonMessage){
//        String returnString="";
//        OutputStreamWriter wr = null;
//        BufferedReader br = null;
//
//        try {
//            URL url = new URL(strUrl);
//            HttpsURLConnection.setDefaultSSLSocketFactory(sslErrorPass().getSocketFactory());
//
//
//            HttpURLConnection con = (HttpURLConnection) url.openConnection();
//            con.setConnectTimeout(CONNECTION_TIMEOUT); //서버에 연결되는 Timeout 시간 설정
//            con.setReadTimeout(READ_TIMEOUT); // InputStream 읽어 오는 Timeout 시간 설정
//            con.setRequestMethod("POST");
//            con.setRequestProperty("Content-Type", "application/json");
//            con.setRequestProperty(keyMap.get("keyName"), keyMap.get("keyValue"));
////            con.setDoInput(true);
//            con.setDoOutput(true); //POST 데이터를 OutputStream으로 넘겨 주겠다는 설정
////            con.setUseCaches(false);
////            con.setDefaultUseCaches(false);
//
//            wr = new OutputStreamWriter(con.getOutputStream());
//            wr.write(jsonMessage); //json 형식의 message 전달
//            wr.flush();
//            StringBuffer sb = new StringBuffer();
//            if(con.getResponseCode() == HttpURLConnection.HTTP_OK){
//                br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
//
//                String line = null;
//                while((line = br.readLine()) != null){
//                    System.out.println("## line : " + line);
//                    System.out.println( con.getResponseMessage());
//                    sb.append(line).append("\n");
//                }
//                returnString = sb.toString();
//                System.out.println("POST Rest API Url="+strUrl+", responseStr=" + sb.toString());
//            }else{
//                System.out.println("POST Rest API Url="+strUrl+", responseCode="+con.getResponseCode()+", responseMessage=" + con.getResponseMessage());
//            }
//        }catch(IOException ioe){
//            System.out.println("POST Rest API Url="+strUrl+", exceptionMessage="+ioe.getMessage());
//        }finally{
//            if(br != null){try{br.close();}catch(IOException e){}}
//            if(wr != null){try{wr.close();}catch(IOException e){}}
//        }
//
//        return returnString;
//    }
    
}


