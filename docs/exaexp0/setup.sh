#!/usr/local/bin/zsh 
# Setup environment for evaluating the RobustWorflows middleware 
#
# 17/07/2013
#
# @mariohct
#

SSH_USER="mariokul"
ROOT_FOLDER="/home/mariokul/robustworkflows"
LOGS_PATH=$ROOT_FOLDER"/logs"

GRAPH_LOADER_SERVER=$(hostname)
DB_SERVER=$(hostname)
DB_SERVER_PORT="27017"

SSH_OPTIONS=" -o PasswordAuthentication=no -o StrictHostKeyChecking=no " 

if [ -n "$PBS_NODEFILE" ]; then
        NUMBER_NODES=$(wc -l $PBS_NODEFILE)
else
        NUMBER_NODES=1
fi

#
# Loads the database.
# This function has to be called from within a node
#
# @LOCAL FUNCTION
#
function loadDatabase {
        /usr/bin/numactl --interleave=all $HOME/opt/mongo/bin/mongod \
            --logpath $LOGS_PATH/mongo.log --logappend \
            --dbpath  $ROOT_FOLDER/db_storage & 

}


#
# Loads GraphLoader
#
function loadGraphLoader {
    network_model=$1
    db_name=$2
    ssh $SSH_OPTIONS  $SSH_USER@$GRAPH_LOADER_SERVER DB_USER=$DB_USER DB_PASS=$DB_PASS \
            DB_SERVER_IP=$DB_SERVER DB_SERVER_PORT=$DB_SERVER_PORT DB_NAME=$db_name \
            SYSTEM_HOSTNAME=$GRAPH_LOADER_SERVER NETWORK_MODEL=$network_model \
            PATH=\$PATH:\$HOME/nvm/v0.6.14/bin/  \
            $ROOT_FOLDER/current/bin/startGraphLoaderApp \> \
            $LOGS_PATH/$db_name/graphloader.txt 2\>\&1 \& \
            echo PID: \$! & 
}


#
# Loads RobustWorkflows launcher application
#
function loadRobustWorkflowsApp {
    db_name=$1
    ssh  $SSH_OPTIONS $SSH_USER@$GRAPH_LOADER_SERVER DB_USER=$DB_USER \
            DB_PASS=$DB_PASS DB_SERVER_IP=$DB_SERVER DB_SERVER_PORT=$DB_SERVER_PORT \
            DB_NAME=$db_name SYSTEM_HOSTNAME=$GRAPH_LOADER_SERVER \
            PATH=\$PATH:\$HOME/nvm/v0.6.14/bin/  \
            $ROOT_FOLDER/current/bin/startRobustWorkflowsLauncher \> \
            $LOGS_PATH/$db_name/workflowsLauncher.txt  2\>\&1  \& \
            echo PID: \$! & 
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
    ssh $SSH_OPTIONS $SSH_USER@$sorcerer_server DB_USER=$DB_USER DB_PASS=$DB_PASS \
            DB_SERVER_IP=$DB_SERVER DB_SERVER_PORT=$DB_SERVER_PORT \
            SORCERER_NAME=$sorcerer_name SYSTEM_HOSTNAME=$sorcerer_server \
            DB_NAME=$db_name PATH=\$PATH:\$HOME/nvm/v0.6.14/bin/  \
            $ROOT_FOLDER/current/bin/startSorcerer \> \
            $LOGS_PATH/$db_name/$sorcerer_name.txt  2\>\&1  \& \
            echo PID: \$! & 
}



#
# Retrieves the list of active computers at the computer labs
#
# @Return Space separated string with the computer names which are Up
#
function getListActiveComputersCluster {
    numberOfpcs=$1
    local pcs=$(cat $PBS_NODEFILE)

    echo "$pcs"
}

#
# Load All needed sorcerers
#
# @Param Integer, number of computers to be used
#
function loadNeededSorcerers {
    numberOfpcs=$1
    db_name=$2
    local pcs=$(getListActiveComputersCluster)
    for pc in $pcs;
        do
            echo loading sorcerer at $pc
            loadSorcerer $pc $pc $db_name
            #sleep 2
        done
    
}


function showVars {
    echo "DB_USER:DB_PASS   =>  " $DB_USER:$DB_PASS
    echo
    echo "DB_ADMIN_USER:DB_ADMIN_PASS    => " $DB_ADMIN_USER:$DB_ADMIN_PASS
}

function stopIt {
    machine=$1
    job=$2
    ssh $SSH_OPTIONS $SSH_USER@$machine killall $job
}

function stopAll {
    local numberOfpcs=$1
    local pcs=$(getListActiveComputersCluster)
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
    ssh $SSH_OPTIONS  $SSH_USER@$DB_SERVER LC_ALL=C $HOME/opt/mongo/bin/mongo $dbName --authenticationDatabase admin -u $DB_ADMIN_USER -p $DB_ADMIN_PASS --eval \"db.addUser\(\'$DB_USER\',\'$DB_PASS\'\)\" 

}

function prepareLogFolders {
    folder=$1
     ssh $SSH_OPTIONS  $SSH_USER@$DB_SERVER LC_ALL=C mkdir $ROOT_FOLDER/logs/$folder 
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
    prepareLogFolders $db_name
    sleep 5
    prepareDBPermissions $db_name
    sleep 15
    #testInput
    loadNeededSorcerers $NUMBER_NODES $db_name
    sleep 10
    #testInput
    loadGraphLoader $network_model $db_name
    sleep 10
    #testInput
    loadRobustWorkflowsApp $db_name
else
    echo "DB_USER, DB_PASS, DB_ADMIN_USER, DB_ADMIN_PASS should be set before executing this script"
fi
}

function stopExt {
    stopAll $NUMBER_NODES 
    #stopIt verviers java
}

#
# Compile Robustworkflows and deploy it 
#
function deploy {
    mvn package -DskipTests
    scp target/RobustWorkflows-Actors-0.0.1-SNAPSHOT.jar  $SSH_USER@$DB_SERVER:/home/$SSH_USER/robustworkflows/current/deploy
    rsync -avz -e ssh datasets/* $SSH_USER@$DB_SERVER:/home/$SSH_USER/robustworkflows/
    echo Current Time $(date)
}
    
#
# Check for leftover processes (still running) on nodes.
#
function leftover {
    activeNodes=$(isub -status|grep F|cut -f 1 -d" ")
    for i in $activeNodes; 
    do
            echo NODE: $i
            ssh $i ps auxw\|grep mongo
    done
}

