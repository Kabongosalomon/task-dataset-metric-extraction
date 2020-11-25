package eu.tib.sre;

import edu.stanford.nlp.ling.CoreLabel;
 import edu.stanford.nlp.pipeline.Annotation;
 import edu.stanford.nlp.pipeline.CoreDocument;
 import edu.stanford.nlp.pipeline.CoreSentence;
 import edu.stanford.nlp.pipeline.StanfordCoreNLP;
// import shadedwipo.org.apache.commons.io.FileUtils;
 import org.apache.commons.io.FileUtils;
//import edu.stanford.nlp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * @author jld
 */
public class CoreNLP {

    static List<String> tasks = new ArrayList<>();
    static List<String> software = new ArrayList<>();
    static List<String> datasets = new ArrayList<>();

    public static Map<String, List<String>> setPrevTokens(List<String> elements) {
        Map<String, List<String>> tokPrevTokens = new HashMap<>();
        for (String element : elements) {
            String[] tokens = element.split("\\s+");

            List<String> prevTokens = tokPrevTokens.get(tokens[tokens.length-1]);
            if (prevTokens == null) {
                tokPrevTokens.put(tokens[tokens.length - 1], prevTokens = new ArrayList<>());
            }
            element = tokens.length != 1 ? element.substring(0, element.lastIndexOf(" ")).trim() : "";

            if (!prevTokens.contains(element)) {
                prevTokens.add(element);
            }


            //String prevToken = "";
            /*for (int i = 0; i < tokens.length; i++) {
                if (i == 0) {
                    List<String> prevTokens = tokPrevTokens.get(tokens[i]);
                    if (prevTokens == null) {
                        tokPrevTokens.put(tokens[i], prevTokens = new ArrayList<>());
                    }
                    prevTokens.add("");
                }
                else {
                    List<String> prevTokens = tokPrevTokens.get(tokens[i]);
                    if (prevTokens == null) {
                        tokPrevTokens.put(tokens[i], prevTokens = new ArrayList<>());
                    }
                    prevTokens.add(prevToken);
                }
                prevToken = tokens[i];
            }*/
        }
        return tokPrevTokens;
    }

    public static Map<String, List<String>> setFollTokens(List<String> elements) {
        Map<String, List<String>> tokFollTokens = new HashMap<>();
        for (String element : elements) {
            String[] tokens = element.split("\\s+");
            List<String> follTokens = tokFollTokens.get(tokens[0]);
            if (follTokens == null) {
                tokFollTokens.put(tokens[0], follTokens = new ArrayList<>());
            }
            if (tokens.length != 1) {
                element = element.substring(element.indexOf(" ")).trim();
            }
            else {
                element = "";
            }

            if (!follTokens.contains(element)) {
                follTokens.add(element);
            }

            /*for (int i = 0; i < tokens.length; i++) {
                if (i == tokens.length-1) {
                    List<String> follTokens = tokFollTokens.get(tokens[i]);
                    if (follTokens == null) {
                        tokFollTokens.put(tokens[i], follTokens = new ArrayList<>());
                    }
                    follTokens.add("");
                    continue;
                }

                follToken = tokens[i+1];
                List<String> follTokens = tokFollTokens.get(tokens[i]);
                if (follTokens == null) {
                    tokFollTokens.put(tokens[i], follTokens = new ArrayList<>());
                }
                follTokens.add(follToken);
            }*/
        }
        return tokFollTokens;
    }

