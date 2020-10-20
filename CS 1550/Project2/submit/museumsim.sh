#! /bin/sh
scp gol6@thoth.cs.pitt.edu:/afs/pitt.edu/home/g/o/gol6/museumsim .
./museumsim -m 50 -k 5 -pv 100 -dv 1 -sv 10 -pg 0 -dg 3 -sg 20