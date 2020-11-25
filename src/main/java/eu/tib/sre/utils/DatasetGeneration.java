package eu.tib.sre.utils;

import eu.tib.sre.pdfparser.DocTAET;
import eu.tib.sre.pdfparser.GrobidPDFProcessor;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DatasetGeneration {

    public static String getTrainingData(String pdfDirm, String[] args) throws IOException, Exception {

        FileOutputStream output = new FileOutputStream(args[1]+"/trainOutput.tsv");

        File dir = new File(pdfDirm);

        File[] filesList = dir.listFiles();

        for (File file : filesList) {
            if (file.isFile()) {

                String pdf_file = FileUtils.readFileToString(file);

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



        return "Done";
    }

//    public static String getTestingData(String pdfDir) throws IOException, Exception {
//        GrobidPDFProcessor gp = GrobidPDFProcessor.getInstance();
//        Map<String, String> sections = gp.getPDFSectionAndText(pdfFile);
//        return sections.get("title");
////        DocTAET
//    }
}
