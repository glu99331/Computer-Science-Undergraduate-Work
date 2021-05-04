% file = fullfile('scenes_lazebnik');
% imds = imageDatastore(file, 'IncludeSubfolders',true, 'LabelSource', 'foldernames');
imds = imageDatastore('C:\Users\gordo\Desktop\hw9\scenes_lazebnik', ...
    'IncludeSubfolders',true, ...
    'LabelSource','foldernames');
numTraining = 100;
[imdsTrain, imdsTest] = splitEachLabel(imds, numTraining, 'randomize');

net = alexnet;
net.Layers;
freezed_layers=net.Layers(1:16);
layers=[
    freezed_layers;
    fullyConnectedLayer(8);
    softmaxLayer;
    classificationLayer];

options = trainingOptions('sgdm', ...
    'MaxEpochs',1,...
    'InitialLearnRate',1e-3, ...
    'ExecutionEnvironment','auto', ...
    'LearnRateSchedule', 'piecewise', ...
    'Plots','training-progress');
net = trainNetwork(imdsTrain, layers, options);
pred = classify(net, imdsTest);
accuracy = sum(pred == imdsTest.Labels)/numel(imdsTest.Labels);