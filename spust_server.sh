#!/bin/bash
rm zaznam.txt
rm vysledky.txt
for i in { 1..4 }
do
	cd $1
	make clean_logs
	cd ..
	cd $2
	make clean_logs
	cd ..
	./redbot $1/Jenny_run $2/Jenny_run >> zaznam.txt
	tail -n zaznam.txt >> vysledky.txt
done
