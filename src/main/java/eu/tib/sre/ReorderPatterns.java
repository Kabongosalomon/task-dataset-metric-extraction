package eu.tib.sre;

// import edu.stanford.nlp.simple.Document;
// import edu.stanford.nlp.simple.Sentence;
// import scala.sys.process.ProcessBuilderImpl;
// import shadedwipo.org.apache.commons.io.FileUtils;

// import java.io.File;
// import java.io.FileOutputStream;
// import java.io.IOException;
// import java.util.*;

/**
 * @author jld
 */
public class ReorderPatterns {

//    public static void main(String[] args) throws IOException {
//        String dir_str = "C:\\Users\\DSouzaJ\\Desktop\\Datasets\\paperswithcode\\nlp";
//        int i = 0;
//        while (i <= 35) {
//            File main_dir = new File(dir_str+"\\"+i);
//            Map<String, Integer> sentence_index = new HashMap<>();
//            for (File dir : main_dir.listFiles()) {
//                if (dir.isDirectory()) {
//                    if (!dir.getName().equals("txt")) {
//                        continue;
//                    }
//                    File txt_file = dir.listFiles()[0];
//                    String txt_str = FileUtils.readFileToString(txt_file);
//                    Document doc = new Document(txt_str);
//                    int index = 0;
//                    for (Sentence sent : doc.sentences()) {
//                        String sent_str = sent.toString().replaceAll("\\n", " ");
//                        sentence_index.put(sent_str, index);
//                        index++;
//                    }
//                }
//            }
//
//            FileOutputStream output = new FileOutputStream(dir_str+"\\"+i+"\\"+i+"-ordered.txt");
//            File data_file = new File(main_dir.toString()+"\\"+i+".txt");
//            List<String> lines = FileUtils.readLines(data_file);
//            Map<Integer, String> data = new HashMap<>();
//            for (String line : lines) {
//                line = line.trim();
//                if (line.equals("")) {
//                    /*if (!data.isEmpty()) {
//                        List<Integer> indexes = new ArrayList<>(data.keySet());
//                        Collections.sort(indexes);
//                        for (int index : indexes) {
//                            String sentence = data.get(index);
//                            output.write((sentence + "\n").getBytes());
//                        }
//                        output.write("\n".getBytes());
//                        data = new HashMap<>();
//                    }*/
//                    continue;
//                }
//                String tempLine = line.replace(" <<", " ").replace(">> ", " ");
//                //System.out.printf(tempLine);
//                int index = sentence_index.get(tempLine);
//                data.put(index, line);
//            }
//
//            if (!data.isEmpty()) {
//                List<Integer> indexes = new ArrayList<>(data.keySet());
//                Collections.sort(indexes);
//                for (int index : indexes) {
//                    String sentence = data.get(index);
//                    output.write((sentence + "\n").getBytes());
//                }
//                output.write("\n".getBytes());
//                data = new HashMap<>();
//            }
//
//            i++;
//        }
//
//    }

}
