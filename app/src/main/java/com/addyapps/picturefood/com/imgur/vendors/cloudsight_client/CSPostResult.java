package com.addyapps.picturefood.com.imgur.vendors.cloudsight_client;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public class CSPostResult extends GenericJson {
  @Key
  private String token;

  @Key
  private String url;

  public String getToken() {
    return token;
  }

  public String getUrl() {
    return url;
  }
}
