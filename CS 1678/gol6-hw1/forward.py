import numpy as np
import matplotlib.pyplot as plt

# First, write a function forward that takes inputs X, W1, W2 and outputs y_pred, Z. This function computes activations from the front towards the back of the network, using fixed input features and weights. You will also use the forward pass function to apply (run inference) and compute the loss for your network during/after training.

# Inputs:
# an NxD matrix X of features, where N is the number of samples and D is the number of feature dimensions,
# an MxD matrix W1 of weights between the first and second layer of the network, where M is the number of hidden neurons, and
# an 1xM matrix W2 of weights between the second and third layer of the network, where there is a single neuron at the output layer
# Outputs:
# [2 pts] an Nx1 vector y_pred containing the outputs at the last layer for all N samples, and
# [2 pts] an NxM matrix Z containing the activations for all M hidden neurons of all N samples.

def activation_func(a):
    return np.tanh(a)
def activation_func_derivative(act_func):
    return (1 - act_func**2)
def forward(X, W1, W2):
    a_mat = X @ W1.T #aj = sum(wji*xi)
    Z = activation_func(a_mat) #zj = h(aj)
    y_pred = Z @ W2.T #ak = sum(wkj*zj)
    return (y_pred, Z) #Z = NxM, y_pred = Nx1



