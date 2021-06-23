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

/**
 * @author jld
 */
public class Main {
    public static void main(String[] args) throws Exception {
      if (args.length != 6 && args.length != 4) {
           System.out.println("\nSyntax :\n########################################################################################");
           System.out.println("Train");
           System.out.println("java -jar  build/libs/task-dataset-metric-extraction-1.0.jar    'train'   <pdfDir>   <threshold> <numbNegative>  <numbUnk>  <outputDir>");
           System.out.println("Test");
           System.out.println("java -jar  build/libs/task-dataset-metric-extraction-1.0.jar    'Test'   <pdfPathFile>   <pathToTDMGold>  <outputDir>");
           System.out.println("########################################################################################");

           System.out.println("\nE.g How to use:\n");
           System.out.println("Train");
           System.out.println("If you are using linux OS");
           System.out.println("java -jar  build/libs/task-dataset-metric-extraction-1.0.jar 'test'  '/home/salomon/path-to-pdf/' '5' '10' '20' '/home/salomon/output-folder/'");
           
           System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
           System.out.println("If you are using windows OS");
           System.out.println("java -jar  build/libs/task-dataset-metric-extraction-1.0.jar 'train' 'C:\\path-to-pdf\\'  '5' '10' '20' 'C:\\output-folder\\'");

           System.out.println("Test");
           System.out.println("java -jar  build/libs/task-dataset-metric-extraction-1.0.jar 'test' '/home/salomon/path-to-pdf-file/' '/home/salomon/output-TDMGold/' '/home/salomon/output-folder/'");
           System.exit(-1);

           // Test
           // java -jar build/libs/task-dataset-metric-extraction-1.0.jar 'test' "/nfs/home/kabenamualus/Research/task-dataset-metric-extraction/data/paperwithcode/pdf/1203.1005v3.pdf" "/nfs/home/kabenamualus/Research/task-dataset-metric-extraction/data/paperwithcode/new/60Neg800unk/" "/nfs/home/kabenamualus/Research/task-dataset-metric-extraction/data/paperwithcode/new/60Neg800unk/test/"
           
           // Train
           // java -jar build/libs/task-dataset-metric-extraction-1.0.jar 'train' "/nfs/home/kabenamualus/Research/task-dataset-metric-extraction/data/paperwithcode/pdf/" '5' '10' '20'  "/nfs/home/kabenamualus/Research/task-dataset-metric-extraction/data/paperwithcode/new/"
      }        


        // Path to pdfs folder
        String pdfDir = args[1];
        // String pdfDir = "/nfs/home/kabenamualus/Research/task-dataset-metric-extraction/data/pdf_IBM/";
        // String pdfDir = "U:\\Documents\\ORKG\\NLP\\task-dataset-metric-extraction\\data\\paperwithcode\\pdf\\";
        // String pdfDir = "/nfs/home/kabenamualus/Research/task-dataset-metric-extraction/data/ibm/NLP-TDMS/pdfFile/";

        // String preOutput="";
        String data_file="";
        String outputDir;
        String pathToTDMGold="";
        String basePath = "";
        Integer threshold = 5;
        Integer numbNegative = 20;
        Integer numbUnk = 200;

        if (args[0].equals("train")){
        // if ("train".equals("train")){
            // Only consider leaderboard that have at least 5 papers
            threshold = Integer.parseInt(args[2]);
   
           // This specify the number of negative instances
            numbNegative = Integer.parseInt(args[3]);
   
           // This specify the number of negative instances
            numbUnk = Integer.parseInt(args[4]);
   
           String Subtype = "";
           // String Subtype = "newFull/";
           // String Subtype = "new_IBM/";

        //    basePath = "/nfs/home/kabenamualus/Research/task-dataset-metric-extraction/data/paperwithcode/newFull/"+Subtype+numbNegative.toString()+"Neg"+numbUnk.toString()+"unk/";
           basePath = args[5]+Subtype+numbNegative.toString()+"Neg"+numbUnk.toString()+"unk/";

            data_file = args[5]+Subtype+numbNegative.toString()+"Neg"+numbUnk.toString()+"unk/trainOutput.tsv";
            // data_file = basePath+"trainOutput.tsv";

            outputDir = args[5]+Subtype+numbNegative.toString()+"Neg"+numbUnk.toString()+"unk/twofoldwithunk/";
            // outputDir = basePath+"twofoldwithunk/";
            
        }
        else{
            outputDir = args[3];
            pathToTDMGold =  args[2].toString();
        }
        // Pre-output folder
        // preOutput = basePath;
        // String preOutput = args[6]+Subtype+numbNegative.toString()+"Neg"+numbUnk.toString()+"unk/";
        // String preOutput = "U:\\Documents\\ORKG\\NLP\\task-dataset-metric-extraction\\data\\paperwithcode\\"+numbNegative.toString()+"Neg"+numbUnk.toString()+"unk/";
        // String preOutput = "/home/salomon/Desktop/task-dataset-metric-extraction/data/paperwithcode/"+numbNegative.toString()+"Neg"+numbUnk.toString()+"unk/";

        
        // check if the target folder exist if not create it.
        File theDiroutput = new File(outputDir);
        if (!theDiroutput.exists()){
            theDiroutput.mkdirs();
        }

       if (args[0].equals("train")){
        // if ("train".equals("train")){

            // check if the target folder exist if not create it.
            File theDir = new File(basePath);
            if (!theDir.exists()){
                theDir.mkdirs();
            }

            // Added this to have the portion of unknown instances
            FileOutputStream fold_stats = new FileOutputStream(basePath+"fold_stats.tsv");

            // Generate the training data
            DatasetGeneration.getTrainData(pdfDir, basePath, threshold, numbUnk, numbNegative, fold_stats);

            Map<String, List<String>> data = new HashMap<>();
            // This help to keep track of TDM seen so far
            List<String> tdms = new ArrayList<>();


            TwoFoldCrossValidation.readFile2(data_file, data, tdms, StandardCharsets.UTF_8);

            // Data split
            int datasize = data.keySet().size();
            int training_datasize = (int)(0.7 * datasize)+1;
            int test_datasize = (int)(0.3 * datasize);

            // Set the number of fold (train/test)
            int number_fold = 2;

            // This returns randomly generated indices for the testing examples
            Map<Integer, List<Integer>> perfoldTestIndexes = getPerFoldTestIndexes(0, datasize, test_datasize, number_fold);

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
                test_indexes.close();

            }
        }else{
            DatasetGeneration.getTestFromFileData(pdfDir , outputDir, pathToTDMGold);
        }
        
        }

        
        

