package eu.tib.sre;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * @author jld
 */
public class CreateFolderPerFile {

//    public static void main(String[] args) throws IOException {
//
//        String nlp_dir = "C:\\Users\\DSouzaJ\\Desktop\\Datasets\\paperswithcode\\nlp";
//        String main_dir = "C:\\Users\\DSouzaJ\\Desktop\\Datasets\\paperswithcode\\nlp\\topic-papers";
//        File coref_dir = new File(main_dir+"\\coref");
//        String txt_dir = main_dir+"\\txt";
//        int i = 0;
//        for (File coref_file : coref_dir.listFiles()) {
//
//            File new_dir = new File(nlp_dir+"\\"+i);
//            if (!new_dir.exists()) new_dir.mkdirs();
//            if (!new File(new_dir.toString()+"\\coref").exists()) new File(new_dir.toString()+"\\coref").mkdirs();
//            if (!new File(new_dir.toString()+"\\txt").exists()) new File(new_dir.toString()+"\\txt").mkdirs();
//            i++;
//
//            String txt_filename = coref_file.getName().replace(".xml", "");
//
//            Files.copy(coref_file.toPath(), new File(new_dir.toString()+"\\coref\\"+coref_file.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
//            Files.copy(new File(txt_dir+"\\"+txt_filename).toPath(), new File(new_dir.toString()+"\\txt\\"+txt_filename).toPath(), StandardCopyOption.REPLACE_EXISTING);
//        }
//
//    }

}
