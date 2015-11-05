import collections
import numpy as np
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
list1 = [] # 300s - 599s
list2 = [] # 600s - 899s
list3 = [] # 900s - 1199s
list4 = [] # 1200s - 1500s
for key in sorted_values:
	if key < 600:
		list1.append(sorted_values[key])
	elif key < 900:
		list2.append(sorted_values[key])
	elif key < 1200:
		list3.append(sorted_values[key])
	else:
		list4.append(sorted_values[key])

print "5-10 min | Mean = " + str(np.mean(list1)) + " | Stdev = " + str(np.std(list1))
print "10-15 min | Mean = " + str(np.mean(list2)) + " | Stdev = " + str(np.std(list2))
print "15-20 min | Mean = " + str(np.mean(list3)) + " | Stdev = " + str(np.std(list3))
print "20-25 min | Mean = " + str(np.mean(list4)) + " | Stdev = " + str(np.std(list4))
print "Overall | Mean = " + str(np.mean(list1 + list2 + list3 + list4)) + " | Stdev = " + str(np.std(list1 + list2 + list3 + list4))

