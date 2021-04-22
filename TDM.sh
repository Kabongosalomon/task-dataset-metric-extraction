#!/bin/bash
#SBATCH --ntasks=1
#SBATCH --gres=gpu:t2080ti:2

srun singularity exlsec --nv  /nfs/home/kabenamualus/TDM.simg "cd task-dataset-metric-extraction/BER; DATA_DIR=../data/paperwithcode/0unk/twofoldwithunk/fold1;BERT_DIR=./model/uncased_L-12_H-768_A-12;Out_DIR=./model/0unkFold1BertBatch125; python run_classifier_sci.py --do_train=true --do_eval=false --do_predict=false --data_dir=$DATA_DIR --task_name=sci --vocab_file=$BERT_DIR/vocab.txt --bert_config_file=$BERT_DIR/bert_config.json --init_checkpoint=$BERT_DIR/bert_model.ckpt --output_dir=$Out_DIR --max_seq_length=512 --train_batch_size=125 --predict_batch_size=6"