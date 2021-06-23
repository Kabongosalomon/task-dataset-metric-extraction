#!/bin/bash
#SBATCH --output=R-%x.%j.out                        # This allow to customize the output with file name and job ID
#SBATCH --ntasks=1
#SBATCH --gres=gpu:t8000:1                          # 1 * 11GB GPU, t2080ti
#SBATCH --mail-type=ALL                             # Type of email notification- BEGIN,END,FAIL,ALL
#SBATCH --mail-user=Salomon.Kabenamualu@tib.eu      # Email to which notifications will be sent

# bash code.sh
srun singularity exec --nv  /nfs/home/kabenamualus/Research/task-dataset-metric-extraction/lab.sif "$@"