    public static void main(String[] args) throws IOException {

        //tasks = FileUtils.readLines(new File("C:\\Users\\DSouzaJ\\Desktop\\Datasets\\paperswithcode\\tasks-p1.txt"));
        Map<String, List<String>> taskTokPrevTokens = new HashMap<>();//setPrevTokens(tasks);
        System.out.println(taskTokPrevTokens);

        //System.out.println("set prev Task");
        //Map<String, List<String>> taskTokFollowTokens = setFollTokens(tasks);
        //System.out.println(taskTokFollowTokens);
        //System.out.println("set follow Task");
        //software = FileUtils.readLines(new File("C:\\Users\\DSouzaJ\\Desktop\\Datasets\\paperswithcode\\software-p1.txt"));
        Map<String, List<String>> softwareTokPrevTokens = new HashMap<>();//setPrevTokens(software);
        System.out.println(softwareTokPrevTokens);

        //Map<String, List<String>> softwareTokFollowTokens = setFollTokens(software);
        //datasets = FileUtils.readLines(new File("C:\\Users\\DSouzaJ\\Desktop\\Datasets\\paperswithcode\\datasets-p1.txt"));
        Map<String, List<String>> datasetTokPrevTokens = new HashMap<>();//setPrevTokens(datasets);
        System.out.println(datasetTokPrevTokens);

        //Map<String, List<String>> datasetTokFollowTokens = setFollTokens(datasets);

        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, parse, lemma, ner, depparse, coref");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        File texts = new File("C:\\Users\\DSouzaJ\\Desktop\\Datasets\\paperswithcode\\nlp\\all\\txt");
        for (File textfile : texts.listFiles()) {
            String pdftext = FileUtils.readFileToString(textfile);
            //String pdftext = FileUtils.readFileToString(new File("C:\\Users\\DSouzaJ\\Desktop\\Datasets\\paperswithcode\\nlp\\p1\\txt\\D17-1120.txt"));
            CoreDocument document = new CoreDocument(pdftext);
            System.out.println("begin annotating!");
            pipeline.annotate(document);
            System.out.println("done annotating!");

            for (CoreSentence sentence : document.sentences()) {
                int size = sentence.tokens().size();

                for (int i = 0; i < size; i++) {
                    CoreLabel token = sentence.tokens().get(i);
                    token.setNER("O");

                    if (taskTokPrevTokens.containsKey(token.word())) {
                        List<String> precede = taskTokPrevTokens.get(token.word());
                        if (precede.size() == 1 && precede.contains("")) {
                            token.setNER("TASK");
                        } else {
                            List<Integer> prevIndexes = new ArrayList<>();
                            String precedePhrase = "";
                            int j = i;
                            while (j > 0) {
                                j--;
                                precedePhrase = sentence.tokens().get(j).word() + " " + precedePhrase;
                                precedePhrase = precedePhrase.trim();
                                //System.out.println(precedePhrase);
                                if (!precede.contains(precedePhrase)) {
                                    prevIndexes.add(j);
                                    continue;
                                }

                                prevIndexes.add(j);
                                for (int prevIndex : prevIndexes) {
                                    sentence.tokens().get(prevIndex).setNER("TASK");
                                }
                                token.setNER("TASK");
                                break;
                            }
                        }
                    }

                    if (softwareTokPrevTokens.containsKey(token.word())) {
                        List<String> precede = softwareTokPrevTokens.get(token.word());
                        if (precede.size() == 1 && precede.contains("")) {
                            token.setNER("SOFTWARE");
                        } else {
                            List<Integer> prevIndexes = new ArrayList<>();
                            String precedePhrase = "";
                            int j = i;
                            while (j > 0) {
                                j--;
                                precedePhrase = sentence.tokens().get(j).word() + " " + precedePhrase;
                                precedePhrase = precedePhrase.trim();
                                //System.out.println(precedePhrase);
                                if (!precede.contains(precedePhrase)) {
                                    prevIndexes.add(j);
                                    continue;
                                }

                                prevIndexes.add(j);
                                for (int prevIndex : prevIndexes) {
                                    sentence.tokens().get(prevIndex).setNER("SOFTWARE");
                                }
                                token.setNER("SOFTWARE");
                                break;
                            }
                        }
                    }

                    if (datasetTokPrevTokens.containsKey(token.word())) {
                        List<String> precede = datasetTokPrevTokens.get(token.word());
                        if (precede.size() == 1 && precede.contains("")) {
                            token.setNER("DATASET");
                        } else {
                            List<Integer> prevIndexes = new ArrayList<>();
                            String precedePhrase = "";
                            int j = i;
                            while (j > 0) {
                                j--;
                                precedePhrase = sentence.tokens().get(j).word() + " " + precedePhrase;
                                precedePhrase = precedePhrase.trim();
                                //System.out.println(precedePhrase);
                                if (!precede.contains(precedePhrase)) {
                                    prevIndexes.add(j);
                                    continue;
                                }

                                prevIndexes.add(j);
                                for (int prevIndex : prevIndexes) {
                                    sentence.tokens().get(prevIndex).setNER("DATASET");
                                }
                                token.setNER("DATASET");
                                break;
                            }
                        }
                    }
                }
            }

            FileOutputStream os = new FileOutputStream(new File("C:\\Users\\DSouzaJ\\Desktop\\Datasets\\paperswithcode\\nlp\\all\\coref\\"+textfile.getName()+".txt"));
            pipeline.xmlPrint(document.annotation(), os);
        }
    }

}
