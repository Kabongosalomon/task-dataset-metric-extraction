#!/bin/bash
#SBATCH --output=TDMBert16Fold1.output
#SBATCH --ntasks=1
#SBATCH --gres=gpu:t8000:1                          # (1 * 48GB GPU , gpu:t8000:1)
#SBATCH --mail-type=END                             # Type of email notification- BEGIN,END,FAIL,ALL
#SBATCH --mail-user=salomon.kabenamualu@tib.eu      # Email to which notifications will be sent

# bash code.sh
srun singularity exec --nv  /nfs/home/kabenamualus/TDM.simg "$@"