# bankanwendung
Bankanwendung zur Bearbeitung der Leistungsnachweise des Moduls Skalierbare Anwendungen in der Cloud

## Hinweis zu Lomobok
In diesem Projekt wird Lombok eingesetzt. Aus diesem Grund muss in der IDE das entsprechende Plugin zum Kompatiblität installiert sein. Unter VS Code ist dies das "Lombok Annotations Support for VS Code"-Plugin zu beziehen aus dem VS-Code-Marketplace. Unter Eclispe muss die lombok.jar als Java-Applicationa ausgeführt werden. 

# Created by Sina Wittkamp, Luca Sahlman and Timo Reitmann
# Forked By Wesson Snyder

I 	Codebase 	There should be exactly one codebase for a deployed service with the codebase being used for many deployments.
II 	Dependencies 	All dependencies should be declared, with no implicit reliance on system tools or libraries.
III 	Config 	Configuration that varies between deployments should be stored in the environment.
IV 	Backing services 	All backing services are treated as attached resources and attached and detached by the execution environment.
V 	Build, release, run 	The delivery pipeline should strictly consist of build, release, run.
VI 	Processes 	Applications should be deployed as one or more stateless processes with persisted data stored on a backing service.
VII 	Port binding 	Self-contained services should make themselves available to other services by specified ports.
VIII 	Concurrency 	Concurrency is advocated by scaling individual processes.
IX 	Disposability 	Fast startup and shutdown are advocated for a more robust and resilient system.
X 	Dev/Prod parity 	All environments should be as similar as possible.
XI 	Logs 	Applications should produce logs as event streams and leave the execution environment to aggregate.
XII 	Admin Processes 	Any needed admin tasks should be kept in source control and packaged with the application.