//        DatasetGeneration.getTestData(pdfDir , b);

//        GenerateTestDataOnPDFPapers createTestdata = new GenerateTestDataOnPDFPapers();
//
//        createTestdata.generateTestData4ScorePrediction("D:\\ORKG\\NLP\\task-dataset-metric-extraction\\data\\pdf\\",
//                "D:\\ORKG\\NLP\\task-dataset-metric-extraction\\src\\main\\resources\\test_score.tsv");

//        createTestdata.generateTestData4ScorePrediction("D:\\ORKG\\NLP\\task-dataset-metric-extraction\\data\\pdf\\",
//                "D:\\ORKG\\NLP\\task-dataset-metric-extraction\\src\\main\\resources\\train_score.tsv");



//    String pdfDir = "U:\\Documents\\ORKG\\NLP\\task-dataset-metric-extraction\\data\\pdf\\";
//    String pdfDir = "U:\\Documents\\ORKG\\NLP\\task-dataset-metric-extraction\\data\\paperwithcode\\pdf\\";
//    String b = "U:\\Documents\\ORKG\\NLP\\task-dataset-metric-extraction\\src\\main\\resources\\"+numbNegative.toString()+"Neg"+numbUnk.toString()+"unk\\";
//    String data_file = "U:\\Documents\\ORKG\\NLP\\task-dataset-metric-extraction\\src\\main\\resources\\"+numbNegative.toString()+"Neg"+numbUnk.toString()+"unk\\trainOutput.tsv";
//    String outputDir = "U:\\Documents\\ORKG\\NLP\\task-dataset-metric-extraction\\src\\main\\resources\\"+numbNegative.toString()+"Neg"+numbUnk.toString()+"unk\\twofoldwithunk\\";


//    java -jar task-dataset-metric-extraction-1.0.jar 'train'    '5' '10' '20' 'U:\\Documents\\ORKG\\NLP\\task-dataset-metric-extraction\\data\\paperwithcode\\pdf\\' 'U:\\Documents\\ORKG\\NLP\\task-dataset-metric-extraction\\data\\paperwithcode\\'
}