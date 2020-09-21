package com.soobineey.collectionforjava;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

@Controller
public class MainController {
  private HashMap<Integer, String> asksPriceHashMap = new HashMap<>();
  private HashMap<Integer, String> asksQuantityHashMap = new HashMap<>();
  private HashMap<Integer, String> bidsPriceHashMap = new HashMap<>();
  private HashMap<Integer, String> bidsQuantityHashMap = new HashMap<>();

  private final String BASE_API_URL = "https://api.bithumb.com/public/orderbook/";

  private String lastReceivedData = null;

  private Boolean asksPriceSortingSequence = true;
  private Boolean asksQuantitySortingSequence = true;
  private Boolean bidsPriceSortingSequence = true;
  private Boolean bidsQuantitySortingSequence = true;

  private ArrayList<HashMap<Integer, String>> returnData = new ArrayList<>();

  @GetMapping("/")
  public String index() {
    return "index";
  }

  @GetMapping("searchCoin")
  @ResponseBody
  public ArrayList<HashMap<Integer, String>> getBithumbAPI(@RequestParam("inputCoin") String orderCurrency, @RequestParam("payMent") String paymentCurrency) {

    if (orderCurrency == null || paymentCurrency == null) {
      throw  new NullPointerException();
    }
    final String API_URL = BASE_API_URL + orderCurrency + "_" + paymentCurrency;

    try {
      // API 조회
      URL url = new URL(API_URL);
      // 커넥션 연결
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      // 요청 방식은 GET
      connection.setRequestMethod("GET");
      // 데이터를 받아아ㅗ기 위해 inputStream 생성 및 charSet 설정
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
      // 받아온 데이터를 String에 담기
      String line = bufferedReader.readLine();

      // 받아온 데이터가 null이 아니라면 진행
      if (line != null) {
        // 정렬을 위해 데이터 저장
        lastReceivedData = line;
        System.out.println(line);

        // String 데이터 jsonParsing을 위해 파서 생성
        JSONParser jsonParser = new JSONParser();
        // jsonParser를 이용하여 String객체를 JsonObject로 만든다.
        JSONObject jsonObjectTotalApi = (JSONObject) jsonParser.parse(line);

        // jsonObject로 만든 데이터 안의 있는 object형태의 data를 꺼내온다.
        JSONObject jsonObjectDataApi = (JSONObject) jsonObjectTotalApi.get("data");

        // 꺼내온 데이터 중 asks와 bids를 각각 JsonArray에 담는다.
        JSONArray asksJsonArray = (JSONArray) jsonObjectDataApi.get("asks");
        JSONArray bidsJsonArray = (JSONArray) jsonObjectDataApi.get("bids");

        JSONObject object;

        // 각각의 데이터를 ArrayList에 Price, Quantity순으로 담는다.
        for (int asksIndex = 0; asksIndex < asksJsonArray.size(); asksIndex++) {
          object = (JSONObject) asksJsonArray.get(asksIndex);
          // 데이터를 하나 하나 꺼내 String에 담는다.
          String asksPrice = (String) object.get("price");
          String asksQuantity = (String) object.get("quantity");
          // String에 담았던 숫자를 int, Double로 변환
          int iAsksPrice = Integer.parseInt(asksPrice);
          Double dAsksQuantity = Double.valueOf(asksQuantity);

          // 천단위마다 ,(콤마) 추가
//          asksPriceHashMap.put(asksIndex, String.format("%,d", iAsksPrice));
//          asksQuantityHashMap.put(asksIndex, String.format("%,f", dAsksQuantity));
          asksPriceHashMap.put(asksIndex, asksPrice);
          asksQuantityHashMap.put(asksIndex, asksQuantity);
        }

        for (int bidsIndex = 0; bidsIndex < bidsJsonArray.size(); bidsIndex++) {
          object = (JSONObject) bidsJsonArray.get(bidsIndex);
          String bidsPrice = (String) object.get("price");
          String bidsQuantity = (String) object.get("quantity");
          int iBidsPrice = Integer.parseInt(bidsPrice);
          Double dBidsQuantity = Double.valueOf(bidsQuantity);

//          bidsPriceHashMap.put(bidsIndex, String.format("%,d", iBidsPrice));
//          bidsQuantityHashMap.put(bidsIndex, String.format("%,f", dBidsQuantity));
          bidsPriceHashMap.put(bidsIndex, bidsPrice);
          bidsQuantityHashMap.put(bidsIndex, bidsQuantity);
        }

        returnData.add(bidsPriceHashMap);
        returnData.add(bidsQuantityHashMap);
        returnData.add(asksPriceHashMap);
        returnData.add(asksQuantityHashMap);

      } else {
        throw  new NullPointerException();
      }
      
    } catch (MalformedURLException e) {
      System.out.println("URL을 확인해 주세요.");
    } catch (IOException e) {
      System.out.println("데이터를 확인해 주세요.");
    } catch (ParseException e) {
      System.out.println("JSON 파싱에 실패 하였였습니다.");
    }catch (NullPointerException e) {
      System.out.println("파라미터 값을 확인해 주세요.");
    }

    return returnData;
  }

