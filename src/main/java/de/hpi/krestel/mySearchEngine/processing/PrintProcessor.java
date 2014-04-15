package de.hpi.krestel.mySearchEngine.processing;

import java.util.List;

public class PrintProcessor extends AbstractProcessor {
    private String heading;

    public PrintProcessor(String heading) {
        this.heading = heading;
    }

    @Override
    public List<String> process(List<String> input) {
        System.out.println("### " + this.heading.toUpperCase());

        for (String item : input) {
            System.out.print(item);
            System.out.print(" | ");
        }
        System.out.print("\n");

        return input;
    }
}
