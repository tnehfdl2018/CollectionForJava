package com.soobineey.collectionforjava;

public class SortPart implements Comparable<SortPart> {
  private String price = null;
  private String quantity = null;
  private String target = null;

  // 한 줄에 보여줄 4개의 데이터를 초기화 시킨다.
  public SortPart(String target, String price, String quantity) {
    this.target = target;
    this.price = price;
    this.quantity = quantity;
  }

  public SortPart() {}

  public String getPrice() {
    return price;
  }

  public String getQuantity() {
    return quantity;
  }

  // 실제 sort되는 제공 메소드
  @Override
  public int compareTo(SortPart current) {
    // if문으로 sorting의 기준을 판단하고 sort를 진행한다.
    if (target.equals("asksPrice")) {
      return stringCompareTo(this.price, current.price);
    } else if (target.equals("asksQuantity")) {
      return stringCompareTo(this.quantity, current.quantity);
    } else if (target.equals("bidsPrice")) {
      return stringCompareTo(this.price, current.price);
    } else {
      return stringCompareTo(this.quantity, current.quantity);
    }
  }

  public int stringCompareTo(String firstNum, String secondNum) {
    int pointIndex = 0;
    String firstWholeNumber = null;
    String secondWholeNumber = null;
    String firstRationalNumber = null;
    String secondRationalNumber = null;

    // .을 찾아 구분하여 자연수 부분과 소수점 아래 부분을 나눈다.
    if (firstNum.indexOf('.') != -1) {
      // 첫번째 숫자의 . 위치 확인 및 나누기
      pointIndex = firstNum.indexOf('.');
      firstWholeNumber = firstNum.substring(0, pointIndex);
      firstRationalNumber = firstNum.substring(pointIndex);

      if (secondNum.indexOf('.') != -1) {
        // 두번째 숫자의 . 위치 확인 및 나누기
        pointIndex = secondNum.indexOf('.');
        secondWholeNumber = secondNum.substring(0, pointIndex);
        secondRationalNumber = secondNum.substring(pointIndex);
      } else {
        // 소수가 아니라면 정수 전체 그대로 저장
        secondWholeNumber = secondNum;
      }
    } else {
      // 첫번째 숫자가 소수가 아니라면 정수 전체 그대로 저장
      firstWholeNumber = firstNum;
      if (secondNum.indexOf('.') != -1) {
        // 두번째 숫자의 . 위치 확인 및 나누기
        pointIndex = secondNum.indexOf('.');
        secondWholeNumber = secondNum.substring(0, pointIndex);
        secondRationalNumber = secondNum.substring(pointIndex);
      } else {
        // 소수가 아니라면 정수 전체 그대로 저장
        secondWholeNumber = secondNum;
      }
    }
    // 길이가 같은지 비교
    if (firstWholeNumber.length() == secondWholeNumber.length()) {
      char firstNumChar;
      char secondNumChar;

      // 길이가 같다면 각각의 인덱스를 1대1로 비교
      for (int dIdx = 0; dIdx < firstWholeNumber.length(); dIdx++) {
        firstNumChar = firstWholeNumber.charAt(dIdx);
        secondNumChar = secondWholeNumber.charAt(dIdx);
        // 각각의 인덱스 비교중 값이 다르다면 큰값에 대해 리턴한다.
        if (firstNumChar != secondNumChar) {
          return firstNumChar > secondNumChar ? 1 : -1;
        }
      }
      // 소숫점 아래 숫자의 갯수가 같다면 1대1 매칭 비교
      int rationalNumLength = 0;
      // 소수점이 없으면 없는 인자값은 무조건 작은 수 이기때문에 확인한다.
      if (firstRationalNumber == null) {
        return -1;
      } else if (secondRationalNumber == null) {
        return 1;
      }
      rationalNumLength = firstRationalNumber.length() > secondRationalNumber.length() ? firstRationalNumber.length() : secondRationalNumber.length();

       // 길이가 긴 문자열의 길이만큼 for문을 돌면서 비교
        for (int dIdx = 0; dIdx < rationalNumLength; dIdx++) {
          if (firstRationalNumber.length() > dIdx) {
            // 만약 인덱스 값이 문자열의 길이보다 길어진다면 비교할 charactor에는 0 대입
            firstNumChar = firstRationalNumber.charAt(dIdx);
          } else {
            firstNumChar = '0';
          }
          if (secondRationalNumber.length() > dIdx) {
            secondNumChar = secondRationalNumber.charAt(dIdx);
          } else {
            secondNumChar = '0';
          }
          // 각각의 인덱스 비교중 값이 다르다면 큰값에 대해 리턴한다.
          if (firstNumChar != secondNumChar) {
            return firstNumChar > secondNumChar ? 1 : -1;
          }
        }
      // for문을 빠져나가지 못했다면 값이 같음으로 0을 리턴
      return 0;
    } else {
      // 길이가 같지 않을 때 처리 로직
      return firstWholeNumber.length() > secondWholeNumber.length() ? 1 : -1;
    }
  }
}