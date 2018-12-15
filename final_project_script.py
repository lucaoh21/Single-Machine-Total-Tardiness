import subprocess

# test all 3 possibilites: just eas, just ga, and both together
# combined number of iterations will be 300 for each
# test on two problems with 40 jobs and two with 50

# when just eas is run, we will run it 50 times and choose the best of 50 scores
# when just ga is run, we will give it 50 randomly created individuals and run it once
# when both are run, eas will run 50 times to create 50 individuals for ga to use

max_iterations = "0"
max_generations = "0"

command = "both"

# eas for 300 on 40.1
max_generations = "0"
max_iterations = "300"
filename = "40.1.txt"
command = "eas"
for i in range(10):
	subprocess.run(['java', 'SMTWTP_HYBRID', max_iterations, max_generations, filename, command])

# eas for 300 on 50.1
max_generations = "0"
max_iterations = "300"
filename = "50.1.txt"
command = "eas"
for i in range(10):
	subprocess.run(['java', 'SMTWTP_HYBRID', max_iterations, max_generations, filename, command])

# eas for 300 on 100.3
max_generations = "0"
max_iterations = "300"
filename = "100.3.txt"
command = "eas"
for i in range(10):
	subprocess.run(['java', 'SMTWTP_HYBRID', max_iterations, max_generations, filename, command])

# ga for 300 on 40.1
max_iterations = "0"
max_generations = "300"
filename = "40.1.txt"
command = "ga"
for i in range(10):
	subprocess.run(['java', 'SMTWTP_HYBRID', max_iterations, max_generations, filename, command])

# ga for 300 on 50.1
max_iterations = "0"
max_generations = "300"
filename = "50.1.txt"
command = "ga"
for i in range(10):
	subprocess.run(['java', 'SMTWTP_HYBRID', max_iterations, max_generations, filename, command])

# ga for 300 on 100.3
max_iterations = "0"
max_generations = "300"
filename = "100.3.txt"
command = "ga"
for i in range(10):
	subprocess.run(['java', 'SMTWTP_HYBRID', max_iterations, max_generations, filename, command])

# both for 150 eas and 150 ga on 40.1
max_iterations = "150"
max_generations = "150"
filename = "40.1.txt"
command = "both"
for i in range(10):
	subprocess.run(['java', 'SMTWTP_HYBRID', max_iterations, max_generations, filename, command])

# both for 150 eas and 150 ga on 50.1
max_iterations = "150"
max_generations = "150"
filename = "50.1.txt"
command = "both"
for i in range(10):
	subprocess.run(['java', 'SMTWTP_HYBRID', max_iterations, max_generations, filename, command])

# both for 150 eas and 150 ga on 100.3
max_iterations = "150"
max_generations = "150"
filename = "100.3.txt"
command = "both"
for i in range(10):
	subprocess.run(['java', 'SMTWTP_HYBRID', max_iterations, max_generations, filename, command])



