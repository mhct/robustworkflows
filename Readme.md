03/05/2013
@mariohct

RobustWorkflows
===============

This project is composed by an emulation platform and a MultiAgent System (MAS) model. The emulation infrastructure provides
facilities to deploy a MAS on a distributed environment. The emulation infrastructure reads a graph model in a file, supported
by the Gephi package, and loads each node from the graph as an agent on a distributed system.

The configuration uses the AKKA configuration library, it is at
$ resources/application.conf 

Infrastructure
==============


Model
=====
The current model is composed by Client and Factory Agents. 
Factory agents have a Computational Profile which represents how the agent operates.
