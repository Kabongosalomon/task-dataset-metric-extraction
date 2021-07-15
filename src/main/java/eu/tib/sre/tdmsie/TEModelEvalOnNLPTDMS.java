/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.tib.sre.tdmsie;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.LoggerFactory;

import eu.tib.sre.NLPResult;
import eu.tib.sre.evaluation.MultiLabelEvaluationMetrics;

/**
 *
 * The code illustrates per_label and per_sample eu.tib.sre.evaluation for the few_shot_setup experiment on NLP-TDMS
 * @author yhou
*/
public class TEModelEvalOnNLPTDMS {

    private Properties prop;
    static org.slf4j.Logger logger = LoggerFactory.getLogger(TEModelEvalOnNLPTDMS.class);

    public TEModelEvalOnNLPTDMS() throws IOException, Exception {
        prop = new Properties();
        prop.load(new FileReader("config.properties"));
    }



    public Map<String, String> getPredictedSore() throws IOException, Exception {
        Map<String, String> scorePrediction = new HashMap();
        // String file1 = prop.getProperty("projectPath") + "/" + "data/ibm/exp/few-shot-setup/NLP-TDMS/paperVersion/test_score.tsv";
        // String file2 = prop.getProperty("projectPath") + "/" + "data/ibm/exp/few-shot-setup/NLP-TDMS/paperVersion/test_score_results.tsv";
        
        String file1 = prop.getProperty("projectPath") + "/" + prop.getProperty("test_score_path");
        String file2 = prop.getProperty("projectPath") + "/" + prop.getProperty("test_score_results_path");

        BufferedReader br1 = new BufferedReader(new FileReader(file1));
        BufferedReader br2 = new BufferedReader(new FileReader(file2));
        List<String> f1 = new ArrayList();
        List<String> f2 = new ArrayList();
        String line = "";
        while ((line = br1.readLine()) != null) {
            f1.add(line);
        }
        while ((line = br2.readLine()) != null) {
            f2.add(line);
        }
        for (int i = 0; i < f1.size(); i++) {
            String filename = f1.get(i).split("\t")[1].split("#")[0];
            String board = f1.get(i).split("\t")[2];

            // String dataset = board.split(",")[0].trim();
            // String eval = board.split(",")[1].trim();

            // The file in my case a splited by ;
            String dataset = board.split(";")[0].trim();
            String eval = board.split(";")[1].trim();

            String scoreStr = f1.get(i).split("\t")[1].split("#")[1];
            if (Double.valueOf(f2.get(i).split("\t")[0]) > 0.0) {
                if (scorePrediction.containsKey(filename + "#" + dataset + ":::" + eval)) {
                    String oldScoreStr = scorePrediction.get(filename + "#" + dataset + ":::" + eval).split("#")[0];
                    String oldConfidenceStr = scorePrediction.get(filename + "#" + dataset + ":::" + eval).split("#")[1];
                    Double newConfidenceScore = Double.valueOf(f2.get(i).split("\t")[0]);
                    Double oldConfidenceScore = Double.valueOf(oldConfidenceStr);
                    if (newConfidenceScore > oldConfidenceScore) {
                        scorePrediction.put(filename + "#" + dataset + ":::" + eval, scoreStr + "#" + f2.get(i).split("\t")[0]);
                    }
                } else {
                    scorePrediction.put(filename + "#" + dataset + ":::" + eval, scoreStr + "#" + f2.get(i).split("\t")[0]);
                }

            }
        }
        return scorePrediction;
    }
    

