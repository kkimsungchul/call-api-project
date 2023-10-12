package com.main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.main.api.CallApi;
import com.main.api.CallApiVO;
import com.main.util.JsonUtils;
import com.main.util.ReadFile;

public class Main {

	public static void main(String[] args) {

		//API 예제
		CallApi callAPI = new CallApi();

		//Header 에 포함될 API 인증 키값
		Map<String,String> keyMap = new HashMap<String, String>();
		keyMap.put("keyName", "PRIVATE-TOKEN");
		keyMap.put("keyValue", "glpat-Zhadfg1235898GXMuCF2Fdgh1SD2nJjeBUb");

		//호출할 URL
		String url = "http://localhost:8080/test";

		//넘길 JSON 데이터
		Map<String,Object> jsonDataMap = new HashMap<String, Object>();
		jsonDataMap.put("name" , "김성철");
		jsonDataMap.put("age" , "33");

		//json 데이터로 변환
		String jsonData = JsonUtils.convertMapToJson(jsonDataMap);

		//Get 호출
		Map<String,Object> getResultMap = callAPI.callRestAPI("get", url,null,keyMap);
		System.out.println("GET API return data : " + getResultMap);

		//Post 호출
		Map<String,Object> postResultMap = callAPI.callRestAPI("post", url, jsonData,keyMap);
		System.out.println("POST API return data : " + postResultMap);




		CallApiVO callApiVO = new CallApiVO.Builder()
				.method("POST")
				.url("https://api.example.com/endpoint")
				.jsonData("{\"key\": \"value\"}")
				.addKey("headerKey", "headerValue")
				.build();






//		//파일 읽기 예제
//		ReadFile readFile = new ReadFile();
//		try {
//			List<String> fileData = readFile.readFile("dataFolder\\data.txt");
//			for(String line : fileData) {
//				System.out.println("line : " + line);
//			}
//
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
	}
}

