# CS 1674: Assignment 9

## Here, I will describe how the workflow for Bayesian Optimization goes.

## Prior to Bayesian Optimization, for parts (i) - (iii), the following files may be used:

### part_i.m
### part_ii.m
### part_iii.m

## Unsurprisingly, in running part_i, part_ii and part_iii, each time the respective neural networks are trained with the same learning rate, we will not always get the same result.

## To run Bayesian Optimization, I have created 6 additional Matlab files, which are the following:

### optimized_part_i.m
Along with this file, this file runs Bayesian optimization for an arbitrary number of Epochs, and the
optimizable parameters, learning rate and L2 regularization parameters can be changed. In the optimVars
list, a feasible range is provided, and the function: makeObjFn will try different combinations falling 
within respective ranges. It is possible to add Momentum as another parameter to tune. 

Additionally, I have divided the images into a Validation, Training, and Test set. I have also
performed L2Regularization and Data Augmentation to reduce overfitting on the training set.

Each time an iteration of Bayesian Optimization completes, a file with the Validation Error
is written like: [curr_val_error].m. In this file, it will have the accuracy, the trainAccuracy, the Epochs and the validation error. In the optimized_part_i.m file, I find the file with the lowest validation error and
retrieve the accuracy.


Similarly, I have optimized_part_ii.m and optimized_part_iii.m with Bayesian Optimization functions: makeObjFnc2.m and makeObjFnc3.m that perform Bayesian Optimization but for the neural network architectures
from part_ii and part_iii.

Overall, I really enjoyed this assignment, and seeing how the hyperparameters can boost accuracy rather
than having to modify the architecture was really interesting! Thank you for writing such a nice assignment!

## The results with Bayesian Optimization can be found in the optimized-results.txt file.