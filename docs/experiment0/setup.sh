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

GRAPH_LOADER_SERVER="verviers.cs.kotnet.kuleuven.be"

DB_SERVER="andenne.cs.kotnet.kuleuven.be"
DB_SERVER_PORT="27017"

SSH_OPTIONS=" -o PasswordAuthentication=no -o StrictHostKeyChecking=no " 
#
# Loads the database
#
function loadDatabase {
    ssh $SSH_OPTIONS  $SSH_USER@$DB_SERVER LC_ALL=C \$HOME/mongo/bin/mongod --auth \
            --logpath \$HOME/robustworkflows/mongo.log --logappend \
            --dbpath \$HOME/robustworkflows/db_storage \& echo PID: \$! &
    #\& pidstat -r -p \$! 1 86400 
}


#
# Loads GraphLoader
#
function loadGraphLoader {
    network_model=$1
    db_name=$2
    ssh $SSH_OPTIONS  $SSH_USER@$GRAPH_LOADER_SERVER DB_USER=$DB_USER DB_PASS=$DB_PASS DB_SERVER_IP=$DB_SERVER DB_SERVER_PORT=$DB_SERVER_PORT DB_NAME=$db_name SYSTEM_HOSTNAME=$GRAPH_LOADER_SERVER NETWORK_MODEL=$network_model PATH=\$PATH:\$HOME/nvm/v0.6.14/bin/  \$HOME/robustworkflows/current/bin/startGraphLoaderApp \& echo PID: \$! & 
}


#
# Loads RobustWorkflows launcher application
#
function loadRobustWorkflowsApp {
    db_name=$1
    ssh  $SSH_OPTIONS $SSH_USER@$GRAPH_LOADER_SERVER DB_USER=$DB_USER DB_PASS=$DB_PASS DB_SERVER_IP=$DB_SERVER DB_SERVER_PORT=$DB_SERVER_PORT DB_NAME=$db_name SYSTEM_HOSTNAME=$GRAPH_LOADER_SERVER PATH=\$PATH:\$HOME/nvm/v0.6.14/bin/  \$HOME/robustworkflows/current/bin/startRobustWorkflowsLauncher \& echo PID: \$! 
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
    db_name=$3
    ssh $SSH_OPTIONS $SSH_USER@$sorcerer_server DB_USER=$DB_USER DB_PASS=$DB_PASS DB_SERVER_IP=$DB_SERVER DB_SERVER_PORT=$DB_SERVER_PORT SORCERER_NAME=$sorcerer_name SYSTEM_HOSTNAME=$sorcerer_server DB_NAME=$db_name PATH=\$PATH:\$HOME/nvm/v0.6.14/bin/  \$HOME/robustworkflows/current/bin/startSorcerer \>\> /home/u0061821/robustworkflows/logs 2\>\&1  \& echo PID: \$! & 
}



#
# Retrieves the list of active computers at the computer labs
#
# @Return Space separated string with the computer names which are Up
#
function getListActiveComputers {
    numberOfpcs=$1
    machinesToIgnore="-e mol -e andenne -e arenberg -e baba -e bibi -e brussel -e heverlee -e matata -e jambo -e leuven -e tabor -e even -e odd"
    local pcs=$(ssh u0061821@andenne.cs.kotnet.kuleuven.be ruptime | awk '{if ("up" == $2) { print $1}}'|grep -v $machinesToIgnore | head -$numberOfpcs)

    echo "$pcs"
}

#
# Load All neded sorcerers
#
# @Param Integer, number of computers to be used
#
function loadNeededSorcerers {
    numberOfpcs=$1
    db_name=$2
    local pcs=$(getListActiveComputers $numberOfpcs)
    for pc in $pcs;
        do
            echo loading sorcerer at $pc".cs.kotnet.kuleuven.be"
            loadSorcerer $pc".cs.kotnet.kuleuven.be" $pc $db_name
            #sleep 2
        done
    
}


function showVars {
    echo $DB_USER " : " $DB_PASS
    echo $DB_ADMIN_USER " : " $DB_ADMIN_PASS
}

function stopIt {
    machine=$1
    job=$2
    ssh $SSH_OPTIONS $SSH_USER@$machine.cs.kotnet.kuleuven.be killall $job
}

function stopAll {
    local numberOfpcs=$1
    local pcs=$(getListActiveComputers $numberOfpcs)
    for pc in $pcs;
        do
                echo stoping sorcerer at $pc
                stopIt $pc java
        done

}

function checkDBUser {
    if [ -z "$DB_USER" ] || [ -z "$DB_PASS" ] || [ -z "$DB_ADMIN_USER" ] || [ -z "$DB_ADMIN_PASS" ]; then
           echo "false"; 
   else
         echo "true"  
   fi
   
}

function  prepareDBPermissions {
    dbName=$1
    ssh $SSH_OPTIONS  $SSH_USER@$DB_SERVER LC_ALL=C \$HOME/mongo/bin/mongo $dbName --authenticationDatabase admin -u $DB_ADMIN_USER -p $DB_ADMIN_PASS --eval \"db.addUser\(\'$DB_USER\',\'$DB_PASS\'\)\" 

}

function testInput {
    read -p "Next step (y,n)?" yn
    case $yn in
        [Yy]* ) break;;
        [Nn]* ) exit;;
            * ) echo "Please answer yes or no.";;
    esac
}

function exp {
    network_model=$1
    db_name=$2
if [ "true" == "$(checkDBUser)" ] && [ -n $network_model ] && [ -n $b_name ]; then
    #loadDatabase
    #sleep 15
    prepareDBPermissions $db_name
    testInput
    loadNeededSorcerers 50 $db_name
    testInput
    loadGraphLoader $network_model $db_name
    testInput
    loadRobustWorkflowsApp $db_name
else
    echo "DB_USER, DB_PASS, DB_ADMIN_USER, DB_ADMIN_PASS should be set before executing this script"
fi
}

function stopExt {
    stopAll 50
    stopIt verviers java
#    stopIt andenne mongod
}
