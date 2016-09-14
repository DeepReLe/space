import subprocess
import csv

def space():
	subprocess.check_call(["sbt","run"])
	file = open("output/filename.txt", 'r')
	CSVfileName = file.read()
	with open(CSVfileName) as csvfile:
		reader = csv.DictReader(csvfile)

	return reader

CSVObject = space()




