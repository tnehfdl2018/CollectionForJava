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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

@Controller
public class MainController implements SortAndSearch {
  private HashMap<Integer, String> asksPriceHashMap = new HashMap<>();
  private HashMap<Integer, String> asksQuantityHashMap = new HashMap<>();
  private HashMap<Integer, String> bidsPriceHashMap = new HashMap<>();
  private HashMap<Integer, String> bidsQuantityHashMap = new HashMap<>();

  private ArrayList<HashMap<Integer, String>> returnData = new ArrayList<>();

  private static final String BASE_API_URL = "https://api.bithumb.com/public/orderbook/";

  private static final String UPPERCASE_ASC = "ASC";
  private static final String LOWERCASE_ASC = "asc";
  private static final String UPPERCASE_DESC = "DESC";
  private static final String LOWERCASE_DESC = "desc";

  private static final String BIDS = "bids";
  private static final String ASKS = "asks";

  private static final String BIDS_PRICE = "bidsPrice";
  private static final String BIDS_QUANTITY = "bidsQuantity";
  private static final String ASKS_PRICE = "asksPrice";
  private static final String ASKS_QUANTITY = "asksQuantity";

  private static final String PRICE = "price";
  private static final String QUANTITY = "quantity";

  private String sortSequence = null;


  @GetMapping("/")
  public String index() {
    return "index";
  }

  @GetMapping("searchCoin")
  @ResponseBody
  public ArrayList<HashMap<Integer, String>> getBithumbAPI(@RequestParam("inputCoin") String orderCurrency, @RequestParam("payMent") String paymentCurrency) {
    returnData.clear();

    HashMap<Integer, String> commaBidsPrice = new HashMap<>();
    HashMap<Integer, String> commaBidsQuantity = new HashMap<>();
    HashMap<Integer, String> commaAsksPrice = new HashMap<>();
    HashMap<Integer, String> commaAsksQuantity = new HashMap<>();

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
      // 데이터를 받아오기 위해 inputStream 생성 및 charSet 설정
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

//        DecimalFormat format = new DecimalFormat("###,###");

        // 각각의 데이터를 ArrayList에 Price, Quantity순으로 담는다.
        for (int asksIndex = 0; asksIndex < asksJsonArray.size(); asksIndex++) {
          object = (JSONObject) asksJsonArray.get(asksIndex);
          // 데이터를 하나 하나 꺼내 String에 담는다.
          String asksPrice = (String) object.get(PRICE);
          String asksQuantity = (String) object.get(QUANTITY);
          // 데이터를 다룰 해쉬맵에 저장
          asksPriceHashMap.put(asksIndex, asksPrice);
          asksQuantityHashMap.put(asksIndex, asksQuantity);
//          commaAsksPrice.put(asksIndex, format.format(asksPrice));
//          commaAsksQuantity.put(asksIndex, format.format(asksQuantity));
        }

        for (int bidsIndex = 0; bidsIndex < bidsJsonArray.size(); bidsIndex++) {
          object = (JSONObject) bidsJsonArray.get(bidsIndex);
          String bidsPrice = (String) object.get(PRICE);
          String bidsQuantity = (String) object.get(QUANTITY);
          bidsPriceHashMap.put(bidsIndex, bidsPrice);
          bidsQuantityHashMap.put(bidsIndex, bidsQuantity);
//          commaBidsPrice.put(bidsIndex, format.format(bidsPrice));
//          commaBidsQuantity.put(bidsIndex, format.format(bidsQuantity));
        }

        // 4가지의 데이터를 담은 HashMap을 arrayList에 순서대로 담는다.
        returnData.add(bidsPriceHashMap);
        returnData.add(bidsQuantityHashMap);
        returnData.add(asksPriceHashMap);
        returnData.add(asksQuantityHashMap);

//        returnData.add(commaAsksPrice);
//        returnData.add(commaAsksQuantity);
//        returnData.add(commaBidsPrice);
//        returnData.add(commaBidsQuantity);

      } else {
        throw  new NullPointerException();
      }
    } catch (MalformedURLException e) {
      System.out.println("URL을 확인해 주세요.");
      return null;
    } catch (IOException e) {
      System.out.println("데이터를 확인해 주세요.");
      return null;
    } catch (ParseException e) {
      System.out.println("JSON 파싱에 실패 하였였습니다.");
      return null;
    }catch (NullPointerException e) {
      System.out.println("파라미터 값을 확인해 주세요.");
      return null;
    }
    return returnData;
  }

  /**
   * standardData  매수 또는 매도
   * standardKinds 가격 또는 수량
   * sortSequence  오름차순 또는 내림차순
   * */
