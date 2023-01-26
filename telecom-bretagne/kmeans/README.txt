# Alvarez Paulina Bencsik Andrei
"TP 413 - Etude et implémentation de l'algorithme des k-means" (CC)

Programme:
	K-means clustering algorithm

Authors:
	ALVAREZ Paulina Alejandra
	BENCSIK Andrei-Florin

Date:
	21/03/2016

Manual:
	Command format:		python Alvarez_Bencsik_INF413_2015_2016_P.py  k/"randomk"  "inputdatafile"/randomdata  "outputfile"[optional] 
	k/"randomk":	The number of centers (k). User can provide an actual number or have a random generated number between 2 and 10 (excluding last) by "randomNumber" (10 is the default maximum number k of groups that shall be created)

	inputdatafile/"randomdata":	The name of the input data file. By typing "randomdata", a random data file will be generated. If the user input an already existing file, he will have the option to choose if he wants to ignore first column, ignore last column and/or skip first line.

	"outputfile"[optional]:	The name of the output data file. This field is optional. If this field is not filled, a new file will be created with the name 'output' (.csv format).

	Note: a file named 'centers.csv' (.csv format) will also be created, providing the centroids of each group.

Examples:
	Alvarez_Bencsik_INF413_2015_2016_P.py 3 input.csv result.csv
	Alvarez_Bencsik_INF413_2015_2016_P.py 3 randomdata
	Alvarez_Bencsik_INF413_2015_2016_P.py randomk input_small.csv a.csv
	Alvarez_Bencsik_INF413_2015_2016_P

Print:
	Centroid

Chosen stop (convergence) condition:
	After a certain number of iterations MAX_ITER or when none of the points change clusters
