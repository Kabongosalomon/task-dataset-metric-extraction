package eu.tib.sre.utils;

import eu.tib.sre.pdfparser.DocTAET;
import eu.tib.sre.pdfparser.GrobidPDFProcessor;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.text.ParseException;
import java.util.*;

public class DatasetGeneration {

    private static Properties prop;
    private static String TDM_taxonomy;
    private static String resultsAnnotation;
    private static HashMap<String, Integer> mapDict;
    private static HashMap<String, ArrayList<String>> mapLabel;

    private static HashMap<String, ArrayList<Integer>> mapScore;

    // public Properties getProperties() {
    // return prop;
    // }

    public DatasetGeneration() throws IOException, Exception {
        prop = new Properties();
        prop.load(new FileReader("config.properties"));
    }

    // public static String getTrainingData(String pdfDir, String[] args) throws
    // IOException, Exception {
    public static String getTrainData(String pdfDir, String b, Integer threshold, Integer numbUnk, Integer numbNegative,
            FileOutputStream fold_stats) throws IOException, Exception {

        prop = new Properties();
        prop.load(new FileReader("config.properties"));
        TDM_taxonomy = prop.getProperty("projectPath") + "/" + prop.getProperty("TDM_taxonomy");

        // The full taxonomy obtains from paper with code json
        // TDM_taxonomy = "D:\\ORKG\\NLP\\task-dataset-metric-extraction\\data\\paperwithcode\\annotations\\TDM_taxonomy.tsv";
        // TDM_taxonomy = "/home/salomon/Desktop/task-dataset-metric-extraction/data/paperwithcode/annotations/TDM_taxonomy.tsv";



        FileOutputStream output = new FileOutputStream(b+"trainOutput.tsv");
//        FileOutputStream output = new FileOutputStream(args[1]+"trainOutput.tsv");

        File dir = new File(pdfDir);

        File[] filesList = dir.listFiles();

        int progress = 0;
        Integer trueUnk = 0;
        Integer falseUnk = 0;

        for (File file : filesList) {
            if (file.isFile()) {

//                String pdf_file = FileUtils.readFileToString(file);
                String pdf_file = file.getPath();
//                String pdf_file pdf_file = file;
                Set<String> trueTDM = new HashSet<String>();
                System.out.println(">>>> ("+ progress++ +") Processing file: " + pdf_file);
                
                // TODO : File to change for ablation study 
                // String docTEATStr = DocTAET.getDocTAETRepresentation150(pdf_file);
                String docTEATStr = DocTAET.getDocTAETRepresentation450(pdf_file);
                // String docTEATStr = DocTAET.getDocTAETRepresentationFull(pdf_file);
                // String docTEATStr = DocTAET.getDocTAETRepresentationTitleAbstract(pdf_file);
                // String docTEATStr = DocTAET.getDocTAETRepresentationAbstract(pdf_file);
                // String docTEATStr = DocTAET.getDocTAETRepresentationTitleAbstractExpSetup(pdf_file);
                // String docTEATStr = DocTAET.getDocTAETRepresentationTitleAbstractTableInfo(pdf_file);

                if (docTEATStr.equals("")) {
                    System.err.print("PDF parsing error!");
                }
                else {
                    // resultsAnnotation = prop.getProperty("result_annotation")

                    // This contains a dict like file that have file name and TDMs informations
                    // resultsAnnotation = "D:\\ORKG\\NLP\\task-dataset-metric-extraction\\data\\paperwithcode\\annotations\\resultsAnnotation.tsv";
                    resultsAnnotation = prop.getProperty("projectPath") + "/" + prop.getProperty("result_annotation");
                    // resultsAnnotation = "/home/salomon/Desktop/task-dataset-metric-extraction/data/paperwithcode/annotations/resultsAnnotation.tsv";

//                    String labels_file = args[1]+"/tdmGoldLabels.tsv";
                    mapDict = getTdmGoldLabelsAndloadDict(TDM_taxonomy, b, threshold);
                    mapLabel = getMapTitlepdfLabel(resultsAnnotation, mapDict, threshold);

                    String labels_file = b+"/tdmGoldLabels.tsv";
                    List<String> labels = FileUtils.readLines(new File(labels_file));
                    String pdf_filename = new File(pdf_file).getName();

                    for (int i = 0; i < mapLabel.get(pdf_filename).size(); i++) {

                        if (mapLabel.get(pdf_filename).get(i).replace("#", "; ").equals("unknow")){

                            if (trueUnk >= numbUnk){

                                continue;

                            }
                            else {
                                output.write(("true\t" + pdf_filename + "\t"+mapLabel.get(pdf_filename).get(i).replace("#", "; ")+"\t" + docTEATStr + "\n").getBytes());
                                
                                // Keep track of positive TDM
                                trueTDM.add(mapLabel.get(pdf_filename).get(i));

                                trueUnk = trueUnk + 1;
                                
                                continue;
                            }


                        }

                        output.write(("true\t" + pdf_filename + "\t"+mapLabel.get(pdf_filename).get(i).replace("#", "; ")+"\t" + docTEATStr + "\n").getBytes());

                        // Keep track of positive TDM
                        trueTDM.add(mapLabel.get(pdf_filename).get(i));


                    }


                    // Thits takes only the first numbNegative example, we may need to make it random
                    int limit = 0;

                    // To randomly allow to get the false for a numNeg threshold
                    Collections.shuffle(labels);

                    for (String label : labels) {
                        if ( limit >= numbNegative ){
                            break;
                        }

                        // Check if the TDM is not a true label for a given paper
                        if (!trueTDM.contains(label)) {
                            output.write(("false\t" + pdf_filename + "\t" + label.replace("#", "; ") + "\t" + docTEATStr + "\n").getBytes());

                            if (label.replace("#", "; ").equals("unknow")){
                                falseUnk = falseUnk + 1;
                            }

                            // Update the count for one more false label
                            limit += 1;
                        }

                    }

                }
            }
        }

        fold_stats.write(("Main Data stats :\n").getBytes());
        fold_stats.write(("Positive Unk : "+trueUnk+"\n").getBytes());
        fold_stats.write(("Negative Unk : "+falseUnk+"\n\n").getBytes());

        output.close();

        return "Done";
    }


