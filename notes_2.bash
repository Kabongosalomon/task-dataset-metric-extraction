
# Wen I rename test as eval 
INFO:tensorflow:***** Eval results *****
INFO:tensorflow:  eval_accuracy = 0.9780785
INFO:tensorflow:  eval_loss = 0.09085037
INFO:tensorflow:  global_step = 6630
INFO:tensorflow:  loss = 0.090821974

# Wen I rename train as eval 
INFO:tensorflow:***** Eval results *****
INFO:tensorflow:  eval_accuracy = 0.97888386
INFO:tensorflow:  eval_loss = 0.084364146
INFO:tensorflow:  global_step = 6630
INFO:tensorflow:  loss = 0.08433911





# Running experiemnt on Singularity 

## Jupyter lab
srun -w devbox4 --gres=gpu:t2080ti:1 singularity exec -B /nfs/home/kabenamualus/Research/:/run/user lab.sif jupyter-lab
## snatch
sbatch 80TDM600Bert64Fold1.sh bash code80Train600F1.sh 

## File transfer
rsync -avP devbox3:/nfs/home/kabenamualus/Research/task-dataset-metric-extraction/BERT/model/80Neg600unkFold1BertBatch32/eval_results.txt /home/salomon/Desktop/task-dataset-metric-extraction/data/paperwithcode/

rsync -avP /home/salomon/Desktop/task-dataset-metric-extraction/data/paperwithcode devbox4:/nfs/home/kabenamualus/Research/

