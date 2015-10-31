import collections
from sys import stdin

values = {}

while True:
	line = stdin.readline()
	if not line:
		break
	line = line.split(' ')
	n1, n2 = int(line[0]), int(line[1])
	sn1 = n1 / 10**9
	if values.get(sn1) is not None:	
		values[sn1] = values[sn1] + n2
	else:
		values[sn1] = n2

sorted_values = collections.OrderedDict(sorted(values.items()))
for key in sorted_values:
	print str(key) + " " + str(sorted_values[key])
