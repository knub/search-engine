package de.hpi.krestel.mySearchEngine.xml;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * A parser for Wikipedia XML dumps.
 */
public class WikipediaReader implements DocumentReaderInterface
{
	private XMLInputFactory factory = XMLInputFactory.newInstance();
	private List<DocumentReaderListener> listeners = new ArrayList<DocumentReaderListener>();

    private String filename;

    public WikipediaReader(String filename)
    {
        this.filename = filename;
    }

	@Override
    public void startReading()
    {
		try {
			Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.filename), StandardCharsets.UTF_8));
			XMLStreamReader streamReader = factory.createXMLStreamReader(reader);

			boolean isInText = false;
			boolean isInTitle = false;
			String title = "";

			StringBuilder text = null;

			while (streamReader.hasNext()) {
				streamReader.next();

                // Opening tags
				if (streamReader.getEventType() == XMLStreamReader.START_ELEMENT) {
					if (streamReader.getLocalName().equals("text")) {
						isInText = true;
						text  = new StringBuilder();
					} else if (streamReader.getLocalName().equals("title")) {
						isInTitle= true;
					}
				}
                // Closing tags
                else if (streamReader.getEventType() == XMLStreamReader.END_ELEMENT) {
					if (streamReader.getLocalName().equals("text")) {
						isInText = false;
						this.notifyListeners(text.toString(), title);
						text  = new StringBuilder();
					} else if (streamReader.getLocalName().equals("title")) {
						isInTitle = false;
					}
				}
                // Text
                else if (streamReader.getEventType() == XMLStreamReader.CHARACTERS) {
					if (isInText) {
						text.append(streamReader.getText());
					}
					if (isInTitle) {
						title = streamReader.getText();
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
    public void addListener(DocumentReaderListener listener)
    {
		this.listeners.add(listener);
	}

	private void notifyListeners(String text, String title)
    {
		for (DocumentReaderListener listener : this.listeners) {
            listener.documentParsed(text, title);
        }
	}
}
