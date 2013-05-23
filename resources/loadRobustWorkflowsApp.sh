#!/bin/bash

#
# 22/05/2013
# @mariohct
#
# Load the RobustWorflows application and the necessary auxiliary apps, such as databases, agent launchers, etc.
# This script works on MAC OS X, change the load_app function to port to other OS.
#

DB_PATH="/Users/mario/opt/mongo/bin"
CURRENT_PWD=$(pwd)


function load_app {
	COMMAND=$1

    #osascript -sh -e "$COMMAND"
    #$(/usr/bin/osascript -sh -e "$COMMAND")
    osascript 2>/dev/null <<EOF
        tell application "Terminal"
            activate
            do script with command "$COMMAND"
        end tell
EOF

}

# 
# Load DB server using standard parameters
#
load_app "cd $DB_PATH;./mongod" 
sleep 10

#
# Load Mongo Client with administrative scripts loaded
#
load_app "$DB_PATH/mongo $CURRENT_PWD/../config/runadm.js;$DB_PATH/mongo"

#
# Load the SorcererApp
#
load_app "cd $CURRENT_PWD;./startSorcerer"
sleep 10

#
# Load the GraphLoaderApp
#
load_app "cd $CURRENT_PWD;./startGraphLoaderApp"


