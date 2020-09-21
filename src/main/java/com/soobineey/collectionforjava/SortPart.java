package com.soobineey.collectionforjava;

import static java.lang.Double.isFinite;

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
      return newCompareTo(this.price, current.price);
//      return Double.valueOf(this.price).compareTo(Double.valueOf(current.price));
    } else if (target.equals("asksQuantity")) {
      return newCompareTo(this.quantity, current.quantity);
//      return Double.valueOf(this.quantity).compareTo(Double.valueOf(current.quantity));
    } else if (target.equals("bidsPrice")) {
      return newCompareTo(this.price, current.price);
//      return Double.valueOf(this.price).compareTo(Double.valueOf(current.price));
    } else {
      return newCompareTo(this.quantity, current.quantity);
//      return Double.valueOf(this.quantity).compareTo(Double.valueOf(current.quantity));
    }

//    Collections.sort(list, Comparator.comparingInt(String::length).thenComparing(String::compareTo));
  }


  private int newCompareTo(String firstNum, String secondNum) {
    // 숫자의 길이를 확인 (둘다 0이면 같고 둘중의 하나가 0이면 0인수가 작은수)
    if (firstNum.length() == 0) {
      if (secondNum.length() == 0) {
        return 0;
      }
      return -1;
    } else if (secondNum.length() == 0) {
      return 1;
    }

    // 숫자가 유한한 소수 또는 숫자인지 확인한다.
    if (isFinite(Double.valueOf(firstNum))) {
      Double val1 = Double.parseDouble(firstNum);
      if (isFinite(Double.valueOf(secondNum))) {
        Double val2 = Double.parseDouble(secondNum);
        return Double.compare(val1, val2);
      } else {
        return firstNum.compareTo(secondNum);
      }
    } else {
      // 두개의 숫자 중 짧은 숫자의 길이를 저장
      int minVal = Math.min(firstNum.length(), secondNum.length());
      int sameCount = 0;

      // 포문을 돌며 같은 자리 다른 숫자가 나올때까지 확인한다.
      for (int i = 0; i < minVal; i++) {
        char leftVal = firstNum.charAt(i), rightVal = secondNum.charAt(i);
        if (leftVal == rightVal) {
          sameCount++;
        } else {
          break;
        }
      }
      // 같은 자리의 같은 숫자가 없다면 두개의 숫자를 compareTo를 이용하여 비교한다.
      if (sameCount == 0) {
        return firstNum.compareTo(secondNum);
      } else {
        // 같은 자리의 다른 숫자가 있다면 같은 숫자 자리까지 잘라서 저장
        String newStr1 = firstNum.substring(sameCount), newStr2 = secondNum.substring(sameCount);
        if (!newStr1.isEmpty() && !newStr2.isEmpty()) {
          if (Double.valueOf(newStr1).isInfinite() && Double.valueOf(newStr2).isInfinite()) {
            // 자른 숫자를 비교하여 크기를 비교, 결과를 리턴한다.
            return Double.compare(Double.valueOf(newStr1), Double.valueOf(newStr2));
          } else {
            return firstNum.compareTo(secondNum);
          }
        }
        return 0;
      }
    }
  }
}