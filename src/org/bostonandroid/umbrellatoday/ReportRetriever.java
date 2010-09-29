package org.bostonandroid.umbrellatoday;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.sax.Element;
import android.sax.EndTextElementListener;
import android.sax.RootElement;

class ReportRetriever {

  private String url;

  ReportRetriever(String url) {
    this.url = url;
  }

  public Maybe<Report> retrieveReport() {
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
      return new Just<Report>(report);
    } catch (ParserConfigurationException e) {
      return new Nothing<Report>();
    } catch (SAXException e) {
      return new Nothing<Report>();
    } catch (IOException e) {
      return new Nothing<Report>();
    }
  }
}