package com.multicampus.app;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	String clientId = "0xok1o8ptm";             // Application Client ID";
    String clientSecret = "AbECzqJO1yw9jhtOPJ3Bzu48kIB0oEMUdv2RuPmV";     // Application Client Secret";
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		
		return "home";
	}
	@PostMapping(value="/speech", produces = "text/plain; charset=UTF-8")
	@ResponseBody
	public String speechRecognitionOk(@RequestParam("mp3file") MultipartFile mfile,
			@RequestParam("language") String language,HttpSession session) {
		System.out.println(">>>>>");
		
		String path=session.getServletContext().getRealPath("/file");
		String fname=mfile.getOriginalFilename();
		
		
		
        StringBuffer response = new StringBuffer();
        try {
        	mfile.transferTo(new File(path,fname));
            String imgFile = path+"/"+fname;//"음성 파일 경로";
            File voiceFile = new File(imgFile);

            //String language = "Kor";        // 언어 코드 ( Kor, Jpn, Eng, Chn )
            String apiURL = "https://naveropenapi.apigw.ntruss.com/recog/v1/stt?lang=" + language;
            URL url = new URL(apiURL);

            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setUseCaches(false);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/octet-stream");
            conn.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
            conn.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);

            OutputStream outputStream = conn.getOutputStream();
            FileInputStream inputStream = new FileInputStream(voiceFile);
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            inputStream.close();
            BufferedReader br = null;
            int responseCode = conn.getResponseCode();
            if(responseCode == 200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
                System.out.println("200응답 옴");
            } else {  // 오류 발생
                System.out.println("error!!!!!!! responseCode= " + responseCode);
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream(),"UTF-8"));
            }
            String inputLine;

            if(br != null) {
                //StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                
                br.close();
                System.out.println(response.toString());
			} /*
				 * else { System.out.println("error !!!"); }
				 */
        } catch (Exception e) {
            System.out.println(e);
        }
		return response.toString();
	}
	
	@PostMapping("/voice")
	@ResponseBody
	public void voiceOk(String text, HttpServletResponse res) {
		System.out.println("text=>"+text);
				
	        try {
	            text = URLEncoder.encode(text, "UTF-8"); // 13자
	            String apiURL = "https://naveropenapi.apigw.ntruss.com/tts-premium/v1/tts";
	            URL url = new URL(apiURL);
	            HttpURLConnection con = (HttpURLConnection)url.openConnection();
	            con.setRequestMethod("POST");
	            con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
	            con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
	            // post request
	            String postParams = "speaker=noyj&volume=0&speed=0&pitch=0&format=mp3&text=" + text;
	            con.setDoOutput(true);
	            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
	            wr.writeBytes(postParams);
	            wr.flush();
	            wr.close();
	            int responseCode = con.getResponseCode();
	            BufferedReader br;
	            if(responseCode==200) { // 정상 호출
	                InputStream is = con.getInputStream();
	                int read = 0;
	                byte[] bytes = new byte[1024];
	                // 랜덤한 이름으로 mp3 파일 생성
	                String tempname = Long.valueOf(new Date().getTime()).toString();
	                File f = new File(tempname + ".mp3");
	                f.createNewFile();
	               // OutputStream outputStream = new FileOutputStream(f);
	                ServletOutputStream outputStream=res.getOutputStream();
	                //우리 서버로 응답을 보내는 작업을 해보자.
	                while ((read =is.read(bytes)) != -1) {
	                    outputStream.write(bytes, 0, read);
	                }
	                is.close();
	            } else {  // 오류 발생
	                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
	                String inputLine;
	                StringBuffer buf = new StringBuffer();
	                while ((inputLine = br.readLine()) != null) {
	                	buf.append(inputLine);
	                }
	                br.close();
	                System.out.println(buf.toString());
	            }
	        } catch (Exception e) {
	            System.out.println(e);
	        }
		
	}//------------------------------------------
	
}
