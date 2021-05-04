% file = fullfile('scenes_lazebnik');
% imds = imageDatastore(file, 'IncludeSubfolders',true, 'LabelSource', 'foldernames');
imds = imageDatastore('C:\Users\gordo\Desktop\hw9\scenes_lazebnik', ...
    'IncludeSubfolders',true, ...
    'LabelSource','foldernames');
numTraining = 100;
[imdsTrain,imdsTest] = splitEachLabel(imds,numTraining,'randomize');
layers = [ ...
    imageInputLayer([227 227 3])
    convolution2dLayer(11,50)
    reluLayer
    maxPooling2dLayer(3,'Stride',1)
    convolution2dLayer(5,60)
    reluLayer
    maxPooling2dLayer(3,'Stride',2)
    fullyConnectedLayer(8)
    softmaxLayer
    classificationLayer];

options = trainingOptions('sgdm', ...
    'MaxEpochs',1,...
    'InitialLearnRate',1e-3, ...
    'ExecutionEnvironment','auto', ...
    'LearnRateSchedule', 'piecewise', ...
    'MiniBatchSize',5, ...
    'Plots','training-progress');

net = trainNetwork(imdsTrain,layers,options);
pred = classify(net, imdsTest);
accuracy = sum(pred == imdsTest.Labels)/numel(imdsTest.Labels);