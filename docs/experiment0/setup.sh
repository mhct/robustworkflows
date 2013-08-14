#!/bin/bash #
# Setup environment for evaluating the RobustWorflows middleware 
#
# 17/07/2013
#
# @mariohct
#



APP_URI="http://aubel.cs.kotnet.kuleuven.be:3000"
SSH_USER="u0061821"
ROOT="/Users/mario/Estudos/phd/software-projects/projects/evaluation_givemearide"
MONGO_CLIENT="/Users/mario/opt/mongo/bin/mongo"

DB_SERVER="andenne.cs.kotnet.kuleuven.be"
APP_SERVER="bastogne.cs.kotnet.kuleuven.be"

#
# Activates the database
#
function mongodbUp {
    ssh $SSH_USER@$DB_SERVER \$HOME/givemearide_project/mongo/bin/mongod --auth --dbpath \$HOME/givemearide_project/db \& echo PID: \$!
    #\& pidstat -r -p \$! 1 86400 
}

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
# Activates the GiveMeARide Cloud Component
#
function givemearidecloudUp {
ssh $SSH_USER@$APP_SERVER DB_USER=coffee DB_PASS=passwordForCoffee DB_URI=$DB_SERVER":27017" DB_DB_NAME=mw2012 PATH=\$PATH:\$HOME/nvm/v0.6.14/bin/  \$HOME/nvm/v0.6.14/bin/coffee \$HOME/givemearide_project/givemearide/web/server.coffee \& echo PID: \$! 
}


#
# creates 102 taxis
#
function createTaxis {
    for taxiId in {20000..30000};
    do
        #echo $taxiId
        curl  -H "Content-Type: application/json" -X POST -d "{\"taxiRegistration\":{\"taxiId\":$taxiId,\"currentLocation\":{\"latitude\":50.856024,\"longitude\":4.695738},\"headingToLocation\":{\"latitude\":20,\"longitude\":10},\"hasPassenger\":false}}" $APP_URI"/taxi"
    done
}

#
# Creates taxis
#
function createTaxisSiege {
    siegeFile=$1
    for taxiId in {1..100000};
    do
        echo $APP_URI"/taxi" POST  "{\"taxiRegistration\":{\"taxiId\":$taxiId,\"currentLocation\":{\"latitude\":50.856024,\"longitude\":4.695738},\"headingToLocation\":{\"latitude\":20,\"longitude\":10},\"hasPassenger\":false}}" >> $siegeFile
    done
}

#createTaxisSiege taxis_siege.txt

#
# Siege
#
# @param String, name of the machine to use as starting point from siege
function siegeIt {
    machine=$1
    ssh  -n $SSH_USER@$machine \$HOME/givemearide_project/siege/src/siege -t 2M -c200 --header "Content-type:application/json" -f \$HOME/givemearide_project/evaluation/taxis_siege.txt 2> $machine.siege &
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

