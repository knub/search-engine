package de.hpi.krestel.mySearchEngine.xml;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;

public class WikipediaReader {

	public void readXMLWithSAX() {
		System.out.println("SAX");
        SAXReader wikiReader = new SAXReader();
        wikiReader.printPageIds("data/dewiki-20140216-pages-articles-multistream-first-five.xml");
	}

	XMLInputFactory factory = XMLInputFactory.newInstance();

	public void readXMLWithStAX() {
		try {
			XMLInputFactory factory = XMLInputFactory.newInstance();
			Reader reader = new BufferedReader(new FileReader("data/dewiki-20140216-pages-articles-multistream-first-five.xml"));
			XMLStreamReader streamReader = factory.createXMLStreamReader(reader);

			boolean isInRevision = false;
			boolean isInId = false;
			boolean isInTitle = false;

			while (streamReader.hasNext()) {
				streamReader.next();

				if (streamReader.getEventType() == XMLStreamReader.START_ELEMENT) {
					if (streamReader.getLocalName().equals("revision"))
						isInRevision = true;
					else if (streamReader.getLocalName().equals("title"))
						isInTitle = true;
					else if (streamReader.getLocalName().equals("id") && !isInRevision)
						isInId = true;
				} else if (streamReader.getEventType() == XMLStreamReader.END_ELEMENT) {
						if (streamReader.getLocalName().equals("revision"))
							isInRevision = false;
					isInId = false;
					isInTitle = false;
				} else if (streamReader.getEventType() == XMLStreamReader.CHARACTERS) {
					if (isInTitle)
						System.out.print(streamReader.getText() + " ");
					else if (isInId)
						System.out.println(streamReader.getText());
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
