% file = fullfile('scenes_lazebnik');
% imds = imageDatastore(file, 'IncludeSubfolders',true, 'LabelSource', 'foldernames');
imds = imageDatastore('C:\Users\gordo\Desktop\hw9\scenes_lazebnik', ...
    'IncludeSubfolders',true, ...
    'LabelSource','foldernames');
numImages = 100;
%try 60,20,20 split
[imdsTrain,imdsValidation, imdsTest] = splitEachLabel(imds,0.60,0.20,'randomize');
%Prepare to use Bayesian Optimization:
XTrain = imdsTrain; 
YTrain = imdsTrain.Labels;
XTest = imdsTest;
YTest = imdsTest.Labels;
XValidation = imdsValidation;
YValidation = imdsValidation.Labels;

% Choose parameters to optimize
% Hyperparameters for SGDM:
optimVars = [
    optimizableVariable('InitialLearnRate',[1e-6 1e-5],'Transform','log')
%     optimizableVariable('Momentum',[0.8 0.98])
    optimizableVariable('L2Regularization',[1e-10 1e-2],'Transform','log')
%     optimizableVariable('NumEpochs', [1,50], 'Type', 'integer')
];

% Perform Bayesian Optimization
% Create the objective function for the Bayesian optimizer, using the training and validation data as inputs. 
% The objective function trains a convolutional neural network and returns the classification error on the validation set. This function is defined at the end of this script. Because bayesopt uses the error rate on the validation set to choose the best model, it is possible that the final network overfits on the validation set. The final chosen model is then tested on the independent test set to estimate the generalization error.
% 
ObjFcn = makeObjFcn2(XTrain,YTrain,XValidation,YValidation);
BayesObject = bayesopt(ObjFcn,optimVars, ...
    'MaxTime',14*60*60, ...
    'IsObjectiveDeterministic',false, ...
    'UseParallel',false);

% Evaluate Final Network
% Load the best network found in the optimization and its validation accuracy.
% 
bestIdx = BayesObject.IndexOfMinimumTrace(end);
fileName = BayesObject.UserDataTrace{bestIdx};
savedStruct = load(fileName);
valError = savedStruct.valError;

% Predict the labels of the test set and calculate the test error. 
% Treat the classification of each image in the test set as independent events with a certain probability of success, 
% which means that the number of incorrectly classified images follows a binomial distribution. 
% Use this to calculate the standard error (testErrorSE) and an approximate 95% confidence interval (testError95CI) of the generalization error rate. 
% This method is often called the Wald method. bayesopt determines the best network using the validation 
% set without exposing the network to the test set. 
% 
% It is then possible that the test error is higher than the validation error.
[YPredicted,probs] = classify(savedStruct.trainedNet,XTest);
testError = 1 - mean(YPredicted == YTest);
accuracy = sum(YPredicted == YTest)/numel(YTest);
NTest = numel(YTest);
testErrorSE = sqrt(testError*(1-testError)/NTest);
disp(testErrorSE);
disp(accuracy);
% testError95CI = [testError - 1.96*testErrorSE, testError + 1.96*testErrorSE];
