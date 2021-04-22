#!/bin/bash
#SBATCH --output=../../data/paperwithcode/500Neg300unk50Thres/twofoldwithunk/fold1/models/BERT/R-%x.%j.out                        # This allow to customize the output with file name and job ID
#SBATCH --ntasks=1
#SBATCH --gres=gpu:t8000:1                          # t2080ti (11GB GPU), t8000 (48GB GPU)
#SBATCH --mail-type=ALL                             # Type of email notification- BEGIN,END,FAIL,ALL
#SBATCH --mail-user=Salomon.Kabenamualu@tib.eu      # Email to which notifications will be sent

# bash code.sh
srun singularity exec --nv  /nfs/home/kabenamualus/Research/task-dataset-metric-extraction/lab.sif "$@"