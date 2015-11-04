import sys

nobs = len(sys.argv) - 1
if nobs == 0:
	print "Usage: mean-response-time.py rt1.dat rt2.dat ... rtn.dat"
	sys.exit(1)
means = [0.0 for i in range(nobs)]

for i in range(nobs):
	sum = 0.0
	count = 0
	with open(sys.argv[i + 1]) as f:
		while True:
			line = f.readline()
			if not line:
				break
			line = line.split(' ')
			s = int(line[0])
			if s < 60 or s > 360:
				continue
			n = float(line[1])
			sum += n
			count += 1
	means[i] = sum / count

sum = 0.0
count = 0
for i in range(len(means)):
	sum += means[i]
	count += 1
mean = sum / count

for mean in means:
	print str(mean) + " "
			
