import numpy as np
import matplotlib.pyplot as plt
from forward import forward, activation_func, activation_func_derivative
from backprop import backprop

def normalize_data(fname):
    denormalized_data = np.genfromtxt(fname, delimiter=';', dtype=np.float64)[1:]
    np.random.shuffle(denormalized_data)
    (rows, cols) = np.shape(denormalized_data)
    #clean up validation set
    test_data = denormalized_data[len(denormalized_data)//2:]
    test_set = test_data[:,:test_data.shape[1]-1]
    test_groundtruth = test_data[:,-1]
    #clean up training set
    train_data = denormalized_data[:len(denormalized_data)//2]
    train_set = train_data[:,:train_data.shape[1]-1]
    train_groundtruth = train_data[:,-1]
    #compute mu and sigma
    test_mean = np.mean(test_set, axis = 0)
    test_std = np.std(test_set, axis = 0)
    train_mean = np.mean(train_set, axis = 0)
    train_std = np.std(train_set, axis = 0)
    #anddd standardize them
    standardized_test_set = (test_set - test_mean)/test_std
    standardized_train_set = (train_set - train_mean)/train_std
    #add in bias by inserting column of 1s
    train_with_bias = np.insert(standardized_train_set, cols-1, 1, axis=1)
    test_with_bias = np.insert(standardized_test_set, cols-1, 1, axis=1)
    return (train_with_bias, train_groundtruth, test_with_bias, test_groundtruth)
if __name__ == "__main__":
    curr_min = 1000000
    min_eta = 0
    iters = 0
    (train_with_bias, train_groundtruth, test_with_bias, test_groundtruth) = normalize_data('winequality-red.csv')
    (W1, W2, error_over_time) = backprop(train_with_bias, train_groundtruth, 30, 1000, 0.0064)
    (y_pred, Z) = forward(test_with_bias, W1, W2)
    error = np.sqrt(((y_pred - test_groundtruth)**2).mean())
    
    print("RMSE on Test Set = {}".format(error))
    plt.plot(error_over_time)
    plt.show()
    plt.close()
    # for i in range(10):
    #     # eta = (0.3)/(10*(i+1))
    #     (train_with_bias, train_groundtruth, test_with_bias, test_groundtruth) = normalize_data('winequality-red.csv')
    #     (W1, W2, error_over_time) = backprop(train_with_bias, train_groundtruth, 30, 1000, 0.0075)
    #     (y_pred, Z) = forward(test_with_bias, W1, W2)
    #     error = np.sqrt(((y_pred - test_groundtruth)**2).mean())
    #     # if(error < curr_min):
    #     #     curr_min = error
    #     #     min_eta = eta
    #     #     iters = i
    #     print("RMSE on Test Set = {}".format(error))

    # print("RMS Error on Validation Set = {}".format(curr_min))
    # print("Eta = {}".format(min_eta))
    # print("Iters = {}".format(iters))

