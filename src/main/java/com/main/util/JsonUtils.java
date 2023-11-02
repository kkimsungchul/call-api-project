package com.main.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class JsonUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();


    /**
     * Map 을 Json 타입으로 변환
     * @param map json 타입으로 변환할 map 데이터
     * @return String 으로 변환한 json 데이터
     * */
    public static String convertMapToJson(Map<String, Object> map)  {
        String resultJsonData = "";
        try{
            resultJsonData = OBJECT_MAPPER.writeValueAsString(map);

        }catch (JsonProcessingException e){
            //컨버팅 오류
            e.printStackTrace();
        }
        return resultJsonData;
    }

    /**
     * Json 데이터를 map으로 변환
     * @param body json 타입의 String 데이터
     * @return Map 으로 변환한 데이터
     * */
    public static Map<String,Object> convertJsonToMap(String body){
        Map<String,Object> resultMap = new HashMap<String, Object>();
        System.out.println("### body : " + body);
        try{
            resultMap =  OBJECT_MAPPER.readValue(body, new TypeReference<Map<String, Object>>() {});
        }catch (Exception e){
            e.printStackTrace();
        }
        return resultMap;
    }
}
