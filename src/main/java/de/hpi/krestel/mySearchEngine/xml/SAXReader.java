package de.hpi.krestel.mySearchEngine.xml;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class SAXReader {

    public static void printPageIds(String filename) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            WikipediaSAXHandler handler = new WikipediaSAXHandler();

            saxParser.parse(filename, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
