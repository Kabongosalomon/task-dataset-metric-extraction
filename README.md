# Task Dataset Metric Software Extraction

This program produces the test data for classification over a set of predefined task#dataset#metrics#software labels.

Given input a pdf file, it scrapes the text from the file using the Grobid parser, subsequently generating the test data file for input to the neural network classifier.

## Steps to run the program
 
The following procedure, suppose that you are using a linux OS, if otherwise kindly just clone the repo and rebuild the project. 

1. Clone this repository (https://github.com/Kabongosalomon/task-dataset-metric-extraction/tree/trainTest) or clone a particular branch `git clone https://github.com/Kabongosalomon/task-dataset-metric-extraction.git`.
2. move to the cloned directory `cd task-dataset-metric-extraction`
3. run the command `bash starter.sh`
4. Depending on wheter you are doing Training or Testing run (you may need to run test atleast once for a specific configuration): 
    - Train 
        - `java -jar build/libs/task-dataset-metric-extraction-1.0.jar 'train' '5' '10' '5' "/home/salomon/Desktop/task-dataset-metric-extraction/data/pdf/" "/home/salomon/Desktop/"`
    - Test
        - `java -jar build/libs/task-dataset-metric-extraction-1.0.jar 'test' '5' '10' '5' "/home/salomon/Desktop/task-dataset-metric-extraction/data/50.pdf" "/home/salomon/Desktop/"`
<!-- 6. The pre-build jar file can be found here https://drive.google.com/file/d/1xxXlJGz6EElOZAnLgj-lHAiYssvecGfe/view?usp=sharing -->


## Acknowledgement: 
This program reuses code modules from IBM's science-result-extractor (https://github.com/IBM/science-result-extractor). A reference url to their paper on the ACL anthology is https://www.aclweb.org/anthology/P19-1513
