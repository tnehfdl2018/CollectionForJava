package com.soobineey.collectionforjava;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

@Controller
public class MainController {

  @GetMapping("/")
  public String index() {
    MainController mainController = new MainController();
    mainController.getBithumbAPI();

    return "index";
  }


//  public ArrayList<String> getBithumbAPI() {
  public void getBithumbAPI() {
    ArrayList<String> apiData = new ArrayList<>();


    final String API_URL = "https://api.bithumb.com/public/orderbook/BTC_KRW";

    try {
      URL url = new URL(API_URL);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
      String line = bufferedReader.readLine();

      if (line != null) {
        System.out.println(line);
      }

      JSONParser jsonParser = new JSONParser();
      JSONObject jsonObject = (JSONObject) jsonParser.parse(line);
      JSONArray jsonArray = (JSONArray) jsonObject.get("data");
      String aa = (String) jsonObject.get("status");
      System.out.println(aa);


//      String a = jsonArray.toString();

//      jsonObject = (JSONObject) jsonParser.parse(a);
//      jsonArray = (JSONArray) jsonObject.get("bids");
      for (int i =0; i < jsonArray.size(); i++) {
        JSONObject object = (JSONObject) jsonArray.get(i);
//        System.out.println("===== asks =====");
//        System.out.println(object.get("asks"));
        System.out.println("===== asks =====");
        System.out.println(object.get("price"));
        System.out.println(object.get("quantity"));
      }



    } catch (MalformedURLException e) {
      System.out.println("URL을 확인해 주세요.");
      return;
    } catch (IOException e) {
      System.out.println("데이터를 확인해 주세요.");
      return;
    } catch (ParseException e) {
      System.out.println("JSON 파싱에 실패 하였였습니다.");
    }

//    return apiData;
  }
}
