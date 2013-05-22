03/05/2013
@mariohct

RobustWorkflows
===============
RobustWorkflows is a software emulator of agent networks. Agents are implemented as Actors and are deployed according 
to a description given by a graph. The goal of the emulator is to understand the behaviour of distributed systems
made by agents. Each agent has its own behaviour and it is quite hard to understand how a system of such agents 
behaves, since the system behaviour derives from the interaction between several agents.


This project is composed by an emulation platform and a MultiAgent System (MAS) model. The emulation infrastructure provides
facilities to deploy a MAS on a distributed environment. The emulation infrastructure reads a graph model in a file, supported
by the Gephi package, and loads each node from the graph as an agent on a distributed system.

The configuration uses the AKKA configuration library, it is at
$ resources/application.conf 

More design documentation can be found at the docs/ foder. The documentation is in the format of UMLet


Requirements
============
- MongoDB
- Java
- Maven


Architecture
============
The whole system is composed by two different types of system. The first type is an application called 
"GraphLoaderApplication" which reads a graph description from a file and requests the second application type to load
 the required components (Agent actors).
 
The second application type is the "SorcererApplication", responsible for loading AgentActors as demanded.

The GraphLoaderApplication knows about the existence of all SorcerersApplications and can load balance the load of Agents 
on top of the Sorcerers. 

After the agents are loaded, the GraphLoaderApplication can just be turned off, since the agents interact independently.

There is a MongoDB to maintain the state of the infrastructure and the logs of the Model of the emulation. The infrastructure uses
the MongoDB to indicate the location of the several Sorcerers. The Agents use the MongoDB to log anything they want, and that pertains 
to the model the agents represent.

Infrastructure
==============
The infrastruture is responsible for providing an environment for loading the agents and allowing them to communicate.
There are two entities responsible for maintaining the infrastructure. The GraphLoaderApplication is responsible for loading a
model and requesting the creation of the agents defined in the model. 

The second part of the infrastructure is made by the SorcererApplication which is responsible for instantiating Agents, when requested.

There is a blackboard for infrastructure events, the InfrastructureStorage, which persists infrastructure events on a MongoDB database.


Model
=====
The current model is composed by Client and Factory Agents. 
Factory agents have a Computational Profile which represents how the agent operates.
Check the following files. 

docs/Client-states.uxf          State diagram for the Client Agent
docs/ClientAgentDesign.uxf      Design of the Client Agent
docs/FactoryAgent-states.uxf    State diagram for the Factory Agent
 
 
Starting up from the SHELL
=======================================
- load the MongoDB
- load the GraphLoaderApplication
- load the SorcererApplication


New method is to enter the bin directory created by "mvn package"  and type
./loadRobustWorkflowsApp.sh


Starting up from Eclipse
========================


