import subprocess
import csv

subprocess.check_call(["sbt","run"])

file = open("output/filename.txt", 'r')

CSVfileName = file.read()

with open(CSVfileName) as csvfile:
	reader = csv.DictReader(csvfile)

print reader


