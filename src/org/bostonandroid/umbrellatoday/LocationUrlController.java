package org.bostonandroid.umbrellatoday;

import android.content.Context;

class LocationUrlController {
  private boolean isAutolocate;
  private String location;
  private Context context;
  private Maybe<String> url;
 
  public LocationUrlController(Context c, SavedAlert a) {
    this.context = c;
    this.isAutolocate = a.isAutolocate();
    this.location = a.location();
  } 
  
  public Maybe<String> url() {
    location().perform(new ValueRunner<String>() {
      public void run(String location) {
        setUrl(new LocationUrlRetriever().url(location)); // TODO: Fix Java.
      }
    }).orElse(new Runnable() {
      public void run() {
        setUrl(new Nothing<String>());
      }
    });
    return this.url;
  }
  
  private void setUrl(Maybe<String> s) {
    this.url = s;
  }
  
  private boolean isAutolocate() {
    return this.isAutolocate;
  }
  
  private Context context() {
    return this.context;
  }
  
  private Maybe<String> location() {
    if (isAutolocate())
      return new LocationRetriever(context()).location();
    else
      return new Just<String>(this.location);
  }
}