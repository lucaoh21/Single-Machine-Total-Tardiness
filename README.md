# Single-Machine-Total-Tardiness
**Tom Lucy, Jake Rourke, Luca Ostertag-Hill**

**Nature Inspired Computation: Final Project**

**December 15, 2018**

The SMTWTP_HYBRID.java file is the main class for the hybrid. To run the either GA, EAS, or the hybrid:

1. Type `javac SMTWTP_HYBRID.java` to create the classes.
2. The alogirhtm is run through the command `java numIterations maxGenerations filename algorithm`
    1. Parameter `numIterations` represents the number of iterations to run the EAS algorithm (real number)
    2. Parameter `maxGenerations` represents the maximum number of generations to run the GA algorithm (real number)
    3. Parameter `filename` is the path to the testing file
    4. Possible parameters for `algorithm` are `ga`, `eas`, and `both`
3. Example for hybrid algorithm with 150 EAS iterationa and 150 GA generations: `java 150 150 100.3.txt both`

`
