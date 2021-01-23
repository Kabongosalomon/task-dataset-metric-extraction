package eu.tib.sre;

import eu.tib.sre.pdfparser.DocTAET;
import eu.tib.sre.tdmsie.GenerateTestDataOnPDFPapers;
import eu.tib.sre.utils.DatasetGeneration;
import eu.tib.sre.utils.TwoFoldCrossValidation;
import org.apache.commons.io.FileUtils;
import java.io.File;


//import eu.tib.sre.pdfbox.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static eu.tib.sre.utils.TwoFoldCrossValidation.getPerFoldTestIndexes;
import static eu.tib.sre.utils.TwoFoldCrossValidation.writeOutput;


// import org.grobid.core.*;
// import org.grobid.core.data.*;
// import org.grobid.core.factory.*;
// import org.grobid.core.main.GrobidHomeFinder;
// // import org.grobid.core.mock.*;
// import org.grobid.core.utilities.*;
// import org.grobid.core.engines.Engine;

// import java.io.FileInputStream;
// import java.util.Arrays;
// import java.util.Properties;

/**
 * @author jld
 */
public class Main {

    public static void main(String[] args) throws Exception {


//         // String pdfPath = "/home/salomon/Desktop/grobid-example/src/test/resources/Wang_paperAVE2008.pdf";

//         String pdfPath = "/home/salomon/Desktop/grobid-example/src/test/resources/HAL.pdf";
//         try {
//             String pGrobidHome = "/home/salomon/Desktop/grobid-0.6.0/grobid-home";
// //                    "/Users/lopez/grobid/grobid-home";

//             // The GrobidHomeFinder can be instantiate without parameters to verify the grobid home in the standard
//             // location (classpath, ../grobid-home, ../../grobid-home)

//             // If the location is customised:
//             GrobidHomeFinder grobidHomeFinder = new GrobidHomeFinder(Arrays.asList(pGrobidHome));

//             //The GrobidProperties needs to be instantiate using the correct grobidHomeFinder or it will use the default
//             //locations
//             GrobidProperties.getInstance(grobidHomeFinder);

//             System.out.println(">>>>>>>> GROBID_HOME=" + GrobidProperties.get_GROBID_HOME_PATH());

//             Engine engine = GrobidFactory.getInstance().createEngine();

//             // Biblio object for the result
//             BiblioItem resHeader = new BiblioItem();
//             String tei = engine.processHeader(pdfPath, 1, resHeader);

//             System.out.println("tei " + tei);
//         } catch (Exception e) {
//             // If an exception is generated, print a stack trace
//             e.printStackTrace();
//         }
//     }






        // "D:\\ORKG\\NLP\\task-dataset-metric-extraction\\data\\paperwithcode\\pdf\\"
        // "D:\\ORKG\\NLP\\task-dataset-metric-extraction\\src\\main\\resources\\"
        // "50"

//        System.out.println(args[0]);
//        System.out.println(args[1]);
//        System.out.println(args[2]);

//        if (args.length != 3) {
//            System.out.println("Usage: java Main.java <path_to_pdf> <path_to_output> <numb_negative>");
//            System.out.println("java -jar task-dataset-metric-extraction-1.0.jar 'D:\\ORKG\\NLP\\task-dataset-metric-extraction\\data\\paperwithcode\\pdf\\' 'D:\\ORKG\\NLP\\task-dataset-metric-extraction\\src\\main\\resources\\' '10'");
//            System.exit(-1);
//        }

        // Only consider leaderboard that have at least 5 papers
        Integer threshold = 5;
        // Integer threshold = 50;

//        Integer numbNegative = Integer.parseInt(args[2]);
//        String pdfDir = args[0];
//        String b = args[1]+numbNegative.toString()+"unk\\";
//        String data_file = args[1]+numbNegative.toString()+"unk\\trainOutput.tsv";
//        String outputDir = args[1]+numbNegative.toString()+"unk\\twofoldwithunk\\";



//        Integer numbNegative = Integer.parseInt("2");
//        String pdfDir = "D:\\ORKG\\NLP\\task-dataset-metric-extraction\\data\\paperwithcode\\pdf\\";
//        String b = "D:\\ORKG\\NLP\\task-dataset-metric-extraction\\src\\main\\resources\\"+numbNegative.toString()+"unk\\";
//        String data_file = "D:\\ORKG\\NLP\\task-dataset-metric-extraction\\src\\main\\resources\\"+numbNegative.toString()+"unk\\trainOutput.tsv";
//        String outputDir = "D:\\ORKG\\NLP\\task-dataset-metric-extraction\\src\\main\\resources\\"+numbNegative.toString()+"unk\\twofoldwithunk\\";

        // This specify the number of negative instances
        Integer numbNegative = Integer.parseInt("5");

        // This specify the number of negative instances
        Integer numbUnk = Integer.parseInt("10");

        // Path to pdfs folder
        // String pdfDir = "D:\\ORKG\\NLP\\task-dataset-metric-extraction\\data\\pdf\\";
        // Pre-output folder
        // String b = "D:\\ORKG\\NLP\\task-dataset-metric-extraction\\data\\paperwithcode\\"+numbNegative.toString()+"unk\\";
        // Main tsv datafile
        // String data_file = "D:\\ORKG\\NLP\\task-dataset-metric-extraction\\data\\paperwithcode\\"+numbNegative.toString()+"unk\\trainOutput.tsv";
        // fold output folder
        // String outputDir = "D:\\ORKG\\NLP\\task-dataset-metric-extraction\\data\\paperwithcode\\"+numbNegative.toString()+"unk\\twofoldwithunk\\";


        // Path to pdfs folder
        String pdfDir = "/home/salomon/Desktop/task-dataset-metric-extraction/data/pdf/"; 
        // String pdfDir = "/home/salomon/Desktop/task-dataset-metric-extraction/data/paperwithcode/pdf/"; 
        
        // Pre-output folder
        String b = "/home/salomon/Desktop/task-dataset-metric-extraction/data/paperwithcode/"+numbNegative.toString()+"Neg"+numbUnk.toString()+"unk/";
        // Main tsv datafile
        String data_file = "/home/salomon/Desktop/task-dataset-metric-extraction/data/paperwithcode/"+numbNegative.toString()+"Neg"+numbUnk.toString()+"unk/trainOutput.tsv";
        // fold output folder
        String outputDir = "/home/salomon/Desktop/task-dataset-metric-extraction/data/paperwithcode/"+numbNegative.toString()+"Neg"+numbUnk.toString()+"unk/twofoldwithunk/";

        // // Pre-output folder
        // String b = "/home/salomon/Desktop/task-dataset-metric-extraction/data/paperswithcodedatawith600unk/twofold/fold2/";
        // // Main tsv datafile
        // String data_file = "/home/salomon/Desktop/task-dataset-metric-extraction/data/paperswithcodedatawith600unk/twofold/fold2/train-full.tsv";
        // // fold output folder
        // String outputDir = "/home/salomon/Desktop/task-dataset-metric-extraction/data/paperswithcodedatawith600unk/twofold/fold2/";


        // check if the target folder exist if not create it.
        File theDir = new File(b);
        if (!theDir.exists()){
            theDir.mkdirs();
        }

        // Added this to have the portion of unknown instances
        FileOutputStream fold_stats = new FileOutputStream(b+"fold_stats.tsv");

        // Generate the training data
        DatasetGeneration.getTrainData(pdfDir, b, threshold, numbUnk, numbNegative, fold_stats);

//        DatasetGeneration.getTestData(pdfDir , b);

//        GenerateTestDataOnPDFPapers createTestdata = new GenerateTestDataOnPDFPapers();
//
//        createTestdata.generateTestData4ScorePrediction("D:\\ORKG\\NLP\\task-dataset-metric-extraction\\data\\pdf\\",
//                "D:\\ORKG\\NLP\\task-dataset-metric-extraction\\src\\main\\resources\\test_score.tsv");

//        createTestdata.generateTestData4ScorePrediction("D:\\ORKG\\NLP\\task-dataset-metric-extraction\\data\\pdf\\",
//                "D:\\ORKG\\NLP\\task-dataset-metric-extraction\\src\\main\\resources\\train_score.tsv");



        Map<String, List<String>> data = new HashMap<>();
        // This help to keep track of TDM seen so far
        List<String> tdms = new ArrayList<>();


        TwoFoldCrossValidation.readFile2(data_file, data, tdms, StandardCharsets.UTF_8);


        // // List of content in trainOutput.tsv
        // String[] lines = TwoFoldCrossValidation.readFile(data_file, StandardCharsets.UTF_8).split("\n");

        // Map<String, List<String>> data = new HashMap<>();

        // // This help to keep track of TDM seen so far
        // List<String> tdms = new ArrayList<>();


        // for (String line : lines) {
        //     line = line.trim();
        //     String[] tokens = line.split("\t");
        //     //System.out.println(tokens.length);

        //     // The help to have the paper with all it's true TDM
        //     List<String> dataLines = data.get(tokens[1]);
        //     if (dataLines == null) data.put(tokens[1], dataLines = new ArrayList<>());

        //     // // Fix issue with false label appended on the paper 
        //     // if(line.split("\t")[0].equals("true")){
        //     //     dataLines.add(line);
        //     // }

        //     dataLines.add(line);

        //     if (!tdms.contains(tokens[2])) tdms.add(tokens[2]);
        // }

        // Data split
        int datasize = data.keySet().size();
        int training_datasize = (int)(0.7 * datasize)+1;
        int test_datasize = (int)(0.3 * datasize);

        // Set the number of fold (train/test)
        int number_fold = 2;

        //System.out.println(datasize);
        //System.out.println(training_datasize);
        //System.out.println(test_datasize);
        //System.exit(-1);

        // This returns randomly generated indices for the testing examples
        Map<Integer, List<Integer>> perfoldTestIndexes = getPerFoldTestIndexes(0, datasize, test_datasize, number_fold);


        // check if the target folder exist if not create it.
        File theDiroutput = new File(outputDir);
        if (!theDiroutput.exists()){
            theDiroutput.mkdirs();
        }

        // A list made of paper titles (e.g : 1404.4326v1.pdf, 2005.12661v1.pdf, ...)
        List<String> dataFiles = new ArrayList<>(data.keySet());

        for (int fold : perfoldTestIndexes.keySet()) {

            String fold_i = outputDir+"fold"+fold+"/";

            // calculate stats (value in Java are pass by value
            // ref (https://stackoverflow.com/questions/26185527/how-can-i-change-integer-value-when-it-is-an-argument-like-change-arrays-value)
            AtomicInteger trueUnk = new AtomicInteger(0);
            AtomicInteger falseUnk = new AtomicInteger(0);

            // check if the target folder exist if not create it.
            File theFoldDir = new File(fold_i);
            if (!theFoldDir.exists()){
                theFoldDir.mkdirs();
            }


            FileOutputStream train_output = new FileOutputStream(fold_i+"train.tsv");
            FileOutputStream test_output = new FileOutputStream(fold_i+"dev.tsv");
            FileOutputStream test_indexes = new FileOutputStream(fold_i+"test_indexes.tsv");

//            // Added this to have the portion of unknown instances
//            FileOutputStream fold_stats = new FileOutputStream(fold_i+"fold_stats.tsv");

            // This give us the indeces of testing
            List<Integer> testIndexes = perfoldTestIndexes.get(fold);

            fold_stats.write(("Fold "+fold+" Data stats :\n").getBytes());

            for (int i = 0; i < datasize; i++) {

                // pdf name as key 
                String file = dataFiles.get(i);

                List<String> dataLines = data.get(file);

                if (testIndexes.contains(i)) {
                    writeOutput(test_output, file, dataLines, tdms, fold_stats, 
                                            trueUnk, falseUnk, numbUnk, numbNegative, "test");

                    // Write test indexes per fold in a file
                    test_indexes.write((i+"\n").getBytes());
                }
                else {
                    writeOutput(train_output, file, dataLines, tdms, fold_stats, 
                                            trueUnk, falseUnk, numbUnk, numbNegative, "train");
                }
            }

            fold_stats.write(("Positive Unk : "+trueUnk+"\n").getBytes());
            fold_stats.write(("Negative Unk : "+falseUnk+"\n\n").getBytes());

        }
    }

}