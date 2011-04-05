package org.bostonandroid.umbrellatoday;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class AboutActivity extends Activity
{
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.about);
    WebView webView = (WebView) findViewById(R.id.about);
    webView.loadUrl("file:///android_asset/about.html");
  }
}