    public static String getTrainScoreData(String pdfDir, String b, Integer threshold, Integer numbUnk, Integer numbNegative,
            FileOutputStream fold_stats) throws IOException, Exception {

        prop = new Properties();
        prop.load(new FileReader("config.properties"));
        TDM_taxonomy = prop.getProperty("projectPath") + "/" + prop.getProperty("TDM_taxonomy");

        // The full taxonomy obtains from paper with code json
        FileOutputStream output = new FileOutputStream(b+"trainOutput.tsv");

        File dir = new File(pdfDir);

        File[] filesList = dir.listFiles();

        int progress = 0;
        Integer trueUnk = 0;
        Integer falseUnk = 0;

        for (File file : filesList) {
            if (file.isFile()) {

//                String pdf_file = FileUtils.readFileToString(file);
                String pdf_file = file.getPath();
//                String pdf_file pdf_file = file;
                Set<String> trueTDM = new HashSet<String>();
                System.out.println(">>>> ("+ progress++ +") Processing file: " + pdf_file);

                // String docTEATStr = DocTAET.getDocTAETRepresentation150(pdf_file);
                String docTEATStr = DocTAET.getDocTAETRepresentation450(pdf_file);
                // String docTEATStr = DocTAET.getDocTAETRepresentationFull(pdf_file);
                // String docTEATStr = DocTAET.getDocTAETRepresentationTitleAbstract(pdf_file);
                // String docTEATStr = DocTAET.getDocTAETRepresentationAbstract(pdf_file);
                // String docTEATStr = DocTAET.getDocTAETRepresentationTitleAbstractExpSetup(pdf_file);
                // String docTEATStr = DocTAET.getDocTAETRepresentationTitleAbstractTableInfo(pdf_file);
                if (docTEATStr.equals("")) {
                    System.err.print("PDF parsing error!");
                }
                else {
                    // This contains a dict like file that have file name and TDMs informations
                    resultsAnnotation = prop.getProperty("projectPath") + "/" + prop.getProperty("result_annotation");
                    // resultsAnnotation = "/home/salomon/Desktop/task-dataset-metric-extraction/data/paperwithcode/annotations/resultsAnnotation.tsv";

//                    String labels_file = args[1]+"/tdmGoldLabels.tsv";
                    mapDict = getTdmGoldLabelsAndloadDict(TDM_taxonomy, b, threshold);
                    mapLabel = getMapTitlepdfLabel(resultsAnnotation, mapDict, threshold);

                    String labels_file = b+"/tdmGoldLabels.tsv";
                    List<String> labels = FileUtils.readLines(new File(labels_file));
                    String pdf_filename = new File(pdf_file).getName();

                    for (int i = 0; i < mapLabel.get(pdf_filename).size(); i++) {

                        if (mapLabel.get(pdf_filename).get(i).replace("#", "; ").equals("unknow")){

                            if (trueUnk >= numbUnk){

                                continue;

                            }
                            else {
                                output.write(("true\t" + pdf_filename + "\t"+mapLabel.get(pdf_filename).get(i).replace("#", "; ")+"\t" + docTEATStr + "\n").getBytes());
                                
                                // Keep track of positive TDM
                                trueTDM.add(mapLabel.get(pdf_filename).get(i));

                                trueUnk = trueUnk + 1;
                                
                                continue;
                            }


                        }

                        output.write(("true\t" + pdf_filename + "\t"+mapLabel.get(pdf_filename).get(i).replace("#", "; ")+"\t" + docTEATStr + "\n").getBytes());

                        // Keep track of positive TDM
                        trueTDM.add(mapLabel.get(pdf_filename).get(i));


                    }


                    // Thits takes only the first numbNegative example, we may need to make it random
                    int limit = 0;

                    // To randomly allow to get the false for a numNeg threshold
                    Collections.shuffle(labels);

                    for (String label : labels) {
                        if ( limit >= numbNegative ){
                            break;
                        }

                        // Check if the TDM is not a true label for a given paper
                        if (!trueTDM.contains(label)) {
                            output.write(("false\t" + pdf_filename + "\t" + label.replace("#", "; ") + "\t" + docTEATStr + "\n").getBytes());
                            }

                            // Update the count for one more false label
                            limit += 1;
                        }


                }
            }
        }

        fold_stats.write(("Main Data stats :\n").getBytes());
        fold_stats.write(("Positive Unk : "+trueUnk+"\n").getBytes());
        fold_stats.write(("Negative Unk : "+falseUnk+"\n\n").getBytes());

        output.close();

        return "Done";
    }


