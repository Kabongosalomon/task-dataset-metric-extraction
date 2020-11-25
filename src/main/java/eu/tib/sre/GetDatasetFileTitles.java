package eu.tib.sre;

import org.apache.commons.io.FileUtils;
//import commons-io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author jld
 */
public class GetDatasetFileTitles {

    public static void main(String[] args) throws IOException {
        int i = 0;
        String main_dir = "C:\\Users\\DSouzaJ\\Desktop\\Datasets\\paperswithcode\\nlp";

        FileOutputStream output = new FileOutputStream("C:\\Users\\DSouzaJ\\Desktop\\Datasets\\paperswithcode\\nlp\\scan-files.txt");

        while (i <= 20) {

            File txt_file = new File(main_dir+"\\"+i+"\\txt\\").listFiles()[0];

            String text = FileUtils.readFileToString(txt_file);

            String[] lines = text.split("\\n");

            for (int j = 0; j < lines.length; j++) {

                if (lines[j].contains("title")) {
                    output.write((i+"\t"+lines[j+1]+"\n").getBytes());
                    break;
                }

            }

            i++;

        }

    }

}
