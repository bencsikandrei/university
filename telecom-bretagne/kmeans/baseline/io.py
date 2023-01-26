"""

"""

import random
import sys
import os

def read_data(filename, skip_first_line = False, ignore_first_column = False):
    '''
    Loads data from a csv file and returns the corresponding list.
    All data are expected to be floats, except in the first column.
    
    @param filename: csv file name.
    
    @param skip_first_line: if True, the first line is not read.
        Default value: False.
    
    @param ignore_first_column: if True, the first column is ignored.
        Default value: False.
    
    @return: a list of lists, each list being a row in the data file.
        Rows are returned in the same order as in the file.
        They contains floats, except for the 1st element which is a string
        when the first column is not ignored.
    '''
    # initialize to NULL
    data = None
    # check if the file exists 
    if not os.path.isfile(filename):
    	print "Error! No such file!\n"
    	exit()
    else:
		with open(filename,'r') as f:
			if skip_first_line:
				f.readline()
			# initialize to empty list
			data = []
			labels = set()
			# go through the file
			for line in f:
				# split at comma (CSV) file
				line = line.split(",")
				# add the labels
				label = str(line[-1]).rstrip()
				if label != '' and label:
					labels.add(label)
				# strip the newline
				line = [ x.rstrip() for x in line ]
				# take all the elems from the file and convert to float
				line = [ float(x) for x in line[:-1] ]
				# labels.add(line[-1])
				# if we have chosen to ignore the first column
				if ignore_first_column:
				    line = line[1:]
				# add to the list
				if len(line) > 0:
					data.append(line)
    return data, labels
###############################################################################
"""
"""
def write_data(data, filename):
    '''
    Writes data in a csv file.

    @param data: a list of lists

    @param filename: the path of the file in which data is written.
        The file is created if necessary; if it exists, it is overwritten.
    '''
    # If you're curious, look at python's module csv instead, which offers
    # more powerful means to write (and read!) csv files.
    f = open(filename, 'w')
    for item in data:
        f.write(','.join([repr(x) for x in item]))
        f.write('\n')
    f.close()
###############################################################################
"""
"""
def generate_random_data(nb_objs, nb_attrs, frand = random.random):
    '''
    Generates a matrix of random data.
    
    @param frand: the fonction used to generate random values.
        It defaults to random.random.
        Example::

            import random
            generate_random_data(5, 6, lambda: random.gauss(0, 1))
    
    @return: a matrix with nb_objs rows and nb_attrs+1 columns. The 1st
        column is filled with line numbers (integers, from 1 to nb_objs).
    '''
    data = []
    for i in range(nb_objs):
        #line = [i+1]
        #for j in range(numAtt):
        #    line.append(frand())
        line = [i+1] + map(lambda x: frand(), range(nb_attrs))
        data.append(line)
    return(data)
