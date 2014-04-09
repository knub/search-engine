package de.hpi.krestel.mySearchEngine.xml;

public class WikipediaReader {

	public static void readXMLWithSAX() {
		System.out.println("SAX");
        SAXReader wikiReader = new SAXReader();
        wikiReader.printPageIds("data/dewiki-20140216-pages-articles-multistream-first-five.xml");
	}

	public static void readXMLWithStAX() {
		System.out.println("StAX");
	}
}
