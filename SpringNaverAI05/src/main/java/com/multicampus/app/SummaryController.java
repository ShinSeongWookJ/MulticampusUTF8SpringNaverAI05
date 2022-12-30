package com.multicampus.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.log4j.Log4j;
/*��û�� �������� ������ ����
 * {
  "document": {
    "title": "'�Ϸ� 2000��' �� Ŀ���� ����۱� ����",
    "content": "����۱� �̿�ݾ��� �Ϸ� ��� 2000����� �Ѿ��. �ѱ������� 17�� ��ǥ�� '2019�� ��ݱ��� �������޼��� �̿� ��Ȳ'�� ������ ���� ��ݱ� ����۱ݼ��� �̿�ݾ�(�����)�� ������ �Ϲݱ� ��� 60.7% ������ 2005������� ����ƴ�. ���� �Ⱓ �̿�Ǽ�(�����)�� 34.8% �þ 218�����̾���. ���� �۱� ���忡�� �����������޼��񽺸� �����ϴ� ���ڱ������ڿ� ������� ���� �����ϰ� �ִ�. �̿�ݾ��� ���ڱ������ڰ� �Ϸ���� 1879���, ��������� 126����̾���. ������ īī������, �佺 �� ����۱� ���񽺸� �����ϴ� ��ü �� ������ ��ȭ�Ǹ鼭 �̿�Ը� ũ�� Ȯ��ƴٰ� �м��ߴ�. ��ȸ ��������ȸ �Ҽ� �ٸ��̷��� ���ǵ� �ǿ��� ������ īī������, �佺 �� �����������޼��� ������ü�� ������ ������ ������� 1000��� �̻��� �����ߴ�. ������ ��� ����Ը�� īī�����̰� 491���, ��ٸ��ۺ�ī(�佺)�� 134��� �� ������ ���Ҵ�."
  },
  "option": {
    "language": "ko",
    "model": "news",
    "tone": 2,
    "summaryCount": 3
  }
}
 * */

@RestController
@Log4j
public class SummaryController {
	
	@GetMapping("/summaryform")
	public ModelAndView summaryForm() {
		ModelAndView mv=new ModelAndView("clova_summary");//����� ����
		//WEB-INF/views/clova_summary.jsp
		return mv;
	}
	
	@PostMapping(value="/summaryEnd", produces = "text/plain; charset=UTF-8")
	public String summaryEnd(@RequestParam("title") String title, @RequestParam("content") String content) 
	throws Exception
	{
		log.info("title==="+title+", content==="+content);
		String clientId="v75fempd8s";
		String clientSecret="IyVW6atlpYgkQEuteKbwVYlcD11jGzmqnE5dTM9d";
		String urlStr="https://naveropenapi.apigw.ntruss.com/text-summary/v1/summarize";
		
		URL url=new URL(urlStr);
		URLConnection urlCon=url.openConnection();
		HttpURLConnection con=(HttpURLConnection)urlCon;
		StringBuffer response=new StringBuffer();
		con.setRequestMethod("POST");//��û��� ����
		con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
		con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
		con.setRequestProperty("Content-Type", "application/json");
		
		//������ �����͸� json�������� ������
		JSONObject doc=new JSONObject();
		doc.put("title", title);
		doc.put("content", content);
		
		JSONObject option=new JSONObject();
		option.put("language", "ko");//ko(�ѱ���), ja(�Ϻ���)
		option.put("model", "news");//news, general
		option.put("tone", "2");//����. => 2: ����ü
		option.put("summaryCount", "3");//������ 3�������� ���
		
		JSONObject root=new JSONObject();
		root.put("document", doc);
		root.put("option", option);
		
		String params=root.toString();
		log.info("params====="+params);
		con.setUseCaches(false);
		con.setDoOutput(true);
		con.setDoInput(true);
		
		//���̹� Ŭ���� ������ ��û �Ķ���� �����͸� �����ϱ� ���� ��Ʈ�� ����
		OutputStream os=con.getOutputStream();
		BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
		bw.write(params);
		bw.flush();
		bw.close();
		os.close();
		int responseCode=con.getResponseCode();
		log.info("responseCode===="+responseCode);
		BufferedReader br;
		if(responseCode==200) {
			br=new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
		}else {
			log.info("Error�߻�: "+responseCode);
			br=new BufferedReader(new InputStreamReader(con.getErrorStream(),"UTF-8"));
		}
		String line="";
		if(br!=null) {
			while((line=br.readLine())!=null) {
				response.append(line);
			}
			br.close();
		}
		log.info("response==="+response.toString());
		JSONObject json=new JSONObject(response.toString());
		String summary=json.getString("summary");
		
		return summary;
	}

}




