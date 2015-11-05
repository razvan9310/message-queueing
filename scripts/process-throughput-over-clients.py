import collections
from sys import argv
from sys import stdin

delay = int(argv[1])
values = {}

while True:
	line = stdin.readline()
	if not line:
		break
	n = int(line)
	sn = n / 10**6
	sn /= delay
	if sn != int(sn):
		sn = int(sn) + 1
	else:
		sn = int(sn)
	# if sn < 300 or sn > 1500:
	#	continue
	if values.get(sn) is not None:
		values[sn] = 1 + values[sn]
	else:
		values[sn] = 1

sorted_values = collections.OrderedDict(sorted(values.items()))
for key in sorted_values:
	print str(key) + " " + str(sorted_values[key])

