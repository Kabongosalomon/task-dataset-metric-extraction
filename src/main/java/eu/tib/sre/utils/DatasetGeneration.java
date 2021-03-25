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
    private static String TDMs_taxonomy;
    private static String resultsAnnotation;
    private static HashMap<String, Integer> mapDict;
    private static HashMap<String, ArrayList<String>> mapLabel;
    private static HashMap<String, ArrayList<String>> mapLabelScore;

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
        TDMs_taxonomy = prop.getProperty("projectPath") + "/" + prop.getProperty("TDMs_taxonomy");

        // The full taxonomy obtains from paper with code json


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

                String docTEATStr = DocTAET.getDocTAETRepresentation(pdf_file);
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
        TDMs_taxonomy = prop.getProperty("projectPath") + "/" + prop.getProperty("TDMs_taxonomy");

        // The full taxonomy obtains from paper with code json
        FileOutputStream output = new FileOutputStream(b+"trainOutput.tsv");

        File dir = new File(pdfDir);

        File[] filesList = dir.listFiles();

        int progress = 0;
        Integer trueUnk = 0;
        Integer falseUnk = 0;

        fold_stats.write(("Papers with no true label :\n").getBytes());

        for (File file : filesList) {
            if (file.isFile()) {

//                String pdf_file = FileUtils.readFileToString(file);
                String pdf_file = file.getPath();
//                String pdf_file pdf_file = file;
                Set<String> trueTDMs = new HashSet<String>();
                System.out.println(">>>> ("+ progress++ +") Processing file: " + pdf_file);

                String docTEATStr = DocTAET.getDocTAETRepresentation(pdf_file);
                List<String> numbersAndContext = DocTAET.getTableBoldNumberContext(pdf_file);
                
                // String str = "";
                // // for (String DMLabel : evaluatedLabels) {
                // for (String numberInfo : numbersAndContext) {
                //     str += " " + numberInfo.split("#")[1];
                // }
                //         // }

                // String tableContents  = str.strip();
                
                // /*
                // * Since the words are separated by space,
                // * we will split the string by one or more space
                // */
                
                // String[] strWords = tableContents.split("\\s+");
                
                // //convert String array to LinkedHashSet to remove duplicates
                // LinkedHashSet<String> lhSetWords 
                //     = new LinkedHashSet<String>( Arrays.asList(strWords) );
                
                // //join the words again by space
                // StringBuilder sbTemp = new StringBuilder();
                // int index = 0;
                
                // for(String s : lhSetWords){
                    
                //     if(index > 0)
                //         sbTemp.append(" ");
                
                //     sbTemp.append(s);
                //     index++;
                // }
                
                // // Get a content of the table from table caption 
                // // by avoiding repeted informations
                // tableContents = sbTemp.toString();

                if (docTEATStr.equals("")) {
                    System.err.print("PDF parsing error!");
                }
                else {
                    // This contains a dict like file that have file name and TDMs informations
                    resultsAnnotation = prop.getProperty("projectPath") + "/" + prop.getProperty("result_annotation");

                    mapDict = getTdmGoldLabelsAndloadDict(TDMs_taxonomy, b, threshold);
                    mapLabelScore = getMapTitlepdfLabelScore(resultsAnnotation, mapDict, threshold);

                    String labels_file = b+"/tdmGoldLabels.tsv";
                    String pdf_filename = new File(pdf_file).getName();
                    List<String> labels = mapLabelScore.get(pdf_filename); //FileUtils.readLines(new File(labels_file));
                    List<String> Goldlabels = FileUtils.readLines(new File(labels_file));

                    Boolean missedPdF = false; 
                    Collections.shuffle(Goldlabels);
                    for (String label : labels) {
                        String preDM[] = label.replace("#", "; ").split(";");
                        String DM = preDM[1].trim()+"; "+preDM[2].trim();
                        String score = preDM[3];

                        

                        int limit = 0;

                        

                        score = score.replaceAll("[^\\.0123456789]","");

                        for (String numberInfo : numbersAndContext) {
                            if (numberInfo.split("#").length >1){
                                if (score.equals(numberInfo.split("#")[0])){
                                    output.write(("true\t" + pdf_filename +"#"+score+ "\t" + DM + "\t" + numberInfo.split("#")[1] + "\n").getBytes());
                                    missedPdF = true;
                                }
                                else {
                                    if ( limit >= numbNegative ){
                                        continue;
                                    }

                                    Random randomizer = new Random();
                                    String randomGoldlabels = Goldlabels.get(randomizer.nextInt(Goldlabels.size()));
                                    String preGoldDM[] = randomGoldlabels.replace("#", "; ").split(";");
                                    
                                    String randGoldDM;
                                    
                                    if (preGoldDM.length !=1) {
                                        randGoldDM = preGoldDM[1].trim()+"; "+preGoldDM[2].trim();
                                    }
                                    else{
                                        randGoldDM = "unknow";
                                    }

                                    if (randGoldDM.equals(DM) || trueTDMs.contains(randGoldDM)) {
                                        while (!(randGoldDM.equals(DM)) && 
                                                randGoldDM.equals("unknow") &&
                                                !(trueTDMs.contains(randGoldDM))){
                                            randomGoldlabels = Goldlabels.get(new Random().nextInt(Goldlabels.size()));
                                            preGoldDM = randomGoldlabels.replace("#", "; ").split(";");
                                            randGoldDM = preGoldDM[1].trim()+"; "+preGoldDM[2].trim();
                                        }
                                    }

                                    output.write(("false\t" + pdf_filename +"#"+numberInfo.split("#")[0].replaceAll("[^\\.0123456789]","")+ "\t" + randGoldDM + "\t" + numberInfo.split("#")[1] + "\n").getBytes());
                                    
                                    limit += 1;
                                    trueTDMs.add(randGoldDM);
                                    }                           
                                }
                            }
                        }

                    if (!missedPdF){                    
                        fold_stats.write((pdf_filename + "\t" + mapLabelScore.get(pdf_filename) + "\t" +numbersAndContext+ "\n").getBytes());
                    }
                    

                    // for (int i = 0; i < mapLabelScore.get(pdf_filename).size(); i++) {

                    //     if (mapLabelScore.get(pdf_filename).get(i).replace("#", "; ").equals("unknow")){
                            
                    //         continue;

                    //     }

                    //     String preDM[] = mapLabelScore.get(pdf_filename).get(i).replace("#", ";").split(";");
                    //     String DM = preDM[1].trim()+"; "+preDM[2].trim();
                    //     String score = preDM[3];
                    //     score = score.replaceAll("[^\\.0123456789]","");
                        
                    //     // // make a for loop on the numbersAndContext 
                    //     // output.write(("true\t" + pdf_filename +"#"+score+"\t"+DM+"\t" + tableContents + "\n").getBytes());
                        
                    //     int sepPos = mapLabelScore.get(pdf_filename).get(i).lastIndexOf("#");
                    //     if (sepPos == -1) {
                    //         System.out.println("");
                    //     }

                    //     String TDM = mapLabelScore.get(pdf_filename).get(i).substring(0, sepPos);


                    //     // // Keep track of positive TDM
                    //     trueTDMs.add(mapLabelScore.get(pdf_filename).get(i).replace("#", ";"));
                    // }


                    // // Thits takes only the first numbNegative example, we may need to make it random
                    // int limit = 0;

                    // // To randomly allow to get the false for a numNeg threshold
                    // Collections.shuffle(labels);

                    // for (String label : labels) {
                    //     if ( limit >= numbNegative ){
                    //         break;
                    //     }

                    //     // Check if the TDM is not a true label for a given paper
                    //     if (!trueTDMs.contains(label)) {

                    //         String preDM[] = label.replace("#", "; ").split(";");
                    //         String DM = preDM[1].trim()+"; "+preDM[2].trim();
                    //         String score = preDM[3];

                    //         score = score.replaceAll("[^\\.0123456789]","");

                            
                        
                            
                            
                    //         for (String numberInfo : numbersAndContext) {
                    //             if (score.equals(numberInfo.split("#")[0])){
                    //                 output.write(("true\t" + pdf_filename +"#"+score+ "\t" + DM + "\t" + numberInfo.split("#")[1] + "\n").getBytes());
                    //             }
                    //             else {
                    //                 output.write(("false\t" + pdf_filename +"#"+score+ "\t" + DM + "\t" + numberInfo.split("#")[1] + "\n").getBytes());
                    //                 // sb1.append("true" + "\t" + filename + "#" + numberInfo.split("#")[0] + "\t" + DMLabel + "\t" + numberInfo.split("#")[1]).append("\n");
                    //                 }                           
                    //             }
                    //         if (label.replace("#", "; ").equals("unknow")){
                    //             falseUnk = falseUnk + 1;
                    //         }

                    //         // Update the count for one more false label
                    //         limit += 1;
                    //     }

                    // }

                }
            }
        }

        // fold_stats.write(("Main Data stats :\n").getBytes());
        // fold_stats.write(("Positive Unk : "+trueUnk+"\n").getBytes());
        // fold_stats.write(("Negative Unk : "+falseUnk+"\n\n").getBytes());

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

    private static HashMap<String, ArrayList<String>> getMapTitlepdfLabelScore(String resultsAnnotation, HashMap<String, Integer> mapDict, Integer threshold) throws ParseException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(resultsAnnotation));
        String line = null;
        HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();

        String TDMS;

        Integer j = 0;
        while ((line = br.readLine()) != null) {
            if (line.length() != 0) {
                j += 1;
                // Get a paper at 0 and the TDMS at 1. [0, 1]
                String str[] = line.split("\t");

                String TDMs[] = str[1].split("\\$");

    //            String preTDM = str[1].split("#")[0];

                for (int i = 0; i < TDMs.length; i++) {
    //                String preTDM = TDMs[i].substring(TDMs[i].lastIndexOf("#") + 1);
                    int sepPos = TDMs[i].lastIndexOf("#");
                    if (sepPos == -1) {
                        System.out.println("");
                    }

                    String preTDMCheck = TDMs[i];

                    // String preTDMCheck = TDMs[i].substring(0, sepPos);
                    String preTDM = TDMs[i].split("\n")[0]; 
                    
                    // String preDM[] = TDMs[i].split("#");

                    // // TODO : Shoud I have a difference dic for Datasetmetrick ? 
                    // if (mapDict.get(preTDMCheck) >= threshold) {
                    //     TDMS = preTDM;
                    // } else {
                    //     // TDMS = "unknow";
                    //     continue;
                    // }
                    
                    // No unknow class for score 
                    TDMS = preTDM;

                    ArrayList<String> list;
                    if(map.containsKey(str[0]) && map.get(str[0]).get(0) != "unknow" ){
                        // if the key has already been used,
                        // we'll just grab the array list and add the value to it
                        list = map.get(str[0]);
                        list.add(TDMS);
                    } else {
                        // if the key hasn't been used yet,
                        // we'll create a new ArrayList<String> object, add the value
                        // and put it in the array list with the new key
                        list = new ArrayList<String>();
                        list.add(TDMS);
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


                String docTEATStr = DocTAET.getDocTAETRepresentation(pdf_file);
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

    public static String getTestFromFileData(String pdfFile, String b) throws IOException, Exception {

        System.out.println("## Now Generating Test Data ##");

        FileOutputStream output = new FileOutputStream(b+"testOutput.tsv");
//        FileOutputStream output = new FileOutputStream(args[2]+"testOutput.tsv");


        System.out.println(">>>> Processing file: " + pdfFile);


        String docTEATStr = DocTAET.getDocTAETRepresentation(pdfFile);
        if (docTEATStr.equals("")) {
            System.err.print("PDF parsing error!");
        }
        else {
            String labels_file = b+"/tdmGoldLabels.tsv"; // tdmGoldLabels
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
