package eu.tib.sre.pdfparser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * https://github.com/IBM/science-result-extractor/blob/master/nlpLeaderboard/src/main/java/com/ibm/sre/pdfparser/CachedTable.java
 * @author yhou
 * @author edited by jld
 */
public class CachedTable {

    public String caption;
    public Set<String> rows;
    public Set<String> columns;
    public Set<String> mergedAllColumns;
    public Set<NumberCell> numberCells;

    public CachedTable(String caption){
        this.caption = caption;
        rows = new HashSet();
        columns = new HashSet();
        mergedAllColumns = new HashSet();
        numberCells = new HashSet();
    }

    public List<NumberCell> getBoldedNumberCells(){
        List<NumberCell> boldedNumbers = new ArrayList();
        for(NumberCell c1: numberCells){
            if(c1.isBolded)
                boldedNumbers.add(c1);
        }
        return boldedNumbers;

    }

    public class NumberCell{
        public String number;
        boolean isBolded;
        public List<String> associatedRows;
        public List<String> associatedColumns;
        public List<String> associatedMergedColumns;

        public NumberCell(String number){
            this.number = number;
            this.isBolded = false;
            associatedRows = new ArrayList();
            associatedColumns = new ArrayList();
            associatedMergedColumns = new ArrayList();
        }

    }

}

