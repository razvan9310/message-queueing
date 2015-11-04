import collections
import numpy as np
from sys import stdin

values = {}

while True:
	line = stdin.readline()
	if not line:
		break
	line = line.split(' ')
	n1, n2 = int(line[0]), int(line[1])
	sn1 = n1 / 10**9
	if sn1 < 300 or sn1 > 1500:
		continue
	delta = 1.0 * n2 / 10**6
	if values.get(sn1) is not None:
		values[sn1].append(delta)
	else:
		values[sn1] = [delta]
	delta = 1.0 * n2 / 10**6

sorted_values = collections.OrderedDict(sorted(values.items()))
list = []
for key in sorted_values:
	avg = sum(sorted_values[key]) / len(sorted_values[key])
	list.append(avg)

print "Mean: " + str(np.mean(list))
print "Variance: " + str(np.var(list))	
