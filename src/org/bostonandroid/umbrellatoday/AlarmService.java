package org.bostonandroid.umbrellatoday;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.sax.Element;
import android.sax.EndTextElementListener;
import android.sax.RootElement;

public class AlarmService extends IntentService {

    public final static String TAG = "AlarmService";

    private NotificationManager notificationManager;

    public AlarmService() {
        super("AlarmService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ReportRetriever retriever = new ReportRetriever(intent.getDataString());
        Report report;
        try {
            report = retriever.retrieveReport();
        } catch (ReportRetrieverException e) {
            report = null;
        }
        if (report != null) {
            String answer = report.getAnswer();
            if (answer.equals("yes")) {
                showNotification("Rain");
            } else if (answer.equals("snow")) {
                showNotification("Snow");
            }
        }
    }

    private void showNotification(String message) {
        String appName = getString(R.string.app_name);
        Notification notification = new Notification(
                R.drawable.notification_icon, appName, System
                        .currentTimeMillis());
        notification.setLatestEventInfo(AlarmService.this, appName, message,
                null);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(1, notification);
    }

    private class ReportRetriever {

        private String url;

        ReportRetriever(String url) {
            this.url = url;
        }

        public Report retrieveReport() throws ReportRetrieverException {
            final Report report = new Report();

            RootElement root = new RootElement("forecast");
            root.getChild("answer").setEndTextElementListener(
                    new EndTextElementListener() {
                        public void end(String body) {
                            report.setAnswer(body);
                        }
                    });
            Element location = root.getChild("location");
            location.getChild("name").setEndTextElementListener(
                    new EndTextElementListener() {
                        public void end(String body) {
                            report.setLocationName(body);
                        }
                    });

            SAXParserFactory factory = SAXParserFactory.newInstance();

            try {
                SAXParser parser = factory.newSAXParser();
                XMLReader reader = parser.getXMLReader();
                reader.setContentHandler(root.getContentHandler());
                reader.parse(url);
            } catch (ParserConfigurationException e) {
                throw new ReportRetrieverException("Unable to parse report", e);
            } catch (SAXException e) {
                throw new ReportRetrieverException("Unable to parse report", e);
            } catch (IOException e) {
                throw new ReportRetrieverException("Unable to parse report", e);
            }

            return report;
        }
    }
}
