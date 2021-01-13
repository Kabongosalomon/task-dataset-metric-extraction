// package com.ibm.sre.tdmsie;

// import com.ibm.sre.NLPResult;
// import com.ibm.sre.evaluation.MultiLabelEvaluationMetrics;
// import org.slf4j.LoggerFactory;

// import java.io.BufferedReader;
// import java.io.FileReader;
// import java.io.IOException;
// import java.util.*;

// /**
//  * @author jld
//  */
// public class TEModelEvalOnNLPTDMSEdit {

//     private Properties prop;
//     static org.slf4j.Logger logger = LoggerFactory.getLogger(TEModelEvalOnNLPTDMS.class);

//     public TEModelEvalOnNLPTDMSEdit() throws Exception {
//         prop = new Properties();
//         prop.load(new FileReader("config.properties"));
//     }

//     public void evaluateTDMSExtraction() throws IOException, Exception {
//         Map<String, String> scorePrediction = new HashMap<>();//getPredictedSore();
//         String file1 = "C:\\Users\\DSouzaJ\\Desktop\\Datasets\\paperswithcode\\twofoldwithunk\\600\\fold2\\test.tsv";
//         String file2 = "C:\\Users\\DSouzaJ\\Desktop\\Datasets\\paperswithcode\\twofoldwithunk\\600\\fold2\\test_results.tsv";

//         //String file1 = "C:\\Users\\DSouzaJ\\Desktop\\Datasets\\paperswithcode\\onefoldzeroshot\\test.tsv";
//         //String file2 = "C:\\Users\\DSouzaJ\\Desktop\\Datasets\\paperswithcode\\onefoldzeroshot\\test_results.tsv";

//         BufferedReader br1 = new BufferedReader(new FileReader(file1));
//         BufferedReader br2 = new BufferedReader(new FileReader(file2));

//         //Set<String> excludeTestFiles = getTestFilesInTrainsetWithDifferentName();

//         MultiLabelEvaluationMetrics evalMatrix = new MultiLabelEvaluationMetrics();
//         Map<String, Set<NLPResult>> resultsPredictionsTestPapers = new HashMap();
//         List<String> f1 = new ArrayList();
//         List<String> f2 = new ArrayList();
//         String line = "";
//         while ((line = br1.readLine()) != null) {
//             f1.add(line);
//         }
//         while ((line = br2.readLine()) != null) {
//             f2.add(line);
//         }
//         //
//         for (int i = 0; i < f1.size(); i++) {
//             String filename = f1.get(i).split("\t")[1];
//             //if(excludeTestFiles.contains(filename)) continue;
//             String leaderboard = f1.get(i).split("\t")[2];
//             if (!resultsPredictionsTestPapers.containsKey(filename)) {
//                 Set<NLPResult> results = new HashSet();
//                 resultsPredictionsTestPapers.put(filename, results);
//             }
//             if (Double.valueOf(f2.get(i).split("\t")[0]) > 0.5) {
//             //if (Double.valueOf(f2.get(i).split("\t")[0]) > Double.valueOf(f2.get(i).split("\t")[1])) {
//                 if (leaderboard.equalsIgnoreCase("unknown")) {
//                     System.out.println("here");
//                     NLPResult result = new NLPResult(filename, "unknown", "unknown");
//                     result.setEvaluationMetric("unknown");
//                     result.setEvaluationScore("unknown");
//                     resultsPredictionsTestPapers.get(filename).add(result);
//                 } else {
//                     String task = leaderboard.split("#")[0].replace(" ", "_").trim();
//                     String dataset = leaderboard.split("#")[1].trim();
//                     String eval = leaderboard.split("#")[2].trim();
//                     NLPResult result = new NLPResult(filename, task, dataset);
//                     result.setEvaluationMetric(eval);
//                     if (scorePrediction.containsKey(filename + "#" + dataset + ":::" + eval)) {
//                         result.setEvaluationScore(scorePrediction.get(filename + "#" + dataset + ":::" + eval).split("#")[0]);
//                     }
//                     resultsPredictionsTestPapers.get(filename).add(result);
//                 }
//             }
//         }
//         //collect evaluation labels seen in the train.tsv
//         Set<String> evaluatedLabels = new HashSet();
//         String file3 = "C:\\Users\\DSouzaJ\\Desktop\\Datasets\\paperswithcode\\tdmGoldLabels.tsv";
//         BufferedReader br3 = new BufferedReader(new FileReader(file3));
//         String line3 = "";
//         while ((line3 = br3.readLine()) != null) {
//             String leaderboard = line3.trim();
//             if (leaderboard.equalsIgnoreCase("unknow")) {
//                 continue;
//             } else {
//                 String task = leaderboard.split("#")[0].replace(" ", "_");
//                 String dataset = leaderboard.split("#")[1];
//                 String eval = leaderboard.split("#")[2];
//                 evaluatedLabels.add(task.trim() + ":::" + dataset.trim() + ":::" + eval.trim());
//             }
//         }
//         logger.info("leaderboard evaluation:");
//         logger.info("per_label:");
//         logger.info(evalMatrix.perLabelEvaluation_Leaderboard_TaskDatasetEvaluationMatrix(resultsPredictionsTestPapers, false, evaluatedLabels));
//         logger.info("per_sample:");
//         logger.info(evalMatrix.perSampleEvaluation_Leaderboard(resultsPredictionsTestPapers, file1));
//     }

//     public static void main(String[] args) throws IOException, Exception{
//         TEModelEvalOnNLPTDMSEdit teEval = new TEModelEvalOnNLPTDMSEdit();
//         teEval.evaluateTDMSExtraction();
//     }

// }
