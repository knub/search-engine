package de.hpi.krestel.mySearchEngine.processing;

import edu.stanford.nlp.ling.CoreLabel;

import java.util.List;

public class PrintProcessor implements ProcessorInterface {
    private String heading;

    public PrintProcessor(String heading) {
        this.heading = heading;
    }

    @Override
    public List<CoreLabel> process(List<CoreLabel> input) {
        System.out.println("### " + this.heading.toUpperCase());

        for (CoreLabel label : input) {
            System.out.print(label.value());
            System.out.print(" | ");
        }
        System.out.print("\n");

	    System.out.println("This article contained " + input.size() + " stemmed words.");

        return input;
    }
}