    public void evaluateTDMSExtraction() throws IOException, Exception {
    // public void evaluateTDMSExtraction(FileOutputStream fold_stats) throws IOException, Exception {
        // Map<String, String> scorePrediction = getPredictedSore();
        // String file1 = prop.getProperty("projectPath") + "/" + "data/ibm/exp/few-shot-setup/NLP-TDMS/paperVersion/test.tsv";
        // String file2 = prop.getProperty("projectPath") + "/" + "data/ibm/exp/few-shot-setup/NLP-TDMS/paperVersion/test_results.tsv";

        String file1 = prop.getProperty("projectPath") + "/" + prop.getProperty("test_path");
        String file2 = prop.getProperty("projectPath") + "/" +  prop.getProperty("test_results_path");

        
        BufferedReader br1 = new BufferedReader(new FileReader(file1));
        BufferedReader br2 = new BufferedReader(new FileReader(file2));
        
        Set<String> excludeTestFiles = getTestFilesInTrainsetWithDifferentName();


        MultiLabelEvaluationMetrics evalMatrix = new MultiLabelEvaluationMetrics();
        Map<String, Set<NLPResult>> resultsPredictionsTestPapers = new HashMap();
        List<String> f1 = new ArrayList();
        List<String> f2 = new ArrayList();
        String line = "";
        while ((line = br1.readLine()) != null) {
            f1.add(line);
        }
        while ((line = br2.readLine()) != null) {
            f2.add(line);
        }

        
        for (int i = 0; i < f1.size(); i++) {
            String filename = f1.get(i).split("\t")[1];
            if(excludeTestFiles.contains(filename)) continue;

            //if(excludeTestFiles.contains(filename)) continue;

            String leaderboard = f1.get(i).split("\t")[2];
            if (!resultsPredictionsTestPapers.containsKey(filename)) {

                Set<NLPResult> results = new HashSet();
                resultsPredictionsTestPapers.put(filename, results);

            }
            if (Double.valueOf(f2.get(i).split("\t")[0]) > 0.5) {
                // if (Double.valueOf(f2.get(i).split("\t")[0]) > Double.valueOf(f2.get(i).split("\t")[1])) {            
                if (leaderboard.equalsIgnoreCase("unknown")) {
                NLPResult result = new NLPResult(filename, "unknown", "unknown");
                result.setEvaluationMetric("unknown");
                result.setEvaluationScore("unknown");
                resultsPredictionsTestPapers.get(filename).add(result);

                } 
            else {
                // String task = leaderboard.split(",")[0].replace(" ", "_").trim();
                // String dataset = leaderboard.split(",")[1].trim();
                // String eval = leaderboard.split(",")[2].trim();

                String task = leaderboard.split(";")[0].replace(" ", "_").trim();
                // String task = leaderboard.split(";")[0].trim();
                String dataset = leaderboard.split(";")[1].trim();
                String eval = leaderboard.split(";")[2].trim();

                NLPResult result = new NLPResult(filename, task, dataset);
                result.setEvaluationMetric(eval);
                
                // // TODO: Evaluation, Task, Dataset, Metric ...
                // NLPResult result = new NLPResult(filename, task, "None");
                // // NLPResult result = new NLPResult(filename, "None", dataset);
                // result.setEvaluationMetric("None");
                // NLPResult result = new NLPResult(filename, "None", "None");
                // result.setEvaluationMetric(eval);
                
                // This is for score 
                // if (scorePrediction.containsKey(filename + "#" + dataset + ":::" + eval)) {
                //     result.setEvaluationScore(scorePrediction.get(filename + "#" + dataset + ":::" + eval).split("#")[0]);
                // }

                resultsPredictionsTestPapers.get(filename).add(result);
                }
            }
        }
        //collect eu.tib.sre.evaluation labels seen in the train.tsv
        Set<String> evaluatedLabels = new HashSet();

        // String file3 = prop.getProperty("projectPath") + "/" + "data/ibm/exp/few-shot-setup/NLP-TDMS/paperVersion/train.tsv";
        String file3 = prop.getProperty("projectPath") + "/" + prop.getProperty("train_path");

        BufferedReader br3 = new BufferedReader(new FileReader(file3));

        String line3 = "";
        while ((line3 = br3.readLine()) != null) {
            String leaderboard = line3.split("\t")[2];
            if (leaderboard.equalsIgnoreCase("unknown")) {
                continue;
            } else {
                // TODO I may need to change this 

                // String task = leaderboard.split(",")[0].replace(" ", "_");
                // String dataset = leaderboard.split(",")[1];
                // String eval = leaderboard.split(",")[2];
                if ( leaderboard.split(";").length !=3){
                    continue;
                }
                String task = leaderboard.split(";")[0].replace(" ", "_");
                // String task = leaderboard.split(";")[0];
                String dataset = leaderboard.split(";")[1];
                String eval = leaderboard.split(";")[2];

                evaluatedLabels.add(task.trim() + ":::" + dataset.trim() + ":::" + eval.trim());
            }
        }

        // logger.info("leaderboard eu.tib.sre.evaluation:");
        // // fold_stats.write(("leaderboard eu.tib.sre.evaluation:").getBytes());

        // logger.info("per_label:");
        // // fold_stats.write(("per_label:").getBytes());

        // logger.info(evalMatrix.perLabelEvaluation_Leaderboard_TaskDatasetEvaluationMatrix(resultsPredictionsTestPapers, false, evaluatedLabels));
        // // fold_stats.write((evalMatrix.perLabelEvaluation_Leaderboard_TaskDatasetEvaluationMatrix(resultsPredictionsTestPapers, false, evaluatedLabels)).getBytes());

        // logger.info("per_sample:");
        // // fold_stats.write(("per_sample:").getBytes());

        // logger.info(evalMatrix.perSampleEvaluation_Leaderboard(resultsPredictionsTestPapers, file1));
        // // fold_stats.write((evalMatrix.perSampleEvaluation_Leaderboard(resultsPredictionsTestPapers, file1)).getBytes());

        System.out.println("leaderboard eu.tib.sre.evaluation:");
        // System.out.println("per_label:");
        // System.out.println(evalMatrix.perLabelEvaluation_Leaderboard_TaskDatasetEvaluationMatrix(resultsPredictionsTestPapers, false, evaluatedLabels));
        System.out.println("per_sample:");
        System.out.println(evalMatrix.perSampleEvaluation_Leaderboard(resultsPredictionsTestPapers, file1));

    
    }
    