    //    public static String getTrainingData(String pdfDir, String[] args) throws IOException, Exception {
//     public static String getTrainScoreData(String pdfDir, String b, Integer threshold, Integer numbNegative) throws IOException, Exception {

//        prop = new Properties();
// //        prop.load(new FileReader("config.properties"));
// //        TDM_taxonomy = prop.getProperty("TDM_taxonomy");
//         // TDM_taxonomy = "D:\\ORKG\\NLP\\task-dataset-metric-extraction\\data\\paperwithcode\\annotations\\TDM_taxonomy.tsv";
//         // TDM_taxonomy = "/home/salomon/Desktop/task-dataset-metric-extraction/data/paperwithcode/annotations/TDM_taxonomy.tsv";

//         FileOutputStream output = new FileOutputStream(b+"trainOutput.tsv");
// //        FileOutputStream output = new FileOutputStream(args[1]+"trainOutput.tsv");

//         File dir = new File(pdfDir);

//         File[] filesList = dir.listFiles();

//         for (File file : filesList) {
//             if (file.isFile()) {

// //                String pdf_file = FileUtils.readFileToString(file);
//                 String pdf_file = file.getPath();
// //                String pdf_file pdf_file = file;
//                 Set<String> trueTDM = new HashSet<String>();
//                 System.out.println(">>>> Processing file: " + pdf_file);


//                 String docTEATStr = DocTAET.getDocTAETRepresentation(pdf_file);
//                 if (docTEATStr.equals("")) {
//                     System.err.print("PDF parsing error!");
//                 }
//                 else {
//                     resultsAnnotation = prop.getProperty("result_annotation");
//                     // resultsAnnotation = "D:\\ORKG\\NLP\\task-dataset-metric-extraction\\data\\paperwithcode\\annotations\\resultsAnnotation.tsv";
//                     // resultsAnnotation = "/home/salomon/Desktop/task-dataset-metric-extraction/data/paperwithcode/annotations/resultsAnnotation.tsv";

// //                    String labels_file = args[1]+"/tdmGoldLabels.tsv";
//                     mapDict = getTdmGoldLabelsAndloadDict(TDM_taxonomy, b, threshold);
//                     mapLabel = getMapTitlepdfLabel(resultsAnnotation, mapDict, threshold);
//                     mapScore = getTrainScoreDict(resultsAnnotation, mapDict, threshold);

//                     String labels_file = b+"/tdmGoldLabels.tsv";
//                     List<String> labels = FileUtils.readLines(new File(labels_file));
//                     String pdf_filename = new File(pdf_file).getName();


//                     for (int i = 0; i < mapLabel.get(pdf_filename).size(); i++) {
//                         output.write(("true\t" + pdf_filename + "\t"+mapLabel.get(pdf_filename).get(i).replace("#", ", ")+"\t" + docTEATStr + "\n").getBytes());
//                         trueTDM.add(mapLabel.get(pdf_filename).get(i));
//                     }

//                     int limit = 0;


//                     // To randomly allow to get the false for a numNeg threshold
//                     Collections.shuffle(labels);

//                     for (String label : labels) {
//                         if (limit>=numbNegative){
//                             break;
//                         }
//                         if (!trueTDM.contains(label)) {
//                             output.write(("false\t" + pdf_filename + "\t" + label.replace("#", ", ") + "\t" + docTEATStr + "\n").getBytes());
//                             limit += 1;
//                         }

//                     }

//                 }
//             }
//         }

//         output.close();

//         return "Done";
//     }

