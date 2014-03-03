#!/bin/bash
# command line arguments:
#	1. number of time steps
#	2. min mass of a body
#	3. max mass of a body
#	4. max start velocity component (x and y)
#get number of processors
#NPROC=$(grep -c ^processor /proc/cpuinfo)
#if (($NPROC>8))
#then NPROC=8
#fi
timesteps=$1
minmass=$2
maxmass=$3
maxvel=$4
NPROC=4
echo $NPROC
nrtries=5
printf "results\n----------------------------------------------------------\n" > result.txt
cd ./dist
#sequential execution
for size in 120 180 240
do
	printf " --------------Size: %02i--------------\n" $size >> result.txt
	printf "Sequential execution: \n" >> result.txt
	for (( i=1; i<=nrtries; i++ ))
	do
		java -jar NBodyBruteForce.jar $size $timesteps 1 $minmass $maxmass $maxvel | grep seconds >>result.txt
	done			
done		
#for all possible number of processors
for (( processors=2; processors<=NPROC; processors++ ))
do
	printf " =============Processors: %02i==============\n" $processors >> result.txt
	#for different number of bodies
	for size in 120 180 240
	do
		printf " --------------Size: %02i--------------\n" $size >> result.txt
		#try many times
		printf "Parallel execution: \n" >> result.txt
		for (( i=1; i<=nrtries; i++ ))
		do
			 java -jar NBodyBruteForce.jar $size $timesteps $NPROC $minmass $maxmass $maxvel | grep seconds >>result.txt
		done
	done
done



