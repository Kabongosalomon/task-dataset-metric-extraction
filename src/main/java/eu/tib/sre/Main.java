package eu.tib.sre;

import eu.tib.sre.pdfparser.DocTAET;
import eu.tib.sre.utils.DatasetGeneration;
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

//        args[0] = "D:\\ORKG\\NLP\\task-dataset-metric-extraction\\data\\pdf\\50.pdf";
//        args[1] = "D:\\ORKG\\NLP\\task-dataset-metric-extraction\\src\\main\\resources\\";

        String pdfDir = "D:\\ORKG\\NLP\\task-dataset-metric-extraction\\data\\pdf\\";
        String b = "D:\\ORKG\\NLP\\task-dataset-metric-extraction\\src\\main\\resources\\";

        // Only consider leaderboard that have atleast 5 papers
        Integer threshold = 3;
        Integer numbNegative = 50;

        DatasetGeneration.getTrainData(pdfDir , b, threshold, numbNegative);

        DatasetGeneration.getTestData(pdfDir , b);

    }

}