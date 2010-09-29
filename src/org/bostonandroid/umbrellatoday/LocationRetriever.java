package org.bostonandroid.umbrellatoday;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

public class LocationRetriever {
  private Context context;
  private Location locationCache;

  public LocationRetriever(Context c) {
    this.context = c;
  }

  public Maybe<String> location() {
    if (locationManager().isProviderEnabled(provider()))
      return new Just<String>(lastKnownLocation().getLatitude() + "," + lastKnownLocation().getLongitude());
    else
      return new Nothing<String>();
  }
  
  private Context context() { return this.context; }
  
  private LocationManager locationManager() {
    return (LocationManager)context().getSystemService(Context.LOCATION_SERVICE);
  }
  
  private String provider() {
    return LocationManager.NETWORK_PROVIDER;
  }
  
  private Location lastKnownLocation() {
    if (this.locationCache == null)
      this.locationCache = locationManager().getLastKnownLocation(provider());
    return this.locationCache;
  }
}