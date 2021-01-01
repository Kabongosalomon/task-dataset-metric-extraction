package eu.tib.sre.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author jld
 */
public class TwoFoldCrossValidation {

    static Map<String, List<String>> data = new HashMap<>();
    static List<String> tdms = new ArrayList<>();

    public static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public static Map<Integer, List<Integer>> getPerFoldTestIndexes(int min_limit, int max_limit, int total_numbers) {
        Set<Integer> seen = new HashSet<>();
        Map<Integer, List<Integer>> perfoldTestInstances = new HashMap<>();
        Random random = new Random();
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
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

    public static void main(String[] args) throws IOException {
        /*if (args.length == 1) {
            System.out.println("Usage: java TwoFoldCrossValidation.java <data-file>");
            System.exit(-1);
        }*/
//        String data_file = //args[0];
//        //"C:\\Users\\DSouzaJ\\Desktop\\Datasets\\paperswithcode\\paperswithcodeTrainingData.tsv";
//                //"C:\\Users\\DSouzaJ\\Desktop\\Datasets\\paperswithcode\\paperswithcodeFullData.tsv";
//                //"C:\\Users\\DSouzaJ\\Desktop\\Datasets\\paperswithcode\\paperswithcode500UnkData.tsv";
//        "D:\\ORKG\\NLP\\task-dataset-metric-extraction\\data\\paperswithcodedatawith600unk\\paperswithcode600UnkDataTitleAbstract.tsv";

        String data_file =
                "U:\\Documents\\ORKG\\NLP\\task-dataset-metric-extraction\\data\\paperswithcodedatawith600unk\\paperswithcode600UnkDataTitleAbstract.tsv";

        String[] lines = readFile(data_file, StandardCharsets.UTF_8).split("\\n");

        for (String line : lines) {
            line = line.trim();
            String[] tokens = line.split("\t");
            //System.out.println(tokens.length);

            List<String> dataLines = data.get(tokens[1]);
            if (dataLines == null) data.put(tokens[1], dataLines = new ArrayList<>());
            dataLines.add(line);
            if (!tdms.contains(tokens[2])) tdms.add(tokens[2]);
        }

        int datasize = data.keySet().size();
        int training_datasize = (int)(0.7 * datasize)+1;
        int test_datasize = (int)(0.3 * datasize);

        //System.out.println(datasize);
        //System.out.println(training_datasize);
        //System.out.println(test_datasize);
        //System.exit(-1);

        Map<Integer, List<Integer>> perfoldTestIndexes = getPerFoldTestIndexes(0, datasize, test_datasize);

//        String outputDir = //"C:\\Users\\DSouzaJ\\Desktop\\Datasets\\paperswithcode\\twofold";
//                "D:\\ORKG\\NLP\\task-dataset-metric-extraction\\src\\main\\resources\\twofoldwithunk";
        String outputDir = //"C:\\Users\\DSouzaJ\\Desktop\\Datasets\\paperswithcode\\twofold";
                "U:\\Documents\\ORKG\\NLP\\task-dataset-metric-extraction\\src\\main\\resources\\twofoldwithunk";


        List<String> dataFiles = new ArrayList<>(data.keySet());

        for (int fold : perfoldTestIndexes.keySet()) {

            FileOutputStream train_output = new FileOutputStream(outputDir+"\\fold"+fold+"\\train.tsv");
            FileOutputStream test_output = new FileOutputStream(outputDir+"\\fold"+fold+"\\test.tsv");
            FileOutputStream test_indexes = new FileOutputStream(outputDir+"\\fold"+fold+"\\test_indexes.tsv");

            List<Integer> testIndexes = perfoldTestIndexes.get(fold);

            for (int i = 0; i < datasize; i++) {
                String file = dataFiles.get(i);
                List<String> dataLines = data.get(file);
                if (testIndexes.contains(i)) {
                    writeOutput(test_output, file, dataLines);
                    test_indexes.write((i+"\n").getBytes());
                }
                else {
                    writeOutput(train_output, file, dataLines);
                }
            }
        }
    }

    public static void writeOutput(FileOutputStream output, String filename, List<String> dataLines) throws IOException {
        String line = "";
        List<String> trueTDM = new ArrayList<>();
        for (String dataLine : dataLines) {
            if (line.equals("")) {
                //System.out.println(dataLine);
                line = dataLine.split("\t")[3];
            }
            trueTDM.add(dataLine.split("\t")[2]);
            output.write((dataLine+"\n").getBytes());
        }

        for (String tdm : tdms) {
            if (trueTDM.contains(tdm)) continue;
            output.write(("false\t"+filename+"\t"+tdm+"\t"+line+"\n").getBytes());
        }
    }

}