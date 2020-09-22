package com.soobineey.collectionforjava;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;
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


  private ArrayList<HashMap<Integer, String>> returnData = new ArrayList<>();


  private final String BASE_API_URL = "https://api.bithumb.com/public/orderbook/";

  private final String UPPERCASE_ASC = "ASC";
  private final String LOWERCASE_ASC = "asc";
  private final String UPPERCASE_DESC = "DESC";
  private final String LOWERCASE_DESC = "desc";

  private final String BIDS = "bids";
  private final String ASKS = "asks";

  private final String PRICE = "price";
  private final String QUANTITY = "quantity";

  private String sortSequence = null;


  @GetMapping("/")
  public String index() {
    return "index";
  }

  @GetMapping("searchCoin")
  @ResponseBody
  public ArrayList<HashMap<Integer, String>> getBithumbAPI(@RequestParam("inputCoin") String orderCurrency, @RequestParam("payMent") String paymentCurrency) {
    returnData.clear();

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
        System.out.println(line);

        // String 데이터 jsonParsing을 위해 파서 생성
        JSONParser jsonParser = new JSONParser();
        // jsonParser를 이용하여 String객체를 JsonObject로 만든다.
        JSONObject jsonObjectTotalApi = (JSONObject) jsonParser.parse(line);

        // jsonObject로 만든 데이터 안의 있는 object형태의 data를 꺼내온다.
        JSONObject jsonObjectDataApi = (JSONObject) jsonObjectTotalApi.get("data");

        // 꺼내온 데이터 중 asks와 bids를 각각 JsonArray에 담는다.
        JSONArray asksJsonArray = (JSONArray) jsonObjectDataApi.get(ASKS);
        JSONArray bidsJsonArray = (JSONArray) jsonObjectDataApi.get(BIDS);

        JSONObject object;

        // 각각의 데이터를 ArrayList에 Price, Quantity순으로 담는다.
        for (int asksIndex = 0; asksIndex < asksJsonArray.size(); asksIndex++) {
          object = (JSONObject) asksJsonArray.get(asksIndex);
          // 데이터를 하나 하나 꺼내 String에 담는다.
          String asksPrice = (String) object.get(PRICE);
          String asksQuantity = (String) object.get(QUANTITY);
          // String에 담았던 숫자를 int, Double로 변환
//          int iAsksPrice = Integer.parseInt(asksPrice);
//          Double dAsksQuantity = Double.valueOf(asksQuantity);

          // 천단위마다 ,(콤마) 추가
//          asksPriceHashMap.put(asksIndex, String.format("%,d", iAsksPrice));
//          asksQuantityHashMap.put(asksIndex, String.format("%,f", dAsksQuantity));
          asksPriceHashMap.put(asksIndex, asksPrice);
          asksQuantityHashMap.put(asksIndex, asksQuantity);
        }

        for (int bidsIndex = 0; bidsIndex < bidsJsonArray.size(); bidsIndex++) {
          object = (JSONObject) bidsJsonArray.get(bidsIndex);
          String bidsPrice = (String) object.get(PRICE);
          String bidsQuantity = (String) object.get(QUANTITY);
//          int iBidsPrice = Integer.parseInt(bidsPrice);
//          Double dBidsQuantity = Double.valueOf(bidsQuantity);

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

    this.sortSequence = sortSequence;

    /**
     * 구분 해야 하는 것
     * 매수인지 매도인지
     * 가격 기준인지 수량 기준인지
     * */
    for (int sortDataIndex = 0; sortDataIndex < 30; sortDataIndex++) {

      if (standardData.equals(BIDS)) {
        if (standardKinds.equals(PRICE)) {
          sortKinds = "bidsPrice";
          price = bidsPriceHashMap.get(sortDataIndex);
          quantity = bidsQuantityHashMap.get(sortDataIndex);
        } else {
          sortKinds = "bidsQuantity";
          price = bidsPriceHashMap.get(sortDataIndex);
          quantity = bidsQuantityHashMap.get(sortDataIndex);
        }
      } else if (standardData.equals(ASKS)) {
        if (standardKinds.equals(PRICE)) {
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
    if (sortSequence.equals(UPPERCASE_ASC) || sortSequence.equals(LOWERCASE_ASC)) {
      sortArrayList.sort(Comparator.naturalOrder());
//        Collections.sort(sortArrayList, new Desc);
    } else if (sortSequence.equals(UPPERCASE_DESC) || sortSequence.equals(LOWERCASE_DESC)) {
      sortArrayList.sort(Comparator.reverseOrder());
//        Collections.sort(sortArrayList);
    }

    if (standardData.equals(BIDS)) {
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

@GetMapping("excel")
@ResponseBody
  public void excelDownload() {
    // 데이터를 담을 ArrayList를 만든다.
    ArrayList<HashMap<Integer, String>> list = new ArrayList<>();
    // 데이터를 리스트에 추가
    list.add(bidsPriceHashMap);
    list.add(bidsQuantityHashMap);
    list.add(asksPriceHashMap);
    list.add(asksQuantityHashMap);

    // 엑셀의 확장자 파이릉ㄹ 읽고 쓰는 컴포넌트
    XSSFWorkbook xssfWorkbook = new XSSFWorkbook();

    // 엑셀 내 시트 생성(시트 명)
    Sheet sheet = xssfWorkbook.createSheet("API");

    // 엑셀을 구성할 row와 cell을 준비
    Row row = null;
    Cell cell = null;

    // 라인 갯수를 위한 변수
    int rowIdx = 0;

    // row를 시트내에 하나씩 추가
    row = sheet.createRow(rowIdx++);

    // title용 arrayList생성
    ArrayList<String> title = new ArrayList<>();
    title.add("매수 거래량");
    title.add("매수 수량");
    title.add("매도 거래량");
    title.add("매도 수량");

    // title용 셀을 생성하고 위에 정해놓은 title데이터를 넣고 셀의 width를 정한다.
    for (int i = 0; i < title.size(); i++) {
      cell = row.createCell(i);
      cell.setCellValue(title.get(i));
      sheet.setColumnWidth(i, 20*256+200); // 셀사이즈 지정
    }

    // 실제 데이터를 넣는 for문
    for (int i = 0; i < 30; i++) {

      // 데이터를 넣을 row를 생성
      row = sheet.createRow(rowIdx++);
      int cellIdx = 0;

      // 데이터 출력
      // 데이터를 넣을 셀을 생성 및 list에 들어있는 데이터를 하나씩 꺼내서 넣는다.
      cell = row.createCell(cellIdx++);
      cell.setCellValue(list.get(0).get(i));

      cell = row.createCell(cellIdx++);
      cell.setCellValue(list.get(1).get(i));

      cell = row.createCell(cellIdx++);
      cell.setCellValue(list.get(2).get(i));

      cell = row.createCell(cellIdx++);
      cell.setCellValue(list.get(3).get(i));
    }

    // 엑셀파일을 저장할 위치 지정
    String path = "C://test/";
    String fileName = "test.xlsx";
    // File 객체를 생성하고 저장할 위치를 파라미터값으로 넘긴다.
    File xlsFile = new File(path+fileName);
    try {
      // 해당 경로로 파일을 내보내기 위해 객체를 생성한다.
      FileOutputStream fileOutputStream = new FileOutputStream(xlsFile);

      // 엑셀파일을 생성한다.
      xssfWorkbook.write(fileOutputStream);
    } catch (FileNotFoundException e) {
      System.out.println("파일을 찾을 수 없습니다.");
      return;
    } catch (IOException e) {
      System.out.println("파일을 저장하던중 오류가 발생했습니다.");
      return;
    }
  }

  @GetMapping("searchData")
  @ResponseBody
  public String searchData(@RequestParam("search") String data) {
    System.out.println(data);

    SortPart sortPart = new SortPart();
    // 매수 가격
    int beforeIndex = 0;
    int lastIndex = asksPriceHashMap.size() - 1;
    int index = 0;

    String findData = null;

    while (lastIndex - beforeIndex >= 0) {
      index = (beforeIndex + lastIndex) / 2;

      findData = asksPriceHashMap.get(index);
      int compareReturn = sortPart.stringCompareTo(data, findData);

      if (sortSequence.equals("ASC")) { // 오름차순일 경우
        if (compareReturn == 0) { // 찾는 데이터 검색 완료
          break;
        } else if (compareReturn == 1) {
          beforeIndex = index + 1;
        }else if (compareReturn == -1) {
          lastIndex = index - 1;
        }
      } else { // 내림차순일 경우
        if (compareReturn == 0) { // 찾는 데이터 검색 완료
          break;
        } else if (compareReturn == 1) {
          lastIndex = index - 1;
        }else if (compareReturn == -1) {
          beforeIndex = index + 1;
        }
      }
    }
    return findData;
  }
}