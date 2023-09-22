package com.main.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ReadFile {
	

	//수정 전
//	public List<String> readFile(String filePath) throws IOException {
//		//"call-api-project\\data.txt"		
//		List<String> resultList = new ArrayList<String>();
//		Path path = Paths.get(filePath);
//		for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
//			resultList.add(line);
//		}
//		return resultList;
//	}

	//수정 후
	public List<String> readFile(String filePath) throws IOException {
		//"call-api-project\\data.txt"		
		Path path = Paths.get(filePath);
		return new ArrayList<String>(Files.readAllLines(path, StandardCharsets.UTF_8));
	}
	

	public void writeFile(List<HashMap<String,Object>> projectList , String fileName) throws Exception{
		String path = "C:\\IntelliJProject\\call-api-project-main\\"+fileName;
		System.out.println(path);
		
        File file = new File(path);
        FileWriter fileWriter = new FileWriter(file);		
		for(HashMap<String,Object> map :  projectList) {
			fileWriter.write(map.toString());
	        fileWriter.write("\n");
	        
		}
		fileWriter.close();	
	}
	
	public void writeFile(ArrayList<String> projectList , String fileName) throws Exception{
		String path = "C:\\IntelliJProject\\call-api-project-main\\"+fileName;
		System.out.println(path);
		
        File file = new File(path);
        FileWriter fileWriter = new FileWriter(file);		
		for(String search :  projectList) {
			fileWriter.write(search);
	        fileWriter.write("\n");
	        
		}
		fileWriter.close();	
	}
	
	
	
	
	

}
