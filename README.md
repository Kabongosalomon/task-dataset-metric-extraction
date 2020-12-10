# Task Dataset Metric Software Extraction

This program produces the test data for classification over a set of predefined task#dataset#metrics#software labels.

Given input a pdf file, it scrapes the text from the file using the Grobid parser, subsequently generating the test data file for input to the neural network classifier.

## Steps to run the program
 

1. Clone this repository (https://github.com/jenlindadsouza/task-dataset-metric-extraction)
2. The program jar file can be found at https://github.com/jenlindadsouza/task-dataset-metric-extraction/tree/master/build/libs <br>
&nbsp;&nbsp;&nbsp;&nbsp; - It is called `tdm-1.0.jar`
3. `Usage: java -jar tdm-1.0.jar <pdf-file-path> <resources-dir>`
Where the `pdf-file-path` is the input pdf article to extract task, dataset, metric, software elements from and `resources-dir` should point to the directory at https://github.com/jenlindadsouza/task-dataset-metric-extraction/tree/master/src/main/resources
4. To run directly from the source code, the build file will have to be updated for the local dependencies for jar files. See here: https://github.com/jenlindadsouza/task-dataset-metric-extraction/blob/master/build.gradle
5. Either the jars can be obtained from source. For convenience, they are also made available here https://drive.google.com/drive/folders/1ax7ah8AInoD-_7amx27VKaSrFV73ggEk?usp=sharing


You man need to install marven manually in linux using `sudo apt install maven`


## Run experiments based on textual entailment system

We release the training/testing datasets for all experiments described in the paper. You can find them under the data/exp directory. The results reported in the paper are based on the datasets under the [data/exp/few-shot-setup/NLP-TDMS/paperVersion](data/exp/few-shot-setup/NLP-TDMS/paperVersion) directory. We later further clean the datasets (e.g., remove five pdf files from the testing datasets which appear in the training datasets with a different name) and the clean version is under the [data/exp/few-shot-setup/NLP-TDMS](data/exp/few-shot-setup/NLP-TDMS) folder. Below we illustrate how to run experiments on the NLP-TDSM dataset in the few-shot setup to extract TDM pairs. 


1) Fork and clone this repository.

2) Download or clone [BERT](https://github.com/google-research/bert).

3) Run this command `pip install -r requirements.txt` from `./bert_tdms/` folder. 

4) Copy [run_classifier_sci.py](./bert_tdms/run_classifier_sci.py) into the BERT directory.

5) Download BERT embeddings.  We use the [base uncased models](https://storage.googleapis.com/bert_models/2018_10_18/uncased_L-12_H-768_A-12.zip).

6) If we use `BERT_DIR` to point to the directory with the embeddings and `DATA_DIR` to point to the [directory with our train and test data](./data/exp/few-shot-setup/NLP-TDMS/), we can run the textual entailment system with  [run_classifier_sci.py](./bert_tdms/run_classifier_sci.py). For example:

```
> DATA_DIR=../data/exp/few-shot-setup/NLP-TDMS/
> BERT_DIR=./model/uncased_L-12_H-768_A-12
> python run_classifier_sci.py --do_train=true --do_eval=false --do_predict=true --data_dir=${DATA_DIR} --task_name=sci --vocab_file=${BERT_DIR}/vocab.txt --bert_config_file=${BERT_DIR}/bert_config.json --init_checkpoint=${BERT_DIR}/bert_model.ckpt --output_dir=bert_tdms --max_seq_length=512 --train_batch_size=6 --predict_batch_size=6

> # To run on TPU
> python run_sci_classifier.py --use_tpu=True --tpu=grpc://10.61.49.210:8470 --do_train=True --do_eval=False --do_predict=False --task_name=sci --data_dir=/content/gdrive/My\ Drive/paperswithcodedatawith600unk/twofold/fold2 --output_dir=$OUTPUT_DIR --model_dir=$OUTPUT_DIR --uncased=False --model_config_path=../xlnet_large_cased/xlnet_config.json --spiece_model_file=../xlnet_large_cased/spiece.model --init_checkpoint=$BUCKET_NAME/xlnet_large_cased/xlnet_model.ckpt --max_seq_length=512 --train_batch_size=16 --num_hosts=1 --num_core_per_host=8 --learning_rate=1e-5 --train_steps=50000 --warmup_steps=500 --save_steps=500 --iterations=1000
```

5) [TEModelEvalOnNLPTDMS](nlpLeaderboard/src/main/java/com/ibm/sre/tdmsie/TEModelEvalOnNLPTDMS.java) provides methods to evaluate TDMS tuples extraction.

6) [GenerateTestDataOnPDFPapers](nlpLeaderboard/src/main/java/com/ibm/sre/tdmsie/GenerateTestDataOnPDFPapers.java) provides methods to generate testing dataset for any PDF papers.


## Acknowledgement: 
This program reuses code modules from IBM's science-result-extractor (https://github.com/IBM/science-result-extractor). A reference url to their paper on the ACL anthology is https://www.aclweb.org/anthology/P19-1513
