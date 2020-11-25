package eu.tib.sre;

import shadedwipo.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author jld
 */
public class RetainOnlyAbstract {

    public static String getCorefStr(String[] coref_lines, int j) {
        String corefStr = "";
        j++;

        boolean keep = false;
        String corefStrTemp = "";

        while (j < coref_lines.length) {

            if (coref_lines[j].contains("</document>")) {
                break;
            }

            corefStrTemp += coref_lines[j]+"\n";

            if (coref_lines[j].contains("<sentence>1</sentence>")) {
                keep = true;
            }
            else if (keep && coref_lines[j].contains("<sentence>") && coref_lines[j].contains("</sentence>") && !coref_lines[j].contains("<sentence>1</sentence>")) {
                keep = false;
            }

            if (coref_lines[j].contains("</coreference>")) {
                if (keep) {
                    corefStr += corefStrTemp;
                    //corefStr += coref_lines[j] + "\n";
                }
                keep = false;
                corefStrTemp = "";
            }

            j++;

        }

        j--;
        return corefStr;
    }

    /*
      <coreference>
        <mention representative="true">
          <sentence>1</sentence>
          <start>10</start>
          <end>12</end>
          <head>11</head>
          <text>Tree Structures</text>
        </mention>
        <mention>
          <sentence>1</sentence>
          <start>26</start>
          <end>27</end>
          <head>26</head>
          <text>them</text>
        </mention>
      </coreference>
     */
    public static void main(String[] args) throws IOException {
        int i = 0;
        String main_dir = "C:\\Users\\DSouzaJ\\Desktop\\Datasets\\paperswithcode\\nlp";

        while (i <= 35) {

            File coref_file = new File(main_dir+"\\"+i+"\\coref\\").listFiles()[0];

            String[] coref_lines = FileUtils.readFileToString(coref_file).split("\\n");

            String output_str = "";
            int append = 1;

            for (int j = 0; j < coref_lines.length; j++) {
                String coref_line = coref_lines[j];
                if (append == 3) {
                    if (coref_line.contains("<coreference>")) {

                        System.out.println("in here");

                        output_str += "    </sentences>\n";
                        String coref_str = getCorefStr(coref_lines, j);
                        output_str += "    <coreference>\n";
                        if (!coref_str.equals("")) {
                            output_str += coref_str;
                        }
                        output_str += "    </coreference>\n";
                        output_str += "  </document>\n";
                        output_str += "</root>";

                        break;
                    }

                    continue;

                }
                if (append == 2) {
                    if (coref_line.contains("</sentence>")) {
                        output_str += coref_line+"\n";
                        append = 3;
                        continue;
                    }
                }
                if (append == 1 && coref_line.contains("<word>abstract</word>")) {
                    append = 2;
                }
                output_str += coref_line+"\n";
            }

            FileOutputStream output = new FileOutputStream(coref_file);
            output.write(output_str.getBytes());

            File txt_file = new File(main_dir+"\\"+i+"\\txt\\").listFiles()[0];

            String text = FileUtils.readFileToString(txt_file);

            String[] lines = text.split("\\n");

            output_str = "";

            for (int j = 0; j < lines.length; j++) {
                if (lines[j].trim().equals("abstract")) {
                    output_str += lines[j]+"\n";
                    lines[j+1] = lines[j+1].trim();
                    output_str += lines[j+1];
                    break;
                }
                output_str += lines[j].trim()+"\n";
            }

            output = new FileOutputStream(txt_file);
            output.write(output_str.getBytes());

            i++;

        }
    }

}
