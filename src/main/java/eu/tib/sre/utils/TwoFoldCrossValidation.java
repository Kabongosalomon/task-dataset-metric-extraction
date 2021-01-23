package eu.tib.sre.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;

/**
 * @author jld
 */
public class TwoFoldCrossValidation {
    // This dictionary save the name of the file (e.g "2005.12661v1.pdf")
//    static Map<String, List<String>> data = new HashMap<>();

    // This structure save the TaskDatasetMetric informations from the input file
    // (e.g "Question Answering; YahooCQA; P@1")
//    static List<String> tdms = new ArrayList<>();

    public static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);

    }

    public static void readFile2(String path,  Map<String, List<String>> data,
                            List<String> tdms, Charset encoding) throws IOException {

        /*
            This solve the issue of Exception in thread "main" java.lang.NegativeArraySizeException: -1907719306
            based on https://www.baeldung.com/java-read-lines-large-file
        */
        // List<String> stringList = FileUtils.readLines(new File(Paths.get(path).toString()), encoding);
        // return StringUtils.join(stringList, "\n");
        File theFile = new File(Paths.get(path).toString());
        LineIterator it = FileUtils.lineIterator(theFile, "UTF-8");
        try {
            while (it.hasNext()) {
                String line = it.nextLine();
                // do something with line
                line = line.trim();
                String[] tokens = line.split("\t");
                //System.out.println(tokens.length);

                // The help to have the paper with all it's true TDM
                List<String> dataLines = data.get(tokens[1]);
                if (dataLines == null) data.put(tokens[1], dataLines = new ArrayList<>());

                // // Fix issue with false label appended on the paper 
                // if(line.split("\t")[0].equals("true")){
                //     dataLines.add(line);
                // }

                dataLines.add(line);

                if (!tdms.contains(tokens[2])) tdms.add(tokens[2]);


            }
        } finally {
            LineIterator.closeQuietly(it);
        }

        
        // return data, tdms

    }
    

    public static Map<Integer, List<Integer>> getPerFoldTestIndexes(int min_limit, int max_limit, int total_numbers, int number_fold) {
        Set<Integer> seen = new HashSet<>();
        Map<Integer, List<Integer>> perfoldTestInstances = new HashMap<>();
        Random random = new Random();
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= number_fold; i++) {
            perfoldTestInstances.put(i, numbers = new ArrayList<>());
            for (int j = 0; j < total_numbers; j++) {
                int num = -1;
                while (true) {
                    num = random.ints(min_limit, max_limit).findFirst().getAsInt();
                    if (!seen.contains(num)) {
                        seen.add(num);
                        break;
                    }
                }
                numbers.add(num);
            }
        }
        return perfoldTestInstances;
    }

