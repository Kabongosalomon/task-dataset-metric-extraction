#!/bin/bash
#SBATCH --output=TDMBert32Fold2.output
#SBATCH --ntasks=1
#SBATCH --gres=gpu:t8000:1              # 48 GB GPU
#SBATCH --mail-type=ALL                             # Type of email notification- BEGIN,END,FAIL,ALL
#SBATCH --mail-user=Salomon.Kabenamualu@tib.eue     # Email to which notifications will be sent

# bash code.sh
srun singularity exec --nv  /nfs/home/kabenamualus/TDM.simg "$@"