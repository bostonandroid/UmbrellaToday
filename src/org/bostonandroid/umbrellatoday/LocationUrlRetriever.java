package org.bostonandroid.umbrellatoday;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class LocationUrlRetriever {
  private Maybe<String> url;
 
  public Maybe<String> url(String location) {
    requestEntity(location).perform(new ValueRunner<StringEntity>() {
      public void run(StringEntity requestEntity) {
        httpResponse(httpXmlPostRequest(forecastsUrl(), requestEntity)).perform(new ValueRunner<HttpResponse>() {
          public void run(HttpResponse response) {
            if (isSuccessfulResponse(response))
              setUrl(locationHeader(response));
            else
              setUrl(new Nothing<String>());
          }
        }).orElse(new Runnable() {
          public void run() {
            setUrl(new Nothing<String>());
          }
        });
      }}).orElse(new Runnable() {
        public void run() {
          setUrl(new Nothing<String>());
        }
      });
    return this.url;
  }
  
  private void setUrl(Maybe<String> s) {
    this.url = s;
  }
  
  private Maybe<String> locationHeader(HttpResponse response) {
    Header redirectLocation = response.getFirstHeader("Location");
    if (redirectLocation == null)
      return new Nothing<String>();
    else
      return new Just<String>(redirectLocation.getValue() + ".xml");
  }

  private boolean isSuccessfulResponse(HttpResponse response) {
    return response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED;
  }
  
  private Maybe<HttpResponse> httpResponse(HttpPost post) {
    try {
      return new Just<HttpResponse>(new DefaultHttpClient().execute(post));
    } catch (IOException e) {
      post.abort();
      return new Nothing<HttpResponse>();
    }
  }
  
  private Maybe<StringEntity> requestEntity(String location) {
    try {
      return new Just<StringEntity>(new StringEntity("<forecast><location-name>"+location+"</location-name></forecast>"));
    } catch (UnsupportedEncodingException e) {
      return new Nothing<StringEntity>();
    }
  }
  
  private HttpPost httpXmlPostRequest(String url, StringEntity requestEntity) {
    HttpPost postRequest = new HttpPost(url);
    postRequest.addHeader("Accept", "text/xml");
    postRequest.addHeader("Content-Type", "text/xml");
    postRequest.addHeader("User-Agent", "Android Umbrella Today/1.0");
    postRequest.setEntity(requestEntity);
    return postRequest;
  }
  
  private String forecastsUrl() {
    return "http://umbrellatoday.com/forecasts";
  }
}
