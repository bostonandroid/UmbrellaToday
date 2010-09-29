package org.bostonandroid.umbrellatoday;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class AboutUmbrellaToday extends Activity
{
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.about);
    TextView tv = (TextView) findViewById(R.id.about);
    tv.setText(Html.fromHtml(getString(R.string.about_text)));
    tv.setMovementMethod(LinkMovementMethod.getInstance());
  }
}
