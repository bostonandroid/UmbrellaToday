package org.bostonandroid.umbrellatoday;

import java.io.IOException;
import java.lang.ref.WeakReference;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.os.AsyncTask;
import android.sax.Element;
import android.sax.EndTextElementListener;
import android.sax.RootElement;

public class ReportRetriever extends AsyncTask<String, Void, Report> {
    public final static String TAG = "ReportRetriever";

    private final WeakReference<ReportConsumer> activityReference;

    public ReportRetriever(ReportConsumer activity) {
        this.activityReference = new WeakReference<ReportConsumer>(activity);
    }

    @Override
    protected Report doInBackground(String... urls) {
        Report report;

        if (urls.length > 0) {
            try {
                report = retrieveReport(urls[0]);
            } catch (ReportRetrieverException e) {
                report = null;
            }
        } else {
            report = null;
        }

        return report;
    }

    @Override
    protected void onPostExecute(Report report) {
        ReportConsumer consumer = activityReference.get();
        if (consumer != null) {
            consumer.consumeReport(report);
        }
    }

    private Report retrieveReport(String uri) throws ReportRetrieverException {
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
            reader.parse(uri);
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