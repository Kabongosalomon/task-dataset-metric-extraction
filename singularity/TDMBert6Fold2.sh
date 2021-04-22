#!/bin/bash
#SBATCH --output=TDMBert6Fold2.output
#SBATCH --ntasks=1
#SBATCH --gres=gpu:t2080ti:1                        # 1 * 11GB GPU
#SBATCH --mail-type=ALL                             # Type of email notification- BEGIN,END,FAIL,ALL
#SBATCH --mail-user=Salomon.Kabenamualu@tib.eu      # Email to which notifications will be sent

# bash code.sh
srun singularity exec --nv  /nfs/home/kabenamualus/TDM.simg "$@"