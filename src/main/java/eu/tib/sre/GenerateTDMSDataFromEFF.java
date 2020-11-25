package eu.tib.sre;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * @author jld
 */
public class GenerateTDMSDataFromEFF {

    static Map<String, String> url_papername = new HashMap<>();

    static Map<String, List<String>> papername_tdms = new HashMap<>();

    public static void main(String[] args) throws IOException, ParseException {
        File pdf_dir = new File("C:\\Users\\DSouzaJ\\Desktop\\Datasets\\aiprogress\\pdfs");
        List<String> pdfs = new ArrayList<>();
        for (File file : pdf_dir.listFiles()) {
            if (file.isDirectory()) continue;
            String name = file.getName();
            if (!pdfs.contains(name)) pdfs.add(name);
        }

        String filename = "C:\\Users\\DSouzaJ\\Desktop\\Datasets\\aiprogress\\progress.json";

        Object obj = new JSONParser().parse(new FileReader(filename));

        JSONObject jo = (JSONObject) obj;

        JSONArray ja = (JSONArray)jo.get("problems");
        Iterator itr = ja.iterator();

        while (itr.hasNext())
        {
            Map problem = (Map) itr.next();

            String problemname = (String) problem.get("name");

            JSONArray metrics_array = (JSONArray)problem.get("metrics");
            if (metrics_array.size() == 0) continue;

            Iterator met_itr = metrics_array.iterator();

            while (met_itr.hasNext()) {

                Map metrics = (Map) met_itr.next();

                String datasetname = (String) metrics.get("name");

                String metric = (String)metrics.get("scale");

                JSONArray measures_array = (JSONArray)metrics.get("measures");
                if (measures_array.size() == 0) continue;
                Iterator measures_itr = measures_array.iterator();

                while (measures_itr.hasNext()) {

                    Map measures = (Map) measures_itr.next();

                    String papername = (String)measures.get("papername");
                    if (measures.containsKey("src_name") && !(measures.get("src_name") == null || measures.get("src_name").toString().trim().equals(""))) {
                        papername = (String) measures.get("src_name");
                    }
                    if (papername == null) continue;

                    //papername.replaceAll("\\s+", "-")+".pdf"

                    String namesimple = papername.replaceAll("\\s+", "-")+".pdf";
                    namesimple = namesimple.replaceAll(":", "").replaceAll("\\,", "")
                            .replaceAll("/", "").replaceAll("\\|", "").replaceAll("\\'", "")
                            .replaceAll("\\?", "").replaceAll("\\+", "")
                            .replaceAll("\\’s", "s");

                    if (!pdfs.contains(namesimple)) {
                        //System.out.println(papername+"\t"+namesimple);
                        continue;
                    }

                    String softwarename = (String)measures.get("name");

                    String url = (String)measures.get("url");
                    if (measures.containsKey("algorithm_src_url") && !(measures.get("algorithm_src_url") == null || measures.get("algorithm_src_url").toString().trim().equals(""))) {
                        url = (String)measures.get("algorithm_src_url");
                    }
                    url = url.contains("https://arxiv.org/abs") ? url.replace("https://arxiv.org/abs", "http://arxiv.org/pdf") : url;

                    if (url.equals("")) {
                        System.out.println(papername);
                        continue;
                    }
                    url_papername.put(url, papername);

                    List<String> tdms = papername_tdms.get(namesimple);
                    if (tdms == null) papername_tdms.put(namesimple, tdms = new ArrayList<>());
                    if (!tdms.contains(problemname+"#"+datasetname+"#"+metric+"#"+softwarename)) {
                        tdms.add(problemname + "#" + datasetname + "#" + metric + "#" + softwarename);
                    }
                }
            }
        }

        /*FileOutputStream paper_links = new FileOutputStream("C:\\Users\\DSouzaJ\\Desktop\\Datasets\\aiprogress\\pdfs\\scripts\\paper_links.tsv");
        for (String url : url_papername.keySet()) {
            String name = url_papername.get(url).replaceAll("\\s+", "-")+".pdf";
            name = name.replaceAll(":", "").replaceAll("\\,", "")
                    .replaceAll("/", "").replaceAll("\\|", "").replaceAll("\\'", "")
                    .replaceAll("\\?", "").replaceAll("\\+", "")
                    .replaceAll("\\’s", "s");
            paper_links.write((name+"\t"+url+"\n").getBytes());
        }*/

        FileOutputStream output = new FileOutputStream("C:\\Users\\DSouzaJ\\Desktop\\Datasets\\aiprogress\\tdmGoldTrainingData.tsv");
        for (String papername : papername_tdms.keySet()) {
            List<String> tdmss = papername_tdms.get(papername);
            String str = "";
            for (String tdms : tdmss) {
                str += tdms.trim()+"\t";
            }
            str = str.trim();
            output.write((papername+"\t"+str+"\n").getBytes());
        }

    }

}
