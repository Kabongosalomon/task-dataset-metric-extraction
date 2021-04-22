#!/bin/bash
cd task-dataset-metric-extraction/BERT
DATA_DIR=../data/paperwithcode/0unk/twofoldwithunk/fold1
BERT_DIR=./model/uncased_L-12_H-768_A-12
Out_DIR=./model/0unkFold1BertBatch16
if [ -d "$Out_DIR" ]; then
  ### Take action if $DIR exists ###
  echo "Directory ${Out_DIR} Exist"
else
  ###  Control will jump here if $DIR does NOT exists ###
  echo "Directory ${Out_DIR} Created"
  mkdir $Out_DIR
fi
python run_classifier_sci.py --do_train=false --do_eval=true --do_predict=false --data_dir=$DATA_DIR --task_name=sci --vocab_file=$BERT_DIR/vocab.txt --bert_config_file=$BERT_DIR/bert_config.json --init_checkpoint=$BERT_DIR/bert_model.ckpt --output_dir=$Out_DIR --max_seq_length=512 --train_batch_size=6 --eval_batch_size=8 --predict_batch_size=6