//    public static void main(String[] args) throws IOException {
//        /*if (args.length == 1) {
//            System.out.println("Usage: java TwoFoldCrossValidation.java <data-file>");
//            System.exit(-1);
//        }*/
//
//        Integer numbNegative = 0;
//
//        String data_file =
////          "D:\\ORKG\\NLP\\task-dataset-metric-extraction\\src\\main\\resources\\"+numbNegative.toString()+"unk\\trainOutput.tsv";
//          "D:\\ORKG\\NLP\\task-dataset-metric-extraction\\src\\main\\resources\\"+numbNegative.toString()+"unk\\trainOutput.tsv";
//
////        "D:\\ORKG\\NLP\\task-dataset-metric-extraction\\data\\paperswithcodedatawith600unk\\paperswithcode600UnkDataTitleAbstract.tsv";
//        String[] lines = readFile(data_file, StandardCharsets.UTF_8).split("\\n");
//
//
//
//        for (String line : lines) {
//            line = line.trim();
//            String[] tokens = line.split("\t");
//            //System.out.println(tokens.length);
//
//            List<String> dataLines = data.get(tokens[1]);
//            if (dataLines == null) data.put(tokens[1], dataLines = new ArrayList<>());
//            dataLines.add(line);
//            if (!tdms.contains(tokens[2])) tdms.add(tokens[2]);
//        }
//
//        int datasize = data.keySet().size();
//        int training_datasize = (int)(0.7 * datasize)+1;
//        int test_datasize = (int)(0.3 * datasize);
//
//        // Set the number of fold (train/test)
//        int number_fold = 2;
//
//        //System.out.println(datasize);
//        //System.out.println(training_datasize);
//        //System.out.println(test_datasize);
//        //System.exit(-1);
//
//        // This returns randomly generated indeces for the testing examples
//        Map<Integer, List<Integer>> perfoldTestIndexes = getPerFoldTestIndexes(0, datasize, test_datasize, number_fold);
//
//        String outputDir = //"C:\\Users\\DSouzaJ\\Desktop\\Datasets\\paperswithcode\\twofold";
//                "D:\\ORKG\\NLP\\task-dataset-metric-extraction\\src\\main\\resources\\"+numbNegative.toString()+"unk\\twofoldwithunk\\";
//
//        // check if the target folder exist if not create it.
//        File theDiroutput = new File(outputDir);
//        if (!theDiroutput.exists()){
//            theDiroutput.mkdirs();
//        }
//
//        // A list made of paper titles (e.g : 1404.4326v1.pdf, 2005.12661v1.pdf, ...)
//        List<String> dataFiles = new ArrayList<>(data.keySet());
//
//        for (int fold : perfoldTestIndexes.keySet()) {
//
//            String fold_i = outputDir+"fold"+fold+"\\";
//
//            // check if the target folder exist if not create it.
//            File theDir = new File(fold_i);
//            if (!theDir.exists()){
//                theDir.mkdirs();
//            }
//
//
//            FileOutputStream train_output = new FileOutputStream(fold_i+"train.tsv");
//            FileOutputStream test_output = new FileOutputStream(fold_i+"test.tsv");
//            FileOutputStream test_indexes = new FileOutputStream(fold_i+"test_indexes.tsv");
//
//            List<Integer> testIndexes = perfoldTestIndexes.get(fold);
//
//            for (int i = 0; i < datasize; i++) {
//
//                String file = dataFiles.get(i);
//                List<String> dataLines = data.get(file);
//
//                if (testIndexes.contains(i)) {
//                    writeOutput(test_output, file, dataLines);
//
//                    // Write test indexes per fold in a file
//                    test_indexes.write((i+"\n").getBytes());
//                }
//                else {
//                    writeOutput(train_output, file, dataLines);
//                }
//            }
//        }
//    }

    public static void writeOutput(FileOutputStream output, String filename,
                                   List<String> dataLines, List<String> tdms, FileOutputStream fold_stats,
                                   AtomicInteger trueUnk, AtomicInteger falseUnk, 
                                   Integer numbUnk, Integer numbNegative, String mode) throws IOException {

        String content = "";

        List<String> trueTDM = new ArrayList<>();
        
        // To look through all the true of a partuclar paper 
        for (String dataLine : dataLines) {
            if (content.equals("")) {
                //System.out.println(dataLine);

                // save the description of the file one time for all
                content = dataLine.split("\t")[3];
            }

            String label = dataLine.split("\t")[2];

            if (mode.equals("train")) {
                if (label.equals("unknow") && dataLine.split("\t")[0].equals("true")) {
                    

                        if (trueUnk.intValue() >= numbUnk){

                            continue;

                        }
                        else {

                            // This help to keep track of TDM that we have seen on a particular fold
                            trueTDM.add(label);

                            output.write((dataLine+"\n").getBytes());

                            // trueUnk = trueUnk + 1;
                            trueUnk.set(trueUnk.intValue() +1);
                            
                            continue;
                        }

                } else if (label.equals("unknow") && dataLine.split("\t")[0].equals("false")) {
                    falseUnk.set(falseUnk.intValue() +1);
                }
            
        }

        // This help to keep track of TDM that we have seen on a particular fold
        trueTDM.add(label);

        output.write((dataLine+"\n").getBytes());

    }

//         // Thits takes only the first numbNegative example, we may need to make it random
//         int limit = 0;

//         // Suffle Collection
//         Collections.shuffle(tdms);

//         for (String tdm : tdms) {

//             if (trueTDM.contains(tdm)) continue;

//             if ( limit >= numbNegative ){
//                 break;
//             }

//             // All the remaining TDM from the overall are created with a false label
//             output.write(("false\t"+filename+"\t"+tdm+"\t"+content+"\n").getBytes());

//             // Update the count for one more false label
//             limit += 1;

//             if (tdm.equals("unknow") && mode.equals("train")) {
// //                falseUnk = falseUnk + 1;
//                 falseUnk.set(falseUnk.intValue() +1);
//             }

//         }




    }

}