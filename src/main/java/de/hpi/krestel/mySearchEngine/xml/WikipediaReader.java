package de.hpi.krestel.mySearchEngine.xml;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class WikipediaReader {
	final String WIKI_FILE_SHORT = "data/dewiki-20140216-pages-articles-multistream-first-five.xml";
	final String WIKI_FILE       = "data/dewiki-20140216-pages-articles-multistream.xml";

	XMLInputFactory factory = XMLInputFactory.newInstance();
	List<TextCompletedListener> listeners = new ArrayList<TextCompletedListener>();

	public void readWikiFile() {
		try {
			Reader reader = new BufferedReader(new FileReader(WIKI_FILE_SHORT));
			XMLStreamReader streamReader = factory.createXMLStreamReader(reader);

			boolean isInText = false;

			StringBuilder text = null;
			while (streamReader.hasNext()) {
				streamReader.next();

				if (streamReader.getEventType() == XMLStreamReader.START_ELEMENT) {
					if (streamReader.getLocalName().equals("text")) {
						isInText = true;
						text  = new StringBuilder();
					}
				} else if (streamReader.getEventType() == XMLStreamReader.END_ELEMENT) {
					if (streamReader.getLocalName().equals("text")) {
						isInText = false;
						onTextCompleted(text.toString());
						text  = new StringBuilder();
					}
				} else if (streamReader.getEventType() == XMLStreamReader.CHARACTERS) {
					if (isInText) {
						text.append(streamReader.getText());
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void addTextCompletedListener(TextCompletedListener listener) {
		this.listeners.add(listener);
	}

	public void onTextCompleted(String text) {
		for (TextCompletedListener listener : listeners)
			listener.onTextCompleted(text);
	}
}
