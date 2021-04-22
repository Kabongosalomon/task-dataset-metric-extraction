#!/bin/bash
cd task-dataset-metric-extraction/XLNet
DATA_DIR=../data/paperwithcode/50unk/twofoldwithunk/fold1
XLNet_DIR=./model/xlnet_cased_L-24_H-1024_A-16
Out_DIR=./model/50unkFold1XLNetBatch6Lr1e-5Train5000Warmup500Save500Iterations1000
if [ -d "$Out_DIR" ]; then
  ### Take action if $DIR exists ###
  echo "Directory ${Out_DIR} Exist"
else
  ###  Control will jump here if $DIR does NOT exists ###
  echo "Directory ${Out_DIR} Created"
  mkdir $Out_DIR
fi
python run_sci_classifier.py --use_tpu=False --do_train=True --do_eval=False --do_predict=False --task_name=sci --data_dir=$DATA_DIR --output_dir=$OUTPUT_DIR --model_dir=$Out_DIR --uncased=False --model_config_path=$XLNet_DIR/xlnet_config.json --spiece_model_file=$XLNet_DIR/spiece.model --init_checkpoint=$XLNet_DIR/xlnet_model.ckpt --max_seq_length=512 --train_batch_size=3 --num_hosts=1 --num_core_per_host=8 --learning_rate=1e-5 --train_steps=5000 --warmup_steps=500 --save_steps=500 --iterations=1000