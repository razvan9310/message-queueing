import collections
from sys import stdin

values = {}

while True:
	line = stdin.readline()
	if not line:
		break
	n = int(line)
	sn = n / 10**9
	if values.get(sn) is not None:
		values[sn] = 1 + values[sn]
	else:
		values[sn] = 1

sorted_values = collections.OrderedDict(sorted(values.items()))
for key in sorted_values:
	print str(key) + " " + str(sorted_values[key])

