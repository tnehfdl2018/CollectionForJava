package com.soobineey.collectionforjava;

import java.util.ArrayList;
import java.util.HashMap;

public interface SortAndSearch {


  public ArrayList<HashMap<Integer, String>> sort(String standardData, String standardKinds, String sortSequence);

  public String search(HashMap<Integer, String> hash, String requstValue);




}
