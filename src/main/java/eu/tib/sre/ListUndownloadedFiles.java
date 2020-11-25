package eu.tib.sre;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jld
 */
public class ListUndownloadedFiles {

    public static void main(String[] args) throws IOException {

        String filename = "C:\\Users\\DSouzaJ\\Desktop\\Datasets\\aiprogress\\pdfs\\scripts\\paper_links.tsv";
        File pdf_dir = new File("C:\\Users\\DSouzaJ\\Desktop\\Datasets\\aiprogress\\pdfs");
        List<String> pdfs = new ArrayList<>();
        for (File file : pdf_dir.listFiles()) {
            if (file.isDirectory()) continue;

            String name = file.getName();
            if (!pdfs.contains(name)) pdfs.add(name);
        }

        FileOutputStream output = new FileOutputStream("C:\\Users\\DSouzaJ\\Desktop\\Datasets\\aiprogress\\pdfs\\scripts\\unfound.tsv");
        List<String> lines = FileUtils.readLines(new File(filename));
        for (String line : lines) {
            line = line.trim();
            String name = line.split("\t")[0].trim();
            if (!pdfs.contains(name)) output.write((line+"\n").getBytes());
        }
    }

}
