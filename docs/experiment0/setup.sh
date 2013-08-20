#!/bin/bash #
# Setup environment for evaluating the RobustWorflows middleware 
#
# 17/07/2013
#
# @mariohct
#

SSH_USER="u0061821"
#ROOT="/Users/mario/Estudos/phd/software-projects/projects/evaluation_givemearide"
#MONGO_CLIENT="/Users/mario/opt/mongo/bin/mongo"

GRAPH_LOADER_SERVER="aalst.cs.kotnet.kuleuven.be"

DB_SERVER="andenne.cs.kotnet.kuleuven.be"
DB_SERVER_PORT="27017"

#
# Loads the database
#
function loadDatabase {
    ssh $SSH_USER@$DB_SERVER LC_ALL=C \$HOME/mongo/bin/mongod --auth --dbpath \$HOME/robustworkflows/db_storage \& echo PID: \$!
    #\& pidstat -r -p \$! 1 86400 
}


#
# Loads GraphLoader
#
function loadGraphLoader {
    ssh $SSH_USER@$GRAPH_LOADER_SERVER DB_USER=$DB_USER DB_PASS=$DB_PASS DB_SERVER_IP=$DB_SERVER DB_SERVER_PORT=$DB_SERVER_PORT SYSTEM_HOSTNAME=$GRAPH_LOADER_SERVER PATH=\$PATH:\$HOME/nvm/v0.6.14/bin/  \$HOME/robustworkflows/current/bin/startGraphLoaderApp \& echo PID: \$! & 
}


#
# Loads RobustWorkflows launcher application
#
function loadRobustWorkflowsApp {
    ssh $SSH_USER@$GRAPH_LOADER_SERVER DB_USER=$DB_USER DB_PASS=$DB_PASS DB_SERVER_IP=$DB_SERVER DB_SERVER_PORT=$DB_SERVER_PORT SYSTEM_HOSTNAME=$GRAPH_LOADER_SERVER PATH=\$PATH:\$HOME/nvm/v0.6.14/bin/  \$HOME/robustworkflows/current/bin/startRobustWorkflowsLauncher \& echo PID: \$! 
}


#
# Loads Sorcerer
#
# @Param String, IP or complete NAME of host
# @Param String, Name given to the sorcerer
#
function loadSorcerer {
    sorcerer_server=$1
    sorcerer_name=$2
    ssh -n $SSH_USER@$sorcerer_server DB_USER=$DB_USER DB_PASS=$DB_PASS DB_SERVER_IP=$DB_SERVER DB_SERVER_PORT=$DB_SERVER_PORT SORCERER_NAME=$sorcerer_name SYSTEM_HOSTNAME=$sorcerer_server PATH=\$PATH:\$HOME/nvm/v0.6.14/bin/  \$HOME/robustworkflows/current/bin/startSorcerer \& echo PID: \$! & 
}


#
# Load All neded sorcerers
#
# @Param Integer, number of computers to be used
#
function loadNeededSorcerers {
    numberOfpcs=$1
    pcs=$(head -$numberOfpcs ./pc_lab_computer_names.txt)
        for pc in $pcs;
        do
            echo loading sorcerer at $pc".cs.kotnet.kuleuven.be"
            loadSorcerer $pc".cs.kotnet.kuleuven.be" $pc
            sleep 2
        done
    
}


function showVars {
    echo $DB_USER " : " $DB_PASS
}

function stopIt {
    machine=$1
    job=$2
    ssh $SSH_USER@$machine.cs.kotnet.kuleuven.be killall $job
}

