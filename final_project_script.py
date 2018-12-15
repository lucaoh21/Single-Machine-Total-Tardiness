import subprocess

# test all 3 possibilites: just eas, just ga, and both together
# combined number of iterations will be 500 for each
# test on two problems with 40 jobs and two with 50

# when just eas is run, we will run it 50 times and choose the best of 50 scores
# when just ga is run, we will give it 50 randomly created individuals and run it once
# when both are run, eas will run 50 times to create 50 individuals for ga to use

num_ants = "30"
max_iterations = "500"
alpha = "1"
beta = "3"
rho = "0.1"
elitism_factor = num_ants
populaiton_size = "50"
max_generations = "500"
crossover_prob = "0.6"
mutatio_prob = "0.4"
optimal = "913"
stop_percent = "0.2"
filename = ""
command = "both"

# eas for 500 on 40.1
max_iterations = "500"
optimal = "913"
filename = "40.1.txt"
command = "eas"
for i in range(10):
	subprocess.run(['java', 'SMTWTP_HYBRID', num_ants, max_iterations, alpha, beta, rho, elitism_factor, population_size, max_generations, crossover_prob, mutation_prob, optimal, stop_percent, filename, command])

# eas for 500 on 40.5
max_iterations = "500"
filename = "40.5.txt"
optimal = "990"
command = "eas"
for i in range(10):
	subprocess.run(['java', 'SMTWTP_HYBRID', num_ants, max_iterations, alpha, beta, rho, elitism_factor, population_size, max_generations, crossover_prob, mutation_prob, optimal, stop_percent, filename, command])

# eas for 500 on 50.1
max_iterations = "500"
filename = "50.1.txt"
optimal = "2134"
command = "eas"
for i in range(10):
	subprocess.run(['java', 'SMTWTP_HYBRID', num_ants, max_iterations, alpha, beta, rho, elitism_factor, population_size, max_generations, crossover_prob, mutation_prob, optimal, stop_percent, filename, command])

# eas for 500 on 50.5
max_iterations = "500"
filename = "50.5.txt"
optimal = "1518"
command = "eas"
for i in range(10):
	subprocess.run(['java', 'SMTWTP_HYBRID', num_ants, max_iterations, alpha, beta, rho, elitism_factor, population_size, max_generations, crossover_prob, mutation_prob, optimal, stop_percent, filename, command])

# ga for 500 on 40.1
max_generations = "500"
filename = "40.1.txt"
optimal = "913"
command = "ga"
for i in range(10):
	subprocess.run(['java', 'SMTWTP_HYBRID', num_ants, max_iterations, alpha, beta, rho, elitism_factor, population_size, max_generations, crossover_prob, mutation_prob, optimal, stop_percent, filename, command])

# ga for 500 on 40.5
max_generations = "500"
filename = "40.5.txt"
optimal = "990"
command = "ga"
for i in range(10):
	subprocess.run(['java', 'SMTWTP_HYBRID', num_ants, max_iterations, alpha, beta, rho, elitism_factor, population_size, max_generations, crossover_prob, mutation_prob, optimal, stop_percent, filename, command])

# ga for 500 on 50.1
max_generations = "500"
filename = "50.1.txt"
optimal = "2134"
command = "ga"
for i in range(10):
	subprocess.run(['java', 'SMTWTP_HYBRID', num_ants, max_iterations, alpha, beta, rho, elitism_factor, population_size, max_generations, crossover_prob, mutation_prob, optimal, stop_percent, filename, command])

# ga for 500 on 50.5
max_generations = "500"
filename = "50.5.txt"
optimal = "1518"
command = "ga"
for i in range(10):
	subprocess.run(['java', 'SMTWTP_HYBRID', num_ants, max_iterations, alpha, beta, rho, elitism_factor, population_size, max_generations, crossover_prob, mutation_prob, optimal, stop_percent, filename, command])

# both for 250 eas and 250 ga on 40.1
max_iterations = "250"
max_generations = "250"
filename = "40.1.txt"
optimal = "913"
command = "both"
for i in range(10):
	subprocess.run(['java', 'SMTWTP_HYBRID', num_ants, max_iterations, alpha, beta, rho, elitism_factor, population_size, max_generations, crossover_prob, mutation_prob, optimal, stop_percent, filename, command])

# both for 250 eas and 250 ga on 40.5
max_iterations = "250"
max_generations = "250"
filename = "40.5.txt"
optimal = "990"
command = "both"
for i in range(10):
	subprocess.run(['java', 'SMTWTP_HYBRID', num_ants, max_iterations, alpha, beta, rho, elitism_factor, population_size, max_generations, crossover_prob, mutation_prob, optimal, stop_percent, filename, command])

# both for 250 eas and 250 ga on 50.1
max_iterations = "250"
max_generations = "250"
filename = "50.1.txt"
optimal = "2134"
command = "both"
for i in range(10):
	subprocess.run(['java', 'SMTWTP_HYBRID', num_ants, max_iterations, alpha, beta, rho, elitism_factor, population_size, max_generations, crossover_prob, mutation_prob, optimal, stop_percent, filename, command])

# both for 250 eas and 250 ga on 50.5
max_iterations = "250"
max_generations = "250"
filename = "50.5.txt"
optimal = "1518"
command = "both"
for i in range(10):
	subprocess.run(['java', 'SMTWTP_HYBRID', num_ants, max_iterations, alpha, beta, rho, elitism_factor, population_size, max_generations, crossover_prob, mutation_prob, optimal, stop_percent, filename, command])