    private static HashMap<String, ArrayList<String>> getMapTitlepdfLabel(String resultsAnnotation, HashMap<String, Integer> mapDict, Integer threshold) throws ParseException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(resultsAnnotation));
        String line = null;
        HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();

        String TDM;

        Integer j = 0;
        while ((line = br.readLine()) != null) {
            if (line.length() != 0) {
                j += 1;
                String str[] = line.split("\t");

                if (str.length <= 1) {
                    continue;
                }

                String TDMs[] = str[1].split("\\$");

    //            String preTDM = str[1].split("#")[0];

                for (int i = 0; i < TDMs.length; i++) {
    //                String preTDM = TDMs[i].substring(TDMs[i].lastIndexOf("#") + 1);
                    int sepPos = TDMs[i].lastIndexOf("#");
                    if (sepPos == -1) {
                        System.out.println("");
                    }

                    String preTDM = TDMs[i].substring(0, sepPos);
                    if (!mapDict.containsKey(preTDM)){
                        continue;
                    }
                    if (mapDict.get(preTDM) >= threshold) {
                        TDM = preTDM;
                    } else {
                        TDM = "unknow";
                    }

                    ArrayList<String> list;
                    if(map.containsKey(str[0]) && map.get(str[0]).get(0) != "unknow" ){
                        // if the key has already been used,
                        // we'll just grab the array list and add the value to it
                        list = map.get(str[0]);
                        list.add(TDM);
                    } else {
                        // if the key hasn't been used yet,
                        // we'll create a new ArrayList<String> object, add the value
                        // and put it in the array list with the new key
                        list = new ArrayList<String>();
                        list.add(TDM);
                        map.put(str[0], list);
                    }
                }
            }
        }

        br.close();

        return map;

    }

    private static HashMap<String,Integer> getTdmGoldLabelsAndloadDict(String tdm_taxonomy, String b, Integer threshold) throws ParseException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(tdm_taxonomy));
        String line =  null;
        HashMap<String,Integer> map = new HashMap<String, Integer>();

        while((line=br.readLine())!=null){
            String str[] = line.split("\t");
            map.put(str[0], Integer.parseInt(str[1]));
        }

        PrintWriter out = new PrintWriter(b+"tdmGoldLabels.tsv");

        for ( Map.Entry<String, Integer> entry : map.entrySet() ) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            if (value>=threshold){
                out.println(key);
            }
        }
        out.println("unknow");
        out.close();
        
        br.close();
        return  map;
    }

    private static HashMap<String, ArrayList<Integer>> getTrainScoreDict(String resultsAnnotation, HashMap<String, Integer> mapDict, Integer threshold) throws ParseException, IOException {

//    private static HashMap<String, ArrayList<String>> getMapTitlepdfLabel(String resultsAnnotation, HashMap<String, Integer> mapDict, Integer threshold) throws ParseException, IOException {


//        BufferedReader br = new BufferedReader(new FileReader(tdm_taxonomy));
//        String line =  null;
//        HashMap<String,Integer> map = new HashMap<String, Integer>();
//
//        while((line=br.readLine())!=null){
//            String str[] = line.split("\t");
//            map.put(str[0], Integer.parseInt(str[1]));
//        }
//
//        PrintWriter out = new PrintWriter(b+"tdmGoldLabels.tsv");
//
//        for ( Map.Entry<String, Integer> entry : map.entrySet() ) {
//            String key = entry.getKey();
//            Integer value = entry.getValue();
//            if (value>=threshold){
//                out.println(key);
//            }
//        }
//        out.println("unknow");
//        out.close();
//
//        return  map;



        BufferedReader br = new BufferedReader(new FileReader(resultsAnnotation));
        String line = null;
//        HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
        HashMap<String,ArrayList<Integer>> map = new HashMap<String, ArrayList<Integer>>();

        String TDM;

        Integer j = 0;
        while ((line = br.readLine()) != null) {
            if (line.length() != 0) {
                j += 1;
                String str[] = line.split("\t");

                String TDMs[] = str[1].split("\\$");

                //            String preTDM = str[1].split("#")[0];

                for (int i = 0; i < TDMs.length; i++) {
                    //                String preTDM = TDMs[i].substring(TDMs[i].lastIndexOf("#") + 1);
                    int sepPos = TDMs[i].lastIndexOf("#");
                    if (sepPos == -1) {
                        System.out.println("");
                    }

                    String preTDM = TDMs[i].substring(0, sepPos);

                    if (mapDict.get(preTDM) >= threshold) {
                        TDM = preTDM;
                    } else {
                        TDM = "unknow";
                    }

//                    ArrayList<Integer> list;
//                    if(map.containsKey(str[0]) && map.get(str[0]).get(0) != "unknow" ){
//                        // if the key has already been used,
//                        // we'll just grab the array list and add the value to it
//                        list = map.get(str[0]);
//                        list.add(TDM);
//                    } else {
//                        // if the key hasn't been used yet,
//                        // we'll create a new ArrayList<String> object, add the value
//                        // and put it in the array list with the new key
//                        list = new ArrayList<String>();
//                        list.add(TDM);
//                        map.put(str[0], list);
//                    }
                }
            }
        }

        br.close();

        return map;

    }


    //    public static String getTestingData(String pdfDir, String[] args) throws IOException, Exception {
    public static String getTestFromFolderData(String pdfDir, String b) throws IOException, Exception {

        System.out.println("## Now Generating Test Data ##");

        FileOutputStream output = new FileOutputStream(b+"testOutput.tsv");

        File dir = new File(pdfDir);

        File[] filesList = dir.listFiles();

        for (File file : filesList) {
            if (file.isFile()) {

//                String pdf_file = FileUtils.readFileToString(file);
                String pdf_file = file.getPath();

                System.out.println(">>>> Processing file: " + pdf_file);


                // String docTEATStr = DocTAET.getDocTAETRepresentation150(pdf_file);
                String docTEATStr = DocTAET.getDocTAETRepresentation450(pdf_file);
                // String docTEATStr = DocTAET.getDocTAETRepresentationFull(pdf_file);
                // String docTEATStr = DocTAET.getDocTAETRepresentationTitleAbstract(pdf_file);
                // String docTEATStr = DocTAET.getDocTAETRepresentationAbstract(pdf_file);
                // String docTEATStr = DocTAET.getDocTAETRepresentationTitleAbstractExpSetup(pdf_file);
                // String docTEATStr = DocTAET.getDocTAETRepresentationTitleAbstractTableInfo(pdf_file);
                if (docTEATStr.equals("")) {
                    System.err.print("PDF parsing error!");
                }
                else {
                    String labels_file = b+"/tdmGoldLabels.tsv";
                    List<String> labels = FileUtils.readLines(new File(labels_file));
                    String pdf_filename = new File(pdf_file).getName();

                    for (String label : labels) {
                        output.write(("true\t" + pdf_filename + "\t"+label.replace("#", "; ")+"\t" + docTEATStr + "\n").getBytes());
                    }
                }
            }
        }

        output.close();



        return "Done";
    }

    public static String getTestFromFileData(String pdfFile, String b, String pathToTDMGold) throws IOException, Exception {

        System.out.println("## Now Generating Test Data ##");

        FileOutputStream output = new FileOutputStream(b+"testOutput.tsv");
//        FileOutputStream output = new FileOutputStream(args[2]+"testOutput.tsv");


        System.out.println(">>>> Processing file: " + pdfFile);


        // String docTEATStr = DocTAET.getDocTAETRepresentation150(pdfFile);
        String docTEATStr = DocTAET.getDocTAETRepresentation450(pdfFile);
        // String docTEATStr = DocTAET.getDocTAETRepresentationFull(pdfFile);
        // String docTEATStr = DocTAET.getDocTAETRepresentationTitleAbstract(pdfFile);
        // String docTEATStr = DocTAET.getDocTAETRepresentationAbstract(pdfFile);
        // String docTEATStr = DocTAET.getDocTAETRepresentationTitleAbstractExpSetup(pdfFile);
        // String docTEATStr = DocTAET.getDocTAETRepresentationTitleAbstractTableInfo(pdfFile);
        if (docTEATStr.equals("")) {
            System.err.print("PDF parsing error!");
        }
        else {
            String labels_file = pathToTDMGold+"tdmGoldLabels.tsv"; // tdmGoldLabels
            List<String> labels = FileUtils.readLines(new File(labels_file));
            String pdf_filename = new File(pdfFile).getName();

            for (String label : labels) {
                output.write(("true\t" + pdf_filename + "\t"+label.replace("#", "; ")+"\t" + docTEATStr + "\n").getBytes());
            }
        }

        output.close();

        return "Done";
    }
}
