evsec = function() {
        previousEvents=db.model_events.find().size();
        sleep(10000);
        for (i=0; i<10; i++) {
                currentEvents = db.model_events.find().size();
                print("Events/second: " + (currentEvents - previousEvents)/10);
                previousEvents = currentEvents;
                sleep(10000);
        }
}

db.model_events.mapReduce(
                function() { emit( this.FactoryAgent, 1); },
                function(key, values) { return Array.sum( values ); },
                {
                        query: {EventType:'ServiceRequest'},
                        out: "messages_totals"
                }
                )

db.model_events.mapReduce(
                function() { emit( this.ClientAgent, 1); },
                function(key, values) { return Array.sum( values ); },
                {
                        query: {ClientAgent: {$exists:true}},
                        out: "messages_totals"
                }
                )


missingCompositions = function() {
        clientCodes = new Array();

        for(i=1000; i<1
        clients = db.model_events.find({EventType:'SERVICE_COMPOSITION_SUMMARY'}, {ClientAgent:1});

