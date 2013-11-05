#!/bin/bash #
# Setup environment for evaluating the RobustWorflows middleware 
#
# 28/10/2013
#
# @mariohct
#

#
# Exports a summary of an experiment trial
#
function mexport {
    db_name=$1
    output_file=$2

    mongoexport -h andenne.cs.kotnet.kuleuven.be -u $DB_USER -p $DB_PASS -d $db_name \
    -c model_events  -f time_block,EXPECTED_TIME_TO_SERVE_COMPOSITION,\
    REAL_TIME_TO_SERVE_COMPOSITION,ClientAgent,SERVICES_ENGAGED,run \
    -q \{EventType:\'SERVICE_COMPOSITION_SUMMARY\'\} --csv > $output_file
}

