WikiContributorsListing
=======================

Uses a web crawler to fetch contributors info for a set of Wiki pages. MapReduce is run on this info to produce a listing of contributors and all the pages(within the set of pages crawled) that they have contributed to.


Build Instructions
------------------
1. First, compile and run the web crawler as a normal Java program to download contributors info into a folder called CrawledContent.

2. To run MapReduce job:

Pre-requisites: Hadoop environment should be set up and all daemons running. This code has been tested on Hadoop v1.2.1 in both LocalJobRunner mode and pseudo-distributed mode.

Command to compile MapReduce job:

	cd <root_folder_of_WikiMapReduce_proj>

	javac -verbose -classpath /usr/local/hadoop/hadoop-core-1.2.1.jar:./lib/jsoup-1.7.2.jar ./src/com/wiki/contributors/mr/*.java -d ./bin/

	jar -cvf ./wiki-mr.jar -C ./bin/ .

To run the MapReduce job:
First, copy the input files to HDFS:

	hadoop fs -put <path_to_Spiderman_proj>/CrawledContent hdfs://localhost/app/input/

Run the MapReduce job

	hadoop jar ./wiki-mr.jar DriverWithToolRunner hdfs://localhost/app/input/ /output

Sample output file is present in the SampleOutput folder.
