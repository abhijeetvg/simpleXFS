simpleXFS
=========

A simple peer to peer, serverless FS based on classic xFS concept.

# Authors:
* Abhijeet Gaikwad (Student id: 4934921)
* Prashant Chaudhary (Student id: 4922579)

# How to Run:
Extract the bin tar(simpleXFS-1.0-SNAPSHOT-bin) provided in the deliverable. And start the following components:
* Server
java -cp ./peer-1.0-SNAPSHOT.jar:./server-1.0-SNAPSHOT.jar:./common-1.0-SNAPSHOT.jar edu.umn.sxfs.server.Server <current_ip> <current_port>
* Peer
java -cp ./peer-1.0-SNAPSHOT.jar:./server-1.0-SNAPSHOT.jar:./common-1.0-SNAPSHOT.jar edu.umn.sxfs.peer.client.PeerClient <current_ip> <current_port> conf/simpleXFSflute.properties
    Properties file contains:
    server_ip: Tracking server ip.
    server_port: Tracking server port.
    load_threshold: A hint for maximum number of requests (both upload and download) that can be served by the peer. Used in the peer selection algorithm. More requests may be served than this value.
    peerAlgorithm: The algorithm that is used for peer selection. The value currently should be 'loadLatencyAlgorithm'.
    fileStoreDir: Name of the directory where all the files are stored.
    peerLatencyfile: The configuration file containing peer latencies. Format: ip_port,ip_port,latency.
    byzantineMode: true or false, executes byzantine failure accordingly.

# Building:
This project was built using maven, hopefully you will not have to build as we developed
in Java and providing already compiled Jars. In case you will have to, these steps will
help on any of the cs machines in the lab:
  1. module load java/maven
  2. Extract the source tar(simpleXFS-1.0-SNAPSHOT-src), change directory to extracted dir, let this dir be PROJ_HOME
  4. run (from PROJ_HOME): mvn clean install
  5. Build is created in this dir: PROJ_HOME/dist/target/simpleXFS-1.0-SNAPSHOT/ or
     PROJ_HOME/dist/target/bulletinboard-1.0-SNAPSHOT.tar.gz

# Peer client usage:
a. Find(filename): finds peers on which the file is present. Internally used, provided interface through client to test the functionality. Usage: find <filename>
   Returns list of servers that contain this file. Empty set if file not present anywhere.
b. updateList(peerInfo, set<files>): updates the file list at the server for the peer. Internally used, provided interface through client to test the functionality. Usage: updatelist [peer_host] [peer_port]. This is also called periodically.
   Does not return anything. Please check server log to verify if the list was updated.
c. download(filename): downloads an authentic (checks checksum) file. Internally emulates latency, updates file list on server. Usage: download [peer_ip] [peer_host] filename. (peer_ip and peer_host are optional and can be used for testing purposes)
   Returns the file path where the file was downloaded.
d. GetLoad(server): returns an integer, the number threads running on the server. Internally used, provided interface through client to test the functionality. Usage: getload peer_host peer_port.
e. getchecksum(filename): return checksum for the specified file. Internally used, provided interface through client to test the functionality. Usage: getchecksum filename.