//  @GetMapping("dataSort")
//  @ResponseBody
//  public ArrayList<HashMap<Integer, String>> dataSort(@RequestParam("targetData") String standardData,
//                                                      @RequestParam("target") String standardKinds,
//                                                      @RequestParam("sequence") String sortSequence) {
//
//    ArrayList<SortPart> sortArrayList = new ArrayList();
//    String price = null;
//    String quantity = null;
//    String sortKinds = null;
//
//    // 검색시 오름차순과 내림차순 구분을 위해 저장
//    this.sortSequence = sortSequence;
//
//    /**
//     * 구분 해야 하는 것
//     * 매수인지 매도인지
//     * 가격 기준인지 수량 기준인지
//     * */
//    for (int sortDataIndex = 0; sortDataIndex < 30; sortDataIndex++) {
//
//      if (standardData.equals(BIDS)) {
//        if (standardKinds.equals(PRICE)) {
//          sortKinds = BIDS_PRICE;
//          price = bidsPriceHashMap.get(sortDataIndex);
//          quantity = bidsQuantityHashMap.get(sortDataIndex);
//        } else {
//          sortKinds = BIDS_QUANTITY;
//          price = bidsPriceHashMap.get(sortDataIndex);
//          quantity = bidsQuantityHashMap.get(sortDataIndex);
//        }
//      } else if (standardData.equals(ASKS)) {
//        if (standardKinds.equals(PRICE)) {
//          sortKinds = ASKS_PRICE;
//          price = asksPriceHashMap.get(sortDataIndex);
//          quantity = asksQuantityHashMap.get(sortDataIndex);
//        } else {
//          sortKinds = ASKS_QUANTITY;
//          price = asksPriceHashMap.get(sortDataIndex);
//          quantity = asksQuantityHashMap.get(sortDataIndex);
//        }
//      }
//
//      sortArrayList.add(new SortPart(sortKinds, price, quantity));
//    }
//    // 오름차순인지, 내림차순인지 확인하고 sort를 호출한다.
//    if (sortSequence.equals(UPPERCASE_ASC) || sortSequence.equals(LOWERCASE_ASC)) {
//      sortArrayList.sort(Comparator.naturalOrder());
//    } else if (sortSequence.equals(UPPERCASE_DESC) || sortSequence.equals(LOWERCASE_DESC)) {
//      sortArrayList.sort(Comparator.reverseOrder());
//    }
//
//    if (standardData.equals(BIDS)) {
//      bidsPriceHashMap.clear();
//      bidsQuantityHashMap.clear();
//      int key = 0;
//      // 정렬해서 가져온 데이터를 HashMap에 담는다.
//      for (SortPart sortPart : sortArrayList) {
//        bidsPriceHashMap.put(key, sortPart.getPrice());
//        bidsQuantityHashMap.put(key, sortPart.getQuantity());
//        key++;
//      }
//    } else {
//      // 이미 데이터가 담겨있는 HashMap 초기화
//      asksPriceHashMap.clear();
//      asksQuantityHashMap.clear();
//
//      int key = 0;
//      // 정렬해서 가져온 데이터를 HashMap에 담는다.
//      for (SortPart sortPart : sortArrayList) {
//        asksPriceHashMap.put(key, sortPart.getPrice());
//        asksQuantityHashMap.put(key, sortPart.getQuantity());
//        key++;
//      }
//    }
//    returnData.clear();
//
//    returnData.add(bidsPriceHashMap);
//    returnData.add(bidsQuantityHashMap);
//    returnData.add(asksPriceHashMap);
//    returnData.add(asksQuantityHashMap);
//
//    return returnData;
//  }

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
    String fileName = "order_book.xlsx";
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
  public String searchData(@RequestParam("searchKinds") String findKinds, @RequestParam("search") String data) {
    System.out.println(data);

    // 찾으려는 데이터가 어떤 컬럼인지 구분하고 데이터와 함께 검색메소드를 호출한다.
    if (findKinds.equals(ASKS_PRICE)) {
      return findNumber(asksPriceHashMap, data);

    } else if (findKinds.equals(ASKS_QUANTITY)) {
      return findNumber(asksQuantityHashMap, data);

    } else if (findKinds.equals(BIDS_PRICE)) {
      return findNumber(bidsPriceHashMap, data);

    } else {
      return findNumber(bidsQuantityHashMap, data);
    }
  }

  /**
   * hashData - 검색할 데이터가 들어있는 HashMap
   * requestFindData - 조회를 요청한 데이터
   * */
  public String findNumber(HashMap<Integer, String> hashData, String requestFindData) {

    SortPart sortPart = new SortPart();
    // binary sorting을 위한 인덱스 포인트 3개
    int beforeIndex = 0;
    int lastIndex = hashData.size() - 1;
    int realIndex = 0;

    // 최종 리턴 데이터
    String findReturnData = null;

    // lastIndex - beforeIndex >= 0가 false이면 더이상 검색할 인덱스가 없다.
    while (lastIndex - beforeIndex >= 0) {

      // 데이터 인덱스값을 중간으로 지정한다.
      realIndex = (beforeIndex + lastIndex) / 2;

      // 인덱스 값에 들어있는 value를 꺼낸다.
      findReturnData = hashData.get(realIndex);
      // 크기를 비교한다.
      int compareReturn = sortPart.stringCompareTo(requestFindData, findReturnData);
      // 조회한 숫자와 인덱스 값이 같으면 0을 리턴
      if (compareReturn == 0)
        break;

      // 정렬순서가 오름차순일 경우
      if (sortSequence.equals(UPPERCASE_ASC)) {
        // 조회한 값이 인덱스 값보다 클경우 1을 리턴
        // 앞쪽의 인덱스 포인터를 현재 인덱스 + 1의 위치로 가져와 재 검색한다.
        if (compareReturn == 1) {
          beforeIndex = realIndex + 1;
          // 조회한 값이 인덱스 값보다 작을경우 -1을 리턴
          // 뒤쪽의 인덱스 포인터를 현재 인덱스 - 1의 위치로 가져와 재 검색한다.
        } else {
          lastIndex = realIndex - 1;
        }
        // 정렬순서가 내림차순일 경우
      } else {
        if (compareReturn == 1) {
          lastIndex = realIndex - 1;
        } else {
          beforeIndex = realIndex + 1;
        }
      }
    }
    // 조회한 데이터가 없으면 err을 반환한다.
    if (findReturnData.equals(requestFindData)) {
      return findReturnData;
    } else {
      // 선형 검색
      for (int searchIndex = 0; searchIndex < hashData.size(); searchIndex++) {
        if (requestFindData.equals(hashData.get(searchIndex))) {
          findReturnData = hashData.get(searchIndex);
          return findReturnData;
        }
      }
      return "err";
    }
  }

