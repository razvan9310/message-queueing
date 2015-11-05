import collections
from sys import argv
from sys import stdin

delay = int(argv[1])
values = {}

while True:
	line = stdin.readline()
	if not line:
		break
	line = line.split(' ')
	n1, n2 = int(line[0]), int(line[1])
	delta = 1.0 * n2 / 10**6
	sn1 = n1 / 10**6
	sn1 /= delay
	if sn1 != int(sn1):
		sn1 = int(sn1) + 1
	else:
		sn1 = int(sn1)
	# if sn1 < 300 or sn1 > 1500:
	# 	continue
	if values.get(sn1) is not None:	
		values[sn1].append(delta)
	else:
		values[sn1] = [delta]

sorted_values = collections.OrderedDict(sorted(values.items()))
for key in sorted_values:
	avg = sum(sorted_values[key]) / len(sorted_values[key])
	print str(key) + " " + str(avg)
