#!/bin/bash #
# Setup environment for evaluating the RobustWorflows middleware 
#
# 17/07/2013
#
# @mariohct
#



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
# Collects DB statistics 
#
# @param, String, name of remote machine
function countIt {
    machine=$1
    db=$2
    $MONGO_CLIENT $machine/$db ~/Estudos/phd/software-projects/projects/evaluation_givemearide/count_drop.js > registers_$machine.txt &
}

#
# Stop jobs at remote machines
#
# @param String, name of machine
# @param String, name of job to be stopped
#
function stopIt {
    machine=$1
    job=$2
    ssh $SSH_USER@$machine.cs.kotnet.kuleuven.be killall $job
}

