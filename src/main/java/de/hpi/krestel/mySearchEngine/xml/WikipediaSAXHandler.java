package de.hpi.krestel.mySearchEngine.xml;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class WikipediaSAXHandler extends DefaultHandler {
    boolean isInRevision = false;
    boolean isInId = false;

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {

        if (qName.equalsIgnoreCase("REVISION")) {
            isInRevision = true;
        } else if (qName.equals("id")) {
            if (!isInRevision) {
                isInId = true;
            }
        }
    }

    @Override
    public void endElement(String uri, String localName,
                           String qName) throws SAXException {

        if (qName.equals("revision")) {
            isInRevision = false;
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        if (isInId) {
            System.out.println(new String(ch, start, length));
            isInId = false;
        }
    }
}
