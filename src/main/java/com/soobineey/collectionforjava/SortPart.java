package com.soobineey.collectionforjava;

import java.util.Collections;
import java.util.Comparator;

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
      return Double.valueOf(this.price).compareTo(Double.valueOf(current.price));
    } else if (target.equals("asksQuantity")) {
      return Double.valueOf(this.quantity).compareTo(Double.valueOf(current.quantity));
    }else if (target.equals("bidsPrice")) {
      return Double.valueOf(this.price).compareTo(Double.valueOf(current.price));
    } else {
      return Double.valueOf(this.quantity).compareTo(Double.valueOf(current.quantity));
    }

//    Collections.sort(list, Comparator.comparingInt(String::length).thenComparing(String::compareTo));
  }
}