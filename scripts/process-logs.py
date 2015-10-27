import collections
from sys import stdin

values = {}

while True:
	line = stdin.readline()
	if not line:
		break
	line = line.split(' ')
	n1, n2 = int(line[0]), int(line[1])
	delta = (n2 - n1) / 10**3
	sn1 = n1 / 10**9
	if values.get(sn1) is not None:	
		values[sn1].append(delta)
	else:
		values[sn1] = [delta]

sorted_values = collections.OrderedDict(sorted(values.items()))
for key in sorted_values:
	avg = sum(sorted_values[key]) / len(sorted_values[key])
	print str(key) + " " + str(avg)
