package eu.tib.sre;

import eu.tib.sre.pdfparser.DocTAET;
import eu.tib.sre.tdmsie.GenerateTestDataOnPDFPapers;
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
//        args[0] = "D:\\ORKG\\NLP\\task-dataset-metric-extraction\\data\\pdf\\50.pdf";
//        args[1] = "D:\\ORKG\\NLP\\task-dataset-metric-extraction\\src\\main\\resources\\";

//        if (args.length != 2) {
//            System.out.println("Usage: java Main.java <pdf-file-path> <resources-dir>");
//            System.exit(-1);
//        }

        // Only consider leaderboard that have atleast 5 papers
        Integer threshold = 5;
        Integer numbNegative = Integer.parseInt(args[1]);

//        String pdf_file = args[0];
//        FileOutputStream output = new FileOutputStream(args[1]+"/output.tsv");
//        System.out.println(">>>> Processing file: " + pdf_file);

//        String pdfDir = "U:\\Documents\\ORKG\\NLP\\task-dataset-metric-extraction\\data\\paperwithcode\\pdf\\";
        String pdfDir = args[0];
//        String pdfDir = "U:\\Documents\\ORKG\\NLP\\task-dataset-metric-extraction\\data\\pdf\\";

//        String pdfDir = "D:\\ORKG\\NLP\\task-dataset-metric-extraction\\data\\pdf\\";
//        String pdfDir = "D:\\ORKG\\NLP\\science-result-extractor\\nlpLeaderboard\\src\\main\\java\\com\\ibm\\sre\\data\\pdfFile\\";
//        String b = "D:\\ORKG\\NLP\\task-dataset-metric-extraction\\src\\main\\resources\\";

        String b = "U:\\Documents\\ORKG\\NLP\\task-dataset-metric-extraction\\src\\main\\resources\\"+numbNegative.toString()+"unk\\";

//        String b = "D:\\ORKG\\NLP\\task-dataset-metric-extraction\\src\\main\\resources\\"+numbNegative.toString()+"unk\\";

        // check if the target folder exist if not create it.
        File theDir = new File(b);
        if (!theDir.exists()){
            theDir.mkdirs();
        }


        DatasetGeneration.getTrainData(pdfDir , b, threshold, numbNegative);
////
//        DatasetGeneration.getTestData(pdfDir , b);

//        GenerateTestDataOnPDFPapers createTestdata = new GenerateTestDataOnPDFPapers();
//
//        createTestdata.generateTestData4ScorePrediction("D:\\ORKG\\NLP\\task-dataset-metric-extraction\\data\\pdf\\",
//                "D:\\ORKG\\NLP\\task-dataset-metric-extraction\\src\\main\\resources\\test_score.tsv");

//        createTestdata.generateTestData4ScorePrediction("D:\\ORKG\\NLP\\task-dataset-metric-extraction\\data\\pdf\\",
//                "D:\\ORKG\\NLP\\task-dataset-metric-extraction\\src\\main\\resources\\train_score.tsv");
    }

}