package com.multicampus.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
//https://naveropenapi.apigw.ntruss.com/vision/v1/celebrity
/*
0xok1o8ptm
AbECzqJO1yw9jhtOPJ3Bzu48kIB0oEMUdv2RuPmV
 * */
@RestController
public class CFRController {
	
	@GetMapping("/cfrform")
	public ModelAndView cfrform() {
		return new ModelAndView("clova_cfr_celbrity");
	}
	
	@PostMapping(value="/cfrCelebrity", produces = "application/json; charset=UTF-8")
	public Map<String, String>  cfrResult(@RequestParam("image") MultipartFile file, HttpSession ses) {
		
		StringBuffer reqStr = new StringBuffer();
        String clientId = "0xok1o8ptm";//���ø����̼� Ŭ���̾�Ʈ ���̵�";
        String clientSecret = "AbECzqJO1yw9jhtOPJ3Bzu48kIB0oEMUdv2RuPmV";//���ø����̼� Ŭ���̾�Ʈ ��ũ����";
        Map<String,String> map=new HashMap<>();
        String path=ses.getServletContext().getRealPath("/file");
        File dir=new File(path);
        if(!dir.exists()) {
        	dir.mkdirs();//���ε��� file ���丮 ����
        }
        //÷���� �������ϸ� �˾Ƴ���
        String fname=file.getOriginalFilename();
        File imgF=new File(path, fname);
        try {
        	
        	file.transferTo(imgF);//���ε� ó��
        	
        	
            String paramName = "image"; // �Ķ���͸��� image�� ����
            String imgFile =imgF.getAbsolutePath();
            File uploadFile = new File(imgFile);
            String apiURL = "https://naveropenapi.apigw.ntruss.com/vision/v1/celebrity"; // ������ �� �ν�
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setUseCaches(false);
            con.setDoOutput(true);
            con.setDoInput(true);
            // multipart request
            String boundary = "---" + System.currentTimeMillis() + "---";
            con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
            con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
            OutputStream outputStream = con.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true);
            String LINE_FEED = "\r\n";
            // file �߰�
            String fileName = uploadFile.getName();
            writer.append("--" + boundary).append(LINE_FEED);
            writer.append("Content-Disposition: form-data; name=\"" + paramName + "\"; filename=\"" + fileName + "\"").append(LINE_FEED);
            writer.append("Content-Type: "  + URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED);
            writer.append(LINE_FEED);
            writer.flush();
            FileInputStream inputStream = new FileInputStream(uploadFile);
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            inputStream.close();
            writer.append(LINE_FEED).flush();
            writer.append("--" + boundary + "--").append(LINE_FEED);
            writer.close();
            /////////////////////////////////
            BufferedReader br = null;
            int responseCode = con.getResponseCode();
            if(responseCode==200) { // ���� ȣ��
                br = new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
            } else {  // ���� �߻�
                System.out.println("error!!!!!!! responseCode= " + responseCode);
                br = new BufferedReader(new InputStreamReader(con.getErrorStream(),"UTF-8"));
            }
            String inputLine;
            if(br != null) {
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
                System.out.println(response.toString());
                map.put("result", response.toString());
            } else {
                System.out.println("error !!!");
                map.put("result", "error!!!");
            }
        } catch (Exception e) {
            System.out.println(e);
            map.put("result", "error: "+e.getMessage());
            e.printStackTrace();
        }
		return map;
	}

}