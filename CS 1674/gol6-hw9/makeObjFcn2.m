function ObjFcn = makeObjFcn2(XTrain,YTrain,XValidation,YValidation)
    ObjFcn = @valErrorFun;
      function [valError,cons,fileName] = valErrorFun(optVars)
          %image size is: 227x227
            imgSize = [227 227 3];
            net = alexnet;
            % net.Layers
            layersTransfer = net.Layers(1:16);
            layers = [
                layersTransfer
                fullyConnectedLayer(8)
                softmaxLayer
                classificationLayer];
        validationFrequency = floor(numel(YTrain)/5);
        % Use L2 regularization to prevent overfitting.
        % The L2 penalty aims to minimize the squared magnitude of the weights.
        
        % Dropout is a regularization technique that prevents neural networks from overfitting.
        options = trainingOptions('sgdm', ...
            'InitialLearnRate',optVars.InitialLearnRate, ...
            'ExecutionEnvironment','auto', ...
            'MaxEpochs',100, ...
            'LearnRateSchedule', 'piecewise', ...
            'LearnRateDropPeriod',40, ...
            'LearnRateDropFactor',0.1, ...
            'L2Regularization',optVars.L2Regularization, ...
            'Shuffle','every-epoch', ...
            'Verbose',false, ...
            'MiniBatchSize',5, ...
            'ValidationData',{XValidation,YValidation}, ...
            'ValidationFrequency',validationFrequency);
        pixelRange = [-4 4];
        imageAugmenter = imageDataAugmenter( ...
            'RandRotation', [-20, 20], ...
            'RandXReflection',true, ...
            'RandXTranslation',pixelRange, ...
            'RandYTranslation',pixelRange);
        %Use data augmentation to randomly flip the training images along the vertical axis, 
        %and randomly translate them up to four pixels horizontally and vertically. 
        %Data augmentation helps prevent the network from overfitting and memorizing the exact 
        %details of the training images.
        datasource = augmentedImageSource(imgSize,XTrain,'DataAugmentation',imageAugmenter);
        trainedNet = trainNetwork(datasource,layers,options);
        YPredicted = classify(trainedNet,XValidation);
        YPredicted2 = classify(trainedNet,XTrain);
    
        valError = 1 - mean(YPredicted == YValidation);
        accuracy = sum(YPredicted == YValidation)/numel(YValidation);
        trainAccuracy = sum(YPredicted2 == YTrain)/numel(YTrain);
        fileName = num2str(valError) + ".mat";
        numEpochs = 100;
        save(fileName,'trainedNet','valError','options', 'accuracy', 'trainAccuracy', 'numEpochs');
        cons = [];
      end
end


        