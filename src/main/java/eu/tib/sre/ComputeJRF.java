package eu.tib.sre;

import org.python.core.*;
import org.python.modules.cPickle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jld
 */
public class ComputeJRF {

    final static double CONST = 4.0/3.0;
    static int total_pairs = 0;
    static double jrf = 0;

    public static void unpickle(String filename) {
        File f = new File(filename);
        InputStream fs = null;
        try {
            fs = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            System.out.println("Pickle file '" + filename + "' not found!");
            //return null;
        }

        PyFile picklefile = new PyFile(fs);
        PyDictionary phash = null;
        try {
            phash = (PyDictionary) cPickle.load(picklefile);

            for (Object key : phash.keys()) {
                PyList pyList = (PyList)phash.get(key);
                int total = pyList.size();
                for (int i = 0; i < total; i++) {
                    PyTuple pyTuple1 = (PyTuple)pyList.get(i);
                    PyList pyList1 = (PyList)pyTuple1.get(0);
                    for (int j = i+1; j < total; j++) {
                        PyTuple pyTuple2 = (PyTuple)pyList.get(j);
                        PyList pyList2 = (PyList)pyTuple2.get(0);



                        PyList list = intersection(pyList1, pyList2);
                        double x = list.size();
                        if (x == 0) {
                            //jrf += CONST;
                            //total_pairs++;
                        }
                        else {
                            double frac = x/(4.0*6.0 - 3.0*x);
                            jrf += 4.0/((1/frac) + 3.0);
                            total_pairs++;
                        }
                    }
                }
            }

            System.out.println("Total pairs: "+total_pairs);
            System.out.println("JRF Avg: "+(jrf/total_pairs));

        } catch (PyException e3) {
            System.out.println("Cannot unpickle! (Python error)");
            e3.printStackTrace(); // throws a null pointer exception
            //return null;
        } catch (Exception e) {
            System.out.println("Cannot unpickle! Err: " + e.getClass().getName());
            e.printStackTrace();
            //return null;
        }
    }

    private static PyList intersection(PyList list1, PyList list2) {
        PyList list = new PyList();
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < list1.size(); i++) {
            PyTuple el1 = (PyTuple)list1.get(i);
            for (int j = 0; j < list2.size(); j++) {
                PyTuple el2 = (PyTuple)list2.get(j);
                if (el1.get(0).equals(el2.get(0))) {
                    PyList elList1 = (PyList)el1.get(1);
                    PyList elList2 = (PyList)el2.get(1);
                    if (elList1.size() != elList2.size()) continue;
                    int size = elList1.size();
                    boolean eq = true;
                    for (int k = 0; k < size; k++) {
                        if (!elList1.get(k).equals(elList2.get(k))) {
                            eq = false;
                            break;
                        }
                    }
                    if (eq) {
                        if (!indexes.contains(j)) {
                            indexes.add(j);
                            list.add(el2);
                            break;
                        }
                    }
                }
            }
        }
        return list;
    }

    public static void main(String[] args) {
        unpickle("C:\\Users\\DSouzaJ\\Desktop\\Datasets\\paperswithcode\\cv\\fold2\\schemas-CT-C3-PMI-L1.00-B0.30-s6.00-S800.00.pkl-.pkl");
    }

}
