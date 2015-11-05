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
list1 = [] # 300s - 599s
list2 = [] # 600s - 899s
list3 = [] # 900s - 1199s
list4 = [] # 1200s - 1500s
for key in sorted_values:
	avg = sum(sorted_values[key]) / len(sorted_values[key])
	if key < 600:
		list1.append(avg)
	elif key < 900:
		list2.append(avg)
	elif key < 1200:
		list3.append(avg)
	else:
		list4.append(avg)

print "5-10 min | Mean = " + str(np.mean(list1)) + " | Stdev = " + str(np.std(list1))
print "10-15 min | Mean = " + str(np.mean(list2)) + " | Stdev = " + str(np.std(list2))
print "15-20 min | Mean = " + str(np.mean(list3)) + " | Stdev = " + str(np.std(list3))
print "20-25 min | Mean = " + str(np.mean(list4)) + " | Stdev = " + str(np.std(list4))
print "Overall | Mean = " + str(np.mean(list1 + list2 + list3 + list4)) + " | Stdev = " + str(np.std(list1 + list2 + list3 + list4))
