package org.bostonandroid.umbrellatoday;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.SAXException;

import android.os.AsyncTask;
import android.sax.Element;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Log;
import android.util.Xml;

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
        if (report != null) {
            ReportConsumer consumer = activityReference.get();
            if (consumer != null) {
                consumer.consumeReport(report);
            }
        }
    }

    private Report retrieveReport(String uri) throws ReportRetrieverException {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet getRequest = new HttpGet(uri);

        final Report report = new Report();

        HttpResponse response;

        try {
            response = client.execute(getRequest);
        } catch (IOException e) {
            getRequest.abort();
            throw new ReportRetrieverException("Failed to execute GET request",
                    e);
        }

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_OK) {
            Log.w(TAG, "Error " + statusCode + " retrieving weather report.");
            // FIXME: throw or perhaps do something else?
            throw new ReportRetrieverException("Failed to communicate with UT");
        }

        HttpEntity entity = response.getEntity();

        if (entity != null) {
            InputStream content;

            try {
                content = entity.getContent();
            } catch (IOException e) {
                throw new ReportRetrieverException(
                        "Failed to read response body", e);
            } catch (IllegalStateException e) {
                throw new ReportRetrieverException(
                        "Failed to read response body", e);
            }

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

            try {
                Xml.parse(content, Xml.Encoding.UTF_8, root.getContentHandler());
            } catch (IOException e) {
                throw new ReportRetrieverException("Unable to parse report", e);
            } catch (SAXException e) {
                throw new ReportRetrieverException("Unable to parse report", e);
            }
        }

        return report;
    }
}