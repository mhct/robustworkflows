#!/bin/bash #
# Setup environment for evaluating the CooS middleware 
#
# @mariohct
#



SSH_USER="u0061821"
ROOT="/Users/mario/Estudos/phd/software-projects/projects/evaluation_givemearide"
MONGO_CLIENT="/Users/mario/opt/mongo/bin/mongo"

DB_SERVER="andenne.cs.kotnet.kuleuven.be"
APP_SERVER="bastogne.cs.kotnet.kuleuven.be"

#
# Monitors a process
# @param String, name of the machine where the process is executing
# @param Integer, PID of the process
# @param String, name of output file
#
function pidstat {
    machine=$1
    pid=$2
    duration=$3
    output=$4

    ssh $SSH_USER@$machine pidstat -u -r -p $pid 2 $duration >> $output &
}


#
# Collects statistics of remote machines
#
# @param String, name of remote machine NOTE it is only the name, not the full name of the machine. the domain name is a variable
# @param Number, duration (in seconds) to monitor the machine
#
function sarIt {
    machine=$1
    duration=$2
    #ssh -n $SSH_USER@$machine sar -P 0 -u ALL 1 $duration > sar_$machine.txt &
    ssh -n $SSH_USER@$machine mpstat -u -P ALL 1 $duration > cpu_$machine.txt &
    ssh -n $SSH_USER@$machine iostat  -z -n -k 1 $duration > iostat_$machine.txt &
    pidstat $machine 26904 $duration pidstat_DB_$machine.txt
    pidstat $machine 12384 $duration pidstat_APP_$machine.txt
}


#
# command to load experiment
# TODO diest and aubel should be moved to constants at the top of the file
#
function experimentIt {
    numberOfpcs=$1
    #pcs=$(head -$numberOfpcs ../resources/map_pc_labs.txt|cut -d , -f 1)
    pcs=$(head -$numberOfpcs $ROOT/resources/map_pcs_temp.txt)
    sarIt $APP_SERVER 5500 
    for i in {0..14};
    do
        mkdir run_"$numberOfpcs"_"$i"
        cd run_"$numberOfpcs"_"$i"
    
        countIt $DB_SERVER mw2012
        sarIt $APP_SERVER 120
        sarIt $DB_SERVER 120   
        for pc in $pcs;
        do
            echo siegeIt $pc".cs.kotnet.kuleuven.be"
            siegeIt $pc".cs.kotnet.kuleuven.be"
            sleep 2
        done
    
        sleep 180 #waits for siege, which takes 120 seconds, to finishe
        countIt $DB_SERVER mw2012
        cd ..
    done
}

