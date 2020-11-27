package eu.tib.sre.utils;

import eu.tib.sre.pdfparser.DocTAET;
import eu.tib.sre.pdfparser.GrobidPDFProcessor;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.text.ParseException;
import java.util.*;

public class DatasetGeneration {

    private Properties prop;
    private static String TDM_taxonomy;
    private static String resultsAnnotation;
    private  static HashMap<String,Integer> mapDict;
    private  static HashMap<String,ArrayList<String>> mapLabel;



    public Properties getProperties() {
        return prop;
    }

    ;

//    public static String getTrainingData(String pdfDir, String[] args) throws IOException, Exception {
    public static String getTrainData(String pdfDir, String b, Integer threshold, Integer numbNegative) throws IOException, Exception {

//        prop = new Properties();
//        prop.load(new FileReader("config.properties"));
//        TDM_taxonomy = prop.getProperty("TDM_taxonomy");
        TDM_taxonomy = "D:\\ORKG\\NLP\\task-dataset-metric-extraction\\data\\paperwithcode\\\\annotations\\\\TDM_taxonomy.tsv";

        FileOutputStream output = new FileOutputStream(b+"trainOutput.tsv");
//        FileOutputStream output = new FileOutputStream(args[1]+"trainOutput.tsv");

        File dir = new File(pdfDir);

        File[] filesList = dir.listFiles();

        for (File file : filesList) {
            if (file.isFile()) {

//                String pdf_file = FileUtils.readFileToString(file);
                String pdf_file = file.getPath();
//                String pdf_file pdf_file = file;
                Set<String> trueTDM = new HashSet<String>();
                System.out.println(">>>> Processing file: " + pdf_file);


                String docTEATStr = DocTAET.getDocTAETRepresentation(pdf_file);
                if (docTEATStr.equals("")) {
                    System.err.print("PDF parsing error!");
                }
                else {
                    // resultsAnnotation = prop.getProperty("result_annotation")
                    resultsAnnotation = "D:\\ORKG\\NLP\\task-dataset-metric-extraction\\data\\paperwithcode\\annotations\\resultsAnnotation.tsv";

//                    String labels_file = args[1]+"/tdmGoldLabels.tsv";
                    mapDict = getTdmGoldLabelsAndloadDict(TDM_taxonomy, b, threshold);
                    mapLabel = getMapTitlepdfLabel(resultsAnnotation, mapDict, threshold);

                    String labels_file = b+"/tdmGoldLabels.tsv";
                    List<String> labels = FileUtils.readLines(new File(labels_file));
                    String pdf_filename = new File(pdf_file).getName();


                    for (int i = 0; i < mapLabel.get(pdf_filename).size(); i++) {
                        output.write(("true\t" + pdf_filename + "\t"+mapLabel.get(pdf_filename).get(i)+"\t" + docTEATStr + "\n").getBytes());
                        trueTDM.add(mapLabel.get(pdf_filename).get(i));
                    }

                    int limit = 0;
                    for (String label : labels) {
                        if (limit>=numbNegative){
                            break;
                        }
                        if (!trueTDM.contains(label)) {
                            output.write(("False\t" + pdf_filename + "\t" + label + "\t" + docTEATStr + "\n").getBytes());
                        }
                        limit += 1;
                    }

                }
            }
        }

        output.close();

        return "Done";
    }

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

//                    if (j == 2583) {
//                        System.out.println("Usage: java eu.tib.sre.Main.java <pdf-file-path> <resources-dir>");
//                    }

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
//                    map.put(str[0], TDM);
                }
            }
        }
        return map;

//        BufferedReader br = new BufferedReader(new FileReader(tdm_taxonomy));
//        String line =  null;
//        HashMap<String,Integer> map = new HashMap<String, Integer>();
//
//        while((line=br.readLine())!=null){
//            String str[] = line.split("\t");
//            map.put(str[0], Integer.parseInt(str[1]));
////            for(int i=1;i<str.length;i++){
////                String arr[] = str[i].split("\t");
////                map.put(arr[0], Integer.parseInt(arr[1]));
////            }
//        }

    }

    private static HashMap<String,Integer> getTdmGoldLabelsAndloadDict(String tdm_taxonomy, String b, Integer threshold) throws ParseException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(tdm_taxonomy));
        String line =  null;
        HashMap<String,Integer> map = new HashMap<String, Integer>();

        while((line=br.readLine())!=null){
            String str[] = line.split("\t");
            map.put(str[0], Integer.parseInt(str[1]));
//            for(int i=1;i<str.length;i++){
//                String arr[] = str[i].split("\t");
//                map.put(arr[0], Integer.parseInt(arr[1]));
//            }
        }

        PrintWriter out = new PrintWriter(b+"tdmGoldLabels.tsv");
//
        System.out.print("\n Section Info \n");

        for ( Map.Entry<String, Integer> entry : map.entrySet() ) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            if (value>=threshold){
                out.println(key);
            }
        }
        out.close();

        return  map;
    }

//    public static String getTestingData(String pdfDir) throws IOException, Exception {
//        GrobidPDFProcessor gp = GrobidPDFProcessor.getInstance();
//        Map<String, String> sections = gp.getPDFSectionAndText(pdfFile);
//        return sections.get("title");
////        DocTAET
//    }

    //    public static String getTestingData(String pdfDir, String[] args) throws IOException, Exception {
    public static String getTestData(String pdfDir, String b) throws IOException, Exception {

        FileOutputStream output = new FileOutputStream(b+"testOutput.tsv");
//        FileOutputStream output = new FileOutputStream(args[2]+"testOutput.tsv");

        File dir = new File(pdfDir);

        File[] filesList = dir.listFiles();

        for (File file : filesList) {
            if (file.isFile()) {

//                String pdf_file = FileUtils.readFileToString(file);
                String pdf_file = file.getPath();
//                String pdf_file pdf_file = file;

                System.out.println(">>>> Processing file: " + pdf_file);


                String docTEATStr = DocTAET.getDocTAETRepresentation(pdf_file);
                if (docTEATStr.equals("")) {
                    System.err.print("PDF parsing error!");
                }
                else {
//                    String labels_file = args[1]+"/tdmGoldLabels.tsv";
                    String labels_file = b+"/tdmGoldLabels.tsv";
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
}
