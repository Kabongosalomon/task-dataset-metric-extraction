package eu.tib.sre;

import eu.tib.sre.pdfparser.DocTAET;
import org.apache.commons.io.FileUtils;

//import eu.tib.sre.pdfbox.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * @author jld
 */
public class Main {

    public static void main(String[] args) throws Exception {

        if (args.length != 2) {
            System.out.println("Usage: java eu.tib.sre.Main.java <pdf-file-path> <resources-dir>");
            System.exit(-1);
        }

        String pdf_file = args[0];
        FileOutputStream output = new FileOutputStream(args[1]+"/output.tsv");
        System.out.println(">>>> Processing file: " + pdf_file);

        String docTEATStr = DocTAET.getDocTAETRepresentation(pdf_file);
        if (docTEATStr.equals("")) {
            System.err.print("PDF parsing error!");
        }
        else {
            String labels_file = args[1]+"/tdmGoldLabels.tsv";
            List<String> labels = FileUtils.readLines(new File(labels_file));
            String pdf_filename = new File(pdf_file).getName();

            for (String label : labels) {
                output.write(("true\t" + pdf_filename + "\t"+label+"\t" + docTEATStr + "\n").getBytes());
            }

            output.write(("true\t" + pdf_filename + "\tunknown\t" + docTEATStr + "\n").getBytes());
        }


    }

}