  /**
   * standardData  매수 또는 매도
   * standardKinds 가격 또는 수량
   * sortSequence  오름차순 또는 내림차순
   * */
  @GetMapping("dataSort")
  @ResponseBody
  public ArrayList<HashMap<Integer, String>> dataSort(@RequestParam("targetData") String standardData,
                                                      @RequestParam("target") String standardKinds,
                                                      @RequestParam("sequence") String sortSequence) {

    ArrayList<SortPart> sortArrayList = new ArrayList();
    String price = null;
    String quantity = null;
    String sortKinds = null;

    /**
     * 구분 해야 하는 것
     * 매수인지 매도인지
     * 가격 기준인지 수량 기준인지
     * */
    for (int sortDataIndex = 0; sortDataIndex < 30; sortDataIndex++) {

      if (standardData.equals("bids")) {
        if (standardKinds.equals("price")) {
          sortKinds = "bidsPrice";
          price = bidsPriceHashMap.get(sortDataIndex);
          quantity = bidsQuantityHashMap.get(sortDataIndex);
        } else {
          sortKinds = "bidsQuantity";
          price = bidsPriceHashMap.get(sortDataIndex);
          quantity = bidsQuantityHashMap.get(sortDataIndex);
        }
      } else if (standardData.equals("asks")) {
        if (standardKinds.equals("price")) {
          sortKinds = "asksPrice";
          price = asksPriceHashMap.get(sortDataIndex);
          quantity = asksQuantityHashMap.get(sortDataIndex);
        } else {
          sortKinds = "asksQuantity";
          price = asksPriceHashMap.get(sortDataIndex);
          quantity = asksQuantityHashMap.get(sortDataIndex);
        }
      }

      sortArrayList.add(new SortPart(sortKinds, price, quantity));
    }
    // 오름차순인지, 내림차순인지 확인하고 sort를 호출한다.
    if (sortSequence.equals("ASC") || sortSequence.equals("asc")) {
      sortArrayList.sort(Comparator.naturalOrder());
//        Collections.sort(sortArrayList, new Desc);
    } else if (sortSequence.equals("DESC") || sortSequence.equals("desc")) {
      sortArrayList.sort(Comparator.reverseOrder());
//        Collections.sort(sortArrayList);
    }

    if (standardData.equals("bids")) {
      bidsPriceHashMap.clear();
      bidsQuantityHashMap.clear();
      int key = 0;
      // 정렬해서 가져온 데이터를 HashMap에 담는다.
      for (SortPart sortPart : sortArrayList) {
        bidsPriceHashMap.put(key, sortPart.getPrice());
        bidsQuantityHashMap.put(key, sortPart.getQuantity());
        key++;
      }
    } else {
      // 이미 데이터가 담겨있는 HashMap 초기화
      asksPriceHashMap.clear();
      asksQuantityHashMap.clear();

      int key = 0;
      // 정렬해서 가져온 데이터를 HashMap에 담는다.
      for (SortPart sortPart : sortArrayList) {
        asksPriceHashMap.put(key, sortPart.getPrice());
        asksQuantityHashMap.put(key, sortPart.getQuantity());
        key++;
      }
    }
    returnData.clear();

    returnData.add(bidsPriceHashMap);
    returnData.add(bidsQuantityHashMap);
    returnData.add(asksPriceHashMap);
    returnData.add(asksQuantityHashMap);

    return returnData;
  }
}