        public Set<String> getTestFilesInTrainsetWithDifferentName() throws IOException, Exception {
        Map<String, Set<String>> trainTitle = new HashMap();
        Map<String, Set<String>> testTitle = new HashMap();
        Set<String> excludeFiles = new HashSet();
        // String file10 = prop.getProperty("projectPath") + "/" + "data/ibm/exp/few-shot-setup/NLP-TDMS/paperVersion/train.tsv";
        // String file20 = prop.getProperty("projectPath") + "/" + "data/ibm/exp/few-shot-setup/NLP-TDMS/paperVersion/test.tsv";
        String file10 = prop.getProperty("projectPath") + "/" + prop.getProperty("train_path");
        String file20 = prop.getProperty("projectPath") + "/" + prop.getProperty("test_path");
        String line0 = "";
        BufferedReader br10 = new BufferedReader(new FileReader(file10));
        BufferedReader br20 = new BufferedReader(new FileReader(file20));
        while ((line0 = br10.readLine()) != null) {
            String filename = line0.split("\t")[1];
            String title;
            if (line0.split("\t")[3].length() <50){
                title = line0.split("\t")[3];
                // continue;
            }else{
                title = line0.split("\t")[3].substring(0, 50);
            }
            if (trainTitle.containsKey(title)) {
                trainTitle.get(title).add(filename);
            } else {
                Set<String> files = new HashSet();
                files.add(filename);
                trainTitle.put(title, files);
            }
        }
        while ((line0 = br20.readLine()) != null) {
            String filename = line0.split("\t")[1];
            String title;
            if (line0.split("\t")[3].length() <50){
                title = line0.split("\t")[3];
                // continue;
            }else{
                title = line0.split("\t")[3].substring(0, 50);
            }

            // String title = line0.split("\t")[3].substring(0, 50);
            if (testTitle.containsKey(title)) {
                testTitle.get(title).add(filename);
            } else {
                Set<String> files = new HashSet();
                files.add(filename);
                testTitle.put(title, files);
            }
        }

        for (String title : testTitle.keySet()) {
            if (trainTitle.keySet().contains(title)) {
                excludeFiles.addAll(testTitle.get(title));
            }
        }
        return excludeFiles;
    }
    
   public static void main(String[] args) throws IOException, Exception{
       TEModelEvalOnNLPTDMS teEval = new TEModelEvalOnNLPTDMS();
       teEval.evaluateTDMSExtraction();
   }

}
