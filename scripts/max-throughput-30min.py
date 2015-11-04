import collections
from sys import stdin

values = {}

while True:
	line = stdin.readline()
	if not line:
		break
	n = int(line)
	sn = n / 10**9
	if sn < 300 or sn > 1500:
		continue
	if values.get(sn) is not None:
		values[sn] = 1 + values[sn]
	else:
		values[sn] = 1

sorted_values = collections.OrderedDict(sorted(values.items()))
list = []
for key in sorted_values:
	list.append(sorted_values[key])

print "Max throughput: " + str(max(list))

