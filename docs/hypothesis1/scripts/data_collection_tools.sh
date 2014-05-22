#!/bin/bash

#
# fix files
# c=2; for i in $a; do mv $i h1_250_$c; c=$(($c+1)); done
#

PATH_TO_EXPERIMENTS="/home/mariokul/robustworkflows/logs"
EXPERIMENTS_SERVER="mariokul@lynx"

#
# add loop to collect multiple executions
#
function collectall {
    local experiment_base_name=$1
    list_folders=$(ssh mariokul@lynx "ls -1vd /home/mariokul/robustworkflows/logs/$experiment_base_name*")
    echo $list_folders > /tmp/list_folders
    while read i; 
    do
            #echo "uau" $i
            exp_name=$(echo $i|awk 'BEGIN{FS="/"}{print $NF}')
            echo "BLA ""$exp_name"
            collect "$exp_name"
    done < /tmp/list_folders
}

function collectBB {
 echo oh my
}

function collect {

    experiment_name=$1
    echo 'ssh $EXPERIMENTS_SERVER "/bin/grep Summary  $PATH_TO_EXPERIMENTS/$experiment_name/*" \| "/usr/bin/cut -d= -f2" \> $PATH_TO_EXPERIMENTS/$experiment_name/summary_compositions.csv'

    ssh $EXPERIMENTS_SERVER "/bin/grep Summary  $PATH_TO_EXPERIMENTS/$experiment_name/*" \| "/usr/bin/cut -d= -f2" \> $PATH_TO_EXPERIMENTS/$experiment_name/summary_compositions.csv

    echo scp $EXPERIMENTS_SERVER:$PATH_TO_EXPERIMENTS/$experiment_name/summary_compositions.csv ./summary_compositions_$experiment_name
    scp $EXPERIMENTS_SERVER:$PATH_TO_EXPERIMENTS/$experiment_name/summary_compositions.csv /tmp/temp_exp_data
    
    echo time_block,EXPECTED_TIME_TO_SERVE_COMPOSITION,REAL_TIME_TO_SERVE_COMPOSITION,CLIENT_AGENT,SERVICES_ENGAGED,start_time,start_time_millis > $experiment_name
    cat /tmp/temp_exp_data >> $experiment_name

    nbCompositions=$(wc -l ./$experiment_name)
    echo "Number Compositions: " $nbCompositions

    ssh $EXPERIMENTS_SERVER "/bin/grep suitable $PATH_TO_EXPERIMENTS/$experiment_name/*" > failed_compositions_$experiment_name

    #
    # retrieve factories queues
    #
    ssh $EXPERIMENTS_SERVER "/bin/grep FactoryAgentQueue $PATH_TO_EXPERIMENTS/$experiment_name/*" \| "/usr/bin/cut -d= -f2" > queue_$experiment_name

    nbFailedCompositions=$(wc -l ./failed_compositions_$experiment_name)
    echo "Number Failed Compositions: " $nbFailedCompositions
}