//  @Override
//  public ArrayList<HashMap<Integer, String>> sort(String standardData, String standardKinds, String sortSequence) {
//
//    // 검색시 오름차순과 내림차순 구분을 위해 저장
//    this.sortSequence = sortSequence;
//
//    HashMap<Integer, String> mainHashMap = new HashMap<>();
//    HashMap<Integer, String> subHashMap = new HashMap<>();
//
//    /**
//     * 구분 해야 하는 것
//     * 매수인지 매도인지
//     * 가격 기준인지 수량 기준인지
//     * */
//    for (int sortDataIndex = 0; sortDataIndex < 30; sortDataIndex++) {
//
//      if (standardData.equals(BIDS)) {
//        if (standardKinds.equals(PRICE)) {
//          mainHashMap = bidsPriceHashMap;
//          subHashMap = bidsQuantityHashMap;
//        } else {
//          subHashMap = bidsPriceHashMap;
//          mainHashMap = bidsQuantityHashMap;
//        }
//      } else if (standardData.equals(ASKS)) {
//        if (standardKinds.equals(PRICE)) {
//          mainHashMap = asksPriceHashMap;
//          subHashMap = asksQuantityHashMap;
//        } else {
//          subHashMap = asksPriceHashMap;
//          mainHashMap = asksQuantityHashMap;
//        }
//      }
//    }
//
//    return quickSort(mainHashMap,0, mainHashMap.size()-1, subHashMap, sortSequence);
//  }

  /**
   * 퀵 정렬
   * */

  public ArrayList<HashMap<Integer, String>> quickSort(HashMap<Integer, String> mainHash, int lValuePoints, int rValuePoints, HashMap<Integer, String> subHash, String sortSequence) {
    int sortSequenceForLValue = 0;
    int sortSequenceForRValue = 0;

    // 오름차순인지, 내림차순인지 확인하고 sort를 호출한다.
    if (sortSequence.equals(UPPERCASE_ASC) || sortSequence.equals(LOWERCASE_ASC)) {
      sortSequenceForLValue = -1;
      sortSequenceForRValue = 1;

    } else if (sortSequence.equals(UPPERCASE_DESC) || sortSequence.equals(LOWERCASE_DESC)) {
      sortSequenceForLValue = 1;
      sortSequenceForRValue = -1;

    }

    // 스트링끼리 비교를 위해 메소드를 호출할 객체 생성
    SortPart sortPart = new SortPart();

    // 데이터를 담을 임시변수
    String mainTemp = null;
    String subTemp = null;

    // 양쪽의 인덱스와 피벗 생성
    int lValuePoint = lValuePoints;
    int rValuePoint = rValuePoints;
    String pivot = mainHash.get((lValuePoint + rValuePoint) / 2);

    do {
      // 왼쪽의 값이 피벗값보다 클때까지 반복하여 확인한다.
      while (sortPart.stringCompareTo(mainHash.get(lValuePoint), pivot) == sortSequenceForLValue) {
        lValuePoint++;
      }
      // 오른쪽의 값이 피벗값보다 작을때까지 반복하여 확인한다.
      while (sortPart.stringCompareTo(mainHash.get(rValuePoint), pivot) == sortSequenceForRValue) {
        rValuePoint--;
      }

      // 왼쪽 인덱스가 오른족 인덱스보다 앞에 있으면 진행
      if (lValuePoint <= rValuePoint) {
        // 지정된 인덱스에 있는 값을 꺼낸다.
        mainTemp = mainHash.get(rValuePoint);
        subTemp = subHash.get(rValuePoint);

        // 각 인덱스의 값은 서로 바꾼다.
        mainHash.put(rValuePoint, mainHash.get(lValuePoint));
        subHash.put(rValuePoint, subHash.get(lValuePoint));
        mainHash.put(lValuePoint, mainTemp);
        subHash.put(lValuePoint, subTemp);
        // 다음 인덱스 진행을 위해 각 좌우 밸류에 증감을 한다.
        lValuePoint++;
        rValuePoint--;
      }

      // 왼쪽 인덱스가 오른족 인덱스보다 앞에 있으면 진행
      // false일 경우 한번의 사이클이 끝
    } while (lValuePoint <= rValuePoint);

    // 정렬을 하기위한 재귀 함수 호출
    if (lValuePoints < rValuePoint) quickSort(mainHash, lValuePoints, rValuePoint, subHash, sortSequence);
    if (lValuePoint < rValuePoints) quickSort(mainHash, lValuePoint, rValuePoints, subHash, sortSequence);
    
    returnData.add(mainHash);
    returnData.add(subHash);

    return returnData;
  }

  /**
   * 버블 정렬
   * */
  @Override
  public ArrayList<HashMap<Integer, String>> sort(String standardData, String standardKinds, String sortSequence) {

    // 검색시 오름차순과 내림차순 구분을 위해 저장
    this.sortSequence = sortSequence;

    HashMap<Integer, String> mainHashMap = new HashMap<>();
    HashMap<Integer, String> subHashMap = new HashMap<>();

    // 문자열의 크기를 비교하기 위한 메소드가  있는 클래스 생성
    SortPart sortPart = new SortPart();
    String mainTemp = null; // 임시변수
    String subTemp = null;

    int sortMethod = 0;

    /**
     * 구분 해야 하는 것
     * 매수인지 매도인지
     * 가격 기준인지 수량 기준인지
     * */
    for (int sortDataIndex = 0; sortDataIndex < 30; sortDataIndex++) {

      if (standardData.equals(BIDS)) {
        if (standardKinds.equals(PRICE)) {
          mainHashMap = bidsPriceHashMap;
          subHashMap = bidsQuantityHashMap;
        } else {
          subHashMap = bidsPriceHashMap;
          mainHashMap = bidsQuantityHashMap;
        }
      } else if (standardData.equals(ASKS)) {
        if (standardKinds.equals(PRICE)) {
          mainHashMap = asksPriceHashMap;
          subHashMap = asksQuantityHashMap;
        } else {
          subHashMap = asksPriceHashMap;
          mainHashMap = asksQuantityHashMap;
        }
      }
    }
    // 오름차순인지, 내림차순인지 확인하고 sort를 호출한다.
    if (sortSequence.equals(UPPERCASE_ASC) || sortSequence.equals(LOWERCASE_ASC)) {
      sortMethod = 1;

    } else if (sortSequence.equals(UPPERCASE_DESC) || sortSequence.equals(LOWERCASE_DESC)) {
      sortMethod = -1;

    }

    // 받아온 해쉬맵의 크기만큼 for문 실행
    for (int oIndex = 0; oIndex < mainHashMap.size(); oIndex++) {
      // 안쪽 for문은 실제 데이터를 바꾸기 위한 포문
      // j < hash.size()-value1-1는 불필요한 for문 실행을 줄여주기 위한 조건문
      // 해쉬맵의 크기에서 이미 정렬이 된 갯수(value1)만큼 빼고 크기는 인덱스보다 1이 크므로 1을 더 빼준다.
      for (int iIndex = 0; iIndex < mainHashMap.size()-oIndex-1; iIndex++) {
        if (sortPart.stringCompareTo(mainHashMap.get(iIndex), mainHashMap.get(iIndex+1)) == sortMethod) {
          mainTemp = mainHashMap.get(iIndex);
          mainHashMap.put(iIndex, mainHashMap.get(iIndex+1));
          mainHashMap.put(iIndex+1, mainTemp);

          subTemp = subHashMap.get(iIndex);
          subHashMap.put(iIndex, subHashMap.get(iIndex+1));
          subHashMap.put(iIndex+1, subTemp);
        }
      }
    }
    returnData.add(mainHashMap);
    returnData.add(subHashMap);

    return returnData;
  }

  /**
   * 바이너리 서치
   * */
