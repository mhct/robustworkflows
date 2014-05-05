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
# Loads a single mongodb instance
#
function loadDb {
    /usr/bin/numactl --interleave=all $HOME/opt/mongo/bin/mongod \
                --auth \
                --logpath $LOGS_PATH/mongo_single.log --logappend \
                --dbpath /scratch/mariokul/mongodb/ 2>&1  & 
}

#
# Loads a mongodb cluster.
# This function has to be called from within a node
#
# @LOCAL FUNCTION
#
function loadDbCluster {
    #
    # Loads master database
    #

    local current_node=$(hostname)

    local active_nodes=$(getActiveNodes)
    local secondary_nodes=$(echo $active_nodes|sed s/$current_node//)
    
    #/usr/bin/numactl --interleave=all $HOME/opt/mongo/bin/mongod \
    #        --config $HOME/robustworkflows/config/mongodb.conf \
    #        --logpath $LOGS_PATH/mongo$current_node.log --logappend \
    #        --dbpath /scratch/mariokul/mongodb/
    #        #--dbpath  $ROOT_FOLDER/db_storage & 

    #
    # Loads slave databases
    #
    for node in $(getActiveNodes);
    do
            echo "loading database at $node"
            ssh $SSH_OPTIONS  $node /usr/bin/numactl --interleave=all \$HOME/opt/mongo/bin/mongod \
                --auth \
                 --config \$HOME/robustworkflows/config/mongodb.conf \
                --logpath $LOGS_PATH/mongo_$node.log --logappend  2\>\&1 \&
    done

                #--dbpath /scratch/mariokul/mongodb/ 2\>\&1  \& 
    #
    # Configure REPLICA set at primary
    #
    local replica_set=""

    for node in $secondary_nodes;
    do
            replica_set="$replica_set rs.add('"$node"');"
    done
    
    #
    # Execute command on local mongod
    # Currently, not using authentication
    echo "Creating Replica set at local MongoDB"
    echo "Secondaries: $replica_set"

    $HOME/opt/mongo/bin/mongo  admin --eval "rs.initiate(); "
    #$HOME/opt/mongo/bin/mongo  --authenticationDatabase admin -u $DB_ADMIN_USER -p $DB_ADMIN_PASS --eval "rs.initiate(); $replica_set"
       #rs.conf()
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
    sorcerer_port=$3
    db_name=$4

    ssh $SSH_OPTIONS $SSH_USER@$sorcerer_server DB_USER=$DB_USER DB_PASS=$DB_PASS \
            DB_SERVER_IP=$DB_SERVER DB_SERVER_PORT=$DB_SERVER_PORT \
            SORCERER_NAME=$sorcerer_name SORCERER_PORT=$sorcerer_port SYSTEM_HOSTNAME=$sorcerer_server \
            DB_NAME=$db_name  \
            $ROOT_FOLDER/current/bin/startSorcerer \> \
            $LOGS_PATH/$db_name/$sorcerer_name_$sorcerer_port.txt  2\>\&1  \& \
            echo PID: \$! & 
}



#
# Retrieves the list of active computers at the computer labs
#
# @Return Space separated string with the computer names which are Up
#
function getActiveNodes {
    local pcs=$(cat /home/mariokul/active_nodes.txt)

    echo "$pcs"
}

#
# Load All needed sorcerers
#
# @Param Integer, number of computers to be used
#
function loadNeededSorcerers {
    local db_name=$1
    local nbSorcerersPerServer=$2

    local initialPort=30000
    local pcs=$(getActiveNodes)
    for pc in $pcs;
        do
            COUNTER=0
            while [ $COUNTER -lt $nbSorcerersPerServer ]; do
                    local port=$(($initialPort + $COUNTER))
                    echo loading sorcerer at $pc port $port
                    loadSorcerer $pc $pc $port $db_name
                    COUNTER=$(($COUNTER + 1))
                    #sleep 2
            done
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
    local pcs=$(getActiveNodes)
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
    concurrent=$3
    local nbSorcerersPerServer=$4
if [ "true" == "$(checkDBUser)" ] && [ -n $network_model ] && [ -n $db_name ]; then
    #loadDatabase
    #sleep 15
    prepareLogFolders $db_name
    sleep 5
    prepareDBPermissions $db_name
    sleep 15
    #testInput
    loadNeededSorcerers $db_name $nbSorcerersPerServer
    sleep 10
    #testInput
    loadGraphLoader $network_model $db_name
    sleep 100
    setConcurrency $concurrent
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
    for i in $(cat /home/mariokul/active_nodes.txt); 
    do
            echo NODE: $i
            ssh $i ps auxw\|grep -E \"\(java\|mongod\)\"
            ssh $i killall java
            ssh $i killall mongod
    done
}


#
# Check which nodes are available for using on the robustworkflows.
# 
function activenodes {
    cat $PBS_NODEFILE > /home/mariokul/active_nodes.txt
}

function setConcurrency {
    sed s/CONCURRENT_CLIENTS_TEMPLATE/$1/   $ROOT_FOLDER/current/config/application_template.conf > $ROOT_FOLDER/current/config/application.conf

}

