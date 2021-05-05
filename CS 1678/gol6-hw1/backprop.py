import numpy as np
import matplotlib.pyplot as plt
from forward import forward, activation_func, activation_func_derivative

# Second, write a function backprop that takes inputs X, y, M, iters, eta and outputs W1, W2, error_over_time. This function performs training using backpropagation (and calls the forward function as it iterates). Construct the network in this function, i.e. create the weight matrices and initialize the weights to small random numbers, then iterate: pick a training sample, compute the error at the output, then backpropagate to the hidden layer, and update the weights with the resulting error.

# Inputs:
# an NxD matrix X of features, where N is the number of samples and D is the number of feature dimensions,
# an Nx1 vector y containing the ground-truth labels for the N samples,
# a scalar M containing the number of hidden neurons to use,
# a scalar iters defining how many iterations to run (one sample used in each), and
# a scalar eta defining the learning rate to use.
# Outputs:
# [7 pts] W1 and W2, defined as above for forward, and
# [1 pts] an itersx1 vector error_over_time that contains the error on the sample used in each iteration.
def backprop(X, y, M, iters, eta):
    error_over_time = np.zeros((iters, 1))
    (N, D) = np.shape(X)
    W1 = np.random.normal(0.0, 0.001, (M, D))
    W2 = np.random.normal(0.0, 0.001, (1, M))
    for i in range(iters):
        k = np.random.randint(0, N)
        (y_pred, Z) = forward(X, W1, W2)
        delta_k = y_pred[k] - y[k]
        delta_j = activation_func_derivative(Z[k]) * W2 * delta_k
        #	wkj = wkj – η dE/dwkj (output layer)
        w2_delta = Z[k] * delta_k * eta
        w1_delta = np.broadcast_to(X[k], (M, D)) * np.broadcast_to(delta_j.T, (M, D)) * eta
        W1 -= w1_delta
        W2 -= w2_delta    
        error_over_time[i] = np.sqrt(((y_pred - y)**2).mean())
    return (W1, W2, error_over_time)