#!/bin/bash
cd ../../XLNet # Moove into Bert Folder
DATA_DIR=../data/paperwithcode/60Neg800unk/twofoldwithunk/fold1
# XLN_DIR=./model/xlnet_cased_L-24_H-1024_A-16
XLN_DIR=./model/xlnet_cased_L-12_H-768_A-12
Out_DIR="${DATA_DIR}/models/cased_L-12_H-768_A-12_XLNet/"
if [ -d "$Out_DIR" ]; then
  ### Take action if $DIR exists ###
  echo "Directory ${Out_DIR} Exist"
else
  ###  Control will jump here if $DIR does NOT exists ###
  echo "Directory ${Out_DIR} Created"
  mkdir $Out_DIR
fi

python run_sci_classifier.py --use_tpu=False --do_train=true --do_eval=false --do_predict=true --task_name=sci --data_dir=$DATA_DIR --output_dir=$Out_DIR --predict_dir=$Out_DIR --model_dir=$Out_DIR --uncased=False --model_config_path=$XLN_DIR/xlnet_config.json --spiece_model_file=$XLN_DIR/spiece.model --init_checkpoint=$XLN_DIR/xlnet_model.ckpt --max_seq_length=512 --train_batch_size=6 --predict_batch_size=6 --num_hosts=1 --num_core_per_host=1 --learning_rate=1e-5 --train_steps=5000 --warmup_steps=500 --save_steps=500 --iterations=1000