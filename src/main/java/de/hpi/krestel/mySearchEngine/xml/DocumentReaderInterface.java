package de.hpi.krestel.mySearchEngine.xml;

public interface DocumentReaderInterface
{
    void startReading();

    void addListener(DocumentReaderListener listener);
}