//  @Override
//  public String search(HashMap<Integer, String> hash, String requstValue) {
//
//    SortPart sortPart = new SortPart();
//    // binary sorting을 위한 인덱스 포인트 3개
//    int beforeIndex = 0;
//    int lastIndex = hash.size() - 1;
//    int realIndex = 0;
//
//    // 최종 리턴 데이터
//    String findReturnData = null;
//
//    // lastIndex - beforeIndex >= 0가 false이면 더이상 검색할 인덱스가 없다.
//    while (lastIndex - beforeIndex >= 0) {
//
//      // 데이터 인덱스값을 중간으로 지정한다.
//      realIndex = (beforeIndex + lastIndex) / 2;
//
//      // 인덱스 값에 들어있는 value를 꺼낸다.
//      findReturnData = hash.get(realIndex);
//      // 크기를 비교한다.
//      int compareReturn = sortPart.stringCompareTo(requstValue, findReturnData);
//      // 조회한 숫자와 인덱스 값이 같으면 0을 리턴
//      if (compareReturn == 0)
//        break;
//
//      // 정렬순서가 오름차순일 경우
//      if (sortSequence.equals(UPPERCASE_ASC)) {
//        // 조회한 값이 인덱스 값보다 클경우 1을 리턴
//        // 앞쪽의 인덱스 포인터를 현재 인덱스 + 1의 위치로 가져와 재 검색한다.
//        if (compareReturn == 1) {
//          beforeIndex = realIndex + 1;
//          // 조회한 값이 인덱스 값보다 작을경우 -1을 리턴
//          // 뒤쪽의 인덱스 포인터를 현재 인덱스 - 1의 위치로 가져와 재 검색한다.
//        } else {
//          lastIndex = realIndex - 1;
//        }
//        // 정렬순서가 내림차순일 경우
//      } else {
//        if (compareReturn == 1) {
//          lastIndex = realIndex - 1;
//        } else {
//          beforeIndex = realIndex + 1;
//        }
//      }
//    }
//    // 조회한 데이터가 없으면 err을 반환한다.
//    if (findReturnData.equals(requstValue)) {
//      return findReturnData;
//    } else {
//      // 선형 검색
//      for (int searchIndex = 0; searchIndex < hash.size(); searchIndex++) {
//        if (requstValue.equals(hash.get(searchIndex))) {
//          findReturnData = hash.get(searchIndex);
//          return findReturnData;
//        }
//      }
//      return "err";
//    }
//  }

  /**
   * 시퀀스 서치
   * */

  @Override
  public String search(HashMap<Integer, String> hash, String requstValue) {
    String findReturnData = null;
    for (int searchIndex = 0; searchIndex < hash.size(); searchIndex++) {
      if (requstValue.equals(hash.get(searchIndex))) {
        findReturnData = hash.get(searchIndex);
        return findReturnData;
      }
    }
    return "err";
  }


  /** 
   * sort 호출 메소드
   * */
  @GetMapping("dataSort")
  @ResponseBody
  public ArrayList<HashMap<Integer, String>> testSort(@RequestParam("targetData") String standardData,
                                                      @RequestParam("target") String standardKinds,
                                                      @RequestParam("sequence") String sortSequence) {

    return sort(standardData, standardKinds, sortSequence);


  }

  /**
   * search 호출 메소드 
   * */
  @GetMapping("searchTest")
  @ResponseBody
  public String testSearch(@RequestParam("searchKinds") String findKinds, @RequestParam("search") String data) {

    // 찾으려는 데이터가 어떤 컬럼인지 구분하고 데이터와 함께 검색메소드를 호출한다.
    if (findKinds.equals(ASKS_PRICE)) {
      return search(asksPriceHashMap, data);

    } else if (findKinds.equals(ASKS_QUANTITY)) {
      return search(asksQuantityHashMap, data);

    } else if (findKinds.equals(BIDS_PRICE)) {
      return search(bidsPriceHashMap, data);

    } else {
      return search(bidsQuantityHashMap, data);
    }
  }


}