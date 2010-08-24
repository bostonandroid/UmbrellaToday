package org.bostonandroid.umbrellatoday;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ReportHandler extends DefaultHandler {
  private Report report = new Report();
  private String currentElement = null;

  public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    if (localName.equals("answer")) {
      this.currentElement = "answer";
    }
  }

  public void endElement(String uri, String localName, String qName) throws SAXException {
    if (localName.equals("answer")) {
      this.currentElement = null;
    }
  }

  public void characters(char[] ch, int start, int length) throws SAXException {
    if (this.currentElement != null && this.currentElement.equals("answer")) {
      String answer = new String(ch, start, length);
      this.report.setAnswer(answer);
    }
  }

  public Report getReport() {
    return this.report;
  }
}
