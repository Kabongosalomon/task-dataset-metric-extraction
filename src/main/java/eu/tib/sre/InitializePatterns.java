package eu.tib.sre;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

import edu.stanford.nlp.simple.*;

import static edu.stanford.nlp.util.XMLUtils.readDocumentFromFile;

/**
 * @author jld
 */
public class InitializePatterns {

//    public static void main (String[] args) throws Exception {
//        String dir_str = "C:\\Users\\DSouzaJ\\Desktop\\Datasets\\paperswithcode\\nlp";
//        int i = 0;
//        while (i <= 35) {
//            File main_dir = new File(dir_str+"\\"+i);
//            Map<String, Integer> sentence_index = new HashMap<>();
//            Map<Integer, Set<String>> index_predicates = new HashMap<>();
//            Set<String> predicates = new HashSet<>();
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
//                else {
//                    if (!dir.getName().contains(".pkl.txt")) continue;
//                    List<String> lines = FileUtils.readLines(dir);
//                    int index = 0;
//
//                    for (int j = 0; j < lines.size(); j=j+3) {
//                        String line = lines.get(j).trim();
//                        line = line.replaceAll("\"", "");
//                        line = line.replaceAll("u\'", "");
//                        line = line.replaceAll("\'", "");
//                        line = line.replaceAll("\\(", "");
//                        line = line.replaceAll("\\)", "");
//                        line = line.replaceAll("\\[.*?\\]", "");
//                        line = line.replaceAll("\\,", "");
//
//                        String[] tokens = line.split("\\s+");
//                        //System.out.println(line);
//                        //if (line.equals("parent")) {
//
//                        //}
//
//                        Set<String> tempSet = new HashSet<>();
//                        for (String token : tokens) {
//                            token = token.trim();
//                            if (token.equals("these") || token.equals("of") || token.equals("")) continue;
//
//                            tempSet.add(token);
//                            predicates.add(token);
//                        }
//
//                        if (index_predicates.isEmpty()) {
//                            index_predicates.put(index, tempSet);
//                            index++;
//                        }
//                        else {
//                            boolean add = true;
//                            for (int ind : index_predicates.keySet()) {
//                                Set<String> added_predicates = new HashSet<>(index_predicates.get(ind));
//                                added_predicates.removeAll(tempSet);
//                                if ((index_predicates.get(ind).size() == tempSet.size()) && added_predicates.isEmpty()) {
//                                    add = false;
//                                    break;
//                                }
//                            }
//                            if (add) {
//                                index_predicates.put(index, tempSet);
//                                index++;
//                            }
//                        }
//
//                    }
//                }
//            }
//
//            FileOutputStream output = new FileOutputStream(dir_str+"\\"+i+"\\"+i+"-predicates.txt");
//            for (String pred : predicates) {
//                output.write((pred+"\n").getBytes());
//            }
//
//            /*
//            //Map each predicate to the sentence
//            Map<String, List<String>> predicate_sentences = new HashMap<>();
//            for(String predicate : predicates) {
//                List<String> sentences = new ArrayList<>();
//                for (String sentence : sentence_index.keySet()) {
//                    if (sentence.contains(" "+predicate+" ")) {
//                        String addedStr = sentence.replace(" "+predicate+" ", " <<"+predicate+">> ");
//                        sentences.add(addedStr);
//                    }
//                }
//                predicate_sentences.put(predicate, sentences);
//            }
//
//            FileOutputStream output = new FileOutputStream(dir_str+"\\"+i+"\\"+i+".txt");
//            for (int index : index_predicates.keySet()) {
//                Object[] predicatesArr = index_predicates.get(index).toArray();
//                if (predicatesArr.length <= 2) continue;
//
//                List<String> patterns = new ArrayList<>();
//                System.out.println("before: "+patterns.size());
//                for (int j = 0; j < predicatesArr.length; j++) {
//                    if (j == 0) {
//                        String pred1 = (String)predicatesArr[j];
//                        List<String> sentences1 = predicate_sentences.get(pred1);
//                        String pred2 = (String)predicatesArr[j+1];
//                        List<String> sentences2 = predicate_sentences.get(pred2);
//
//                        if (sentences1.isEmpty() && !sentences2.isEmpty()) {
//                            for (String s : sentences2) {
//                                patterns.add(s);
//                            }
//                        }
//                        else if (!sentences1.isEmpty() && sentences2.isEmpty()) {
//                            for (String s : sentences1) {
//                                patterns.add(s);
//                            }
//                        }
//                        else {
//                            for (String s1 : sentences1) {
//                                for (String s2 : sentences2) {
//                                    patterns.add(s1 + "\n" + s2);
//                                }
//                            }
//                        }
//
//                        j++;
//                    }
//                    else {
//                        String pred = (String)predicatesArr[j];
//                        List<String> sentences = predicate_sentences.get(pred);
//                        System.out.println(pred);
//                        System.out.println(sentences);
//
//                        if (sentences.isEmpty() || sentences.size() == 0 || sentences == null) continue;
//
//                        List<String> temp = new ArrayList<>();
//                        for (String pattern : patterns) {
//                            for (String sentence : sentences) {
//                                System.out.println(pattern);
//                                System.out.println(" ");
//                                System.out.println(" ");
//                                temp.add(pattern+"\n"+sentence);
//                            }
//                        }
//                        patterns = new ArrayList<>(temp);
//                        temp.clear();
//                    }
//                }
//
//                //if (patterns.size() == 0) {
//                    //continue;
//                    //System.out.println(index_predicates);
//                    //System.exit(-1);
//                //}
//                System.out.println("after: "+patterns.size());
//                for (String pattern : patterns) {
//                    output.write((pattern+"\n\n").getBytes());
//                }
//
//            }
//
//             */
//            i++;
//        }
//    }

}
