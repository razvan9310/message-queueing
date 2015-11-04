from sys import stdin

SIGNS = [[1, -1, -1, -1, 1, 1, 1, -1],
	 [1, -1, -1, 1, 1, -1, -1, 1],
	 [1, -1, 1, -1, -1, 1, -1, 1],
	 [1, -1, 1, 1, -1, -1, 1, -1],
	 [1, 1, -1, -1, -1, -1, 1, 1],
	 [1, 1, -1, 1, -1, 1, -1, -1],
	 [1, 1, 1, -1, 1, -1, -1, -1],
	 [1, 1, 1, 1, 1, 1, 1, 1]]

# List of response times; should be 8x5
response_times = []
means = []
while True:
	line = stdin.readline()
	if not line:
		break
	# Each line should be: RT1 RT2 RT3 RT4 RT5
	line = line.split(' ')
	line_ints = [int(n) for n in line]
	response_times.append(line_ints)
	means.append(sum(line_ints) / len(line_ints))

qs = []
for i in range(8):
	sum = 0.0
	for j in range(8):
		sum += SIGNS[j][i] * means[j]
	qs.append(sum / 8)

ybars = []
SSE = 0.0
for i in range(8):
	sum = 0.0
	for j in range(8):
		sum += SIGNS[i][j] * qs[j]
	ybars.append(sum)
	for j in range(5):
		err = response_times[i][j] - ybars[i]
		SSE += err ** 2

SSA = 40 * qs[1] ** 2
SSB = 40 * qs[2] ** 2
SSC = 40 * qs[3] ** 2
SSAB = 40 * qs[4] ** 2
SSAC = 40 * qs[5] ** 2
SSBC = 40 * qs[6] ** 2
SSABC = 40 * qs[7] ** 2

SST = SSA + SSB + SSC + SSAB + SSAC + SSBC + SSABC + SSE

varA = SSA / SST
varB = SSB / SST
varC = SSC / SST
varAB = SSAB / SST
varAC = SSAC / SST
varBC = SSBC / SST
varABC = SSABC / SST

print "Variation A: " + str(varA * 100) + "%"
print "Variation B: " + str(varB * 100) + "%"
print "Variation C: " + str(varC * 100) + "%"
print "Variation AB: " + str(varAB * 100) + "%"
print "Variation AC: " + str(varAC * 100) + "%"
print "Variation BC: " + str(varBC * 100) + "%"
print "Variation ABC: " + str(varABC * 100) + "%"
	
