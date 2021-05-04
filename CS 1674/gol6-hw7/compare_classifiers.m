% Apply the SVM and KNN classifiers (i.e. call findLabelsSVM, findLabelsKNN) to predict labels on the test set, 
% using the pyramid variable as the representation for each image. For KNN, use the following values of k=1:2:21. 
% Each value of k gives a different KNN classifier.

trainSVM = findLabelsSVM(train_pyramid, train_labels, train_pyramid);
trainSVM_accuracy = sum(trainSVM == train_labels)/size(train_labels,1);
testSVM = findLabelsSVM(train_pyramid, train_labels, test_pyramid);
testSVM_accuracy = sum(testSVM == test_labels)/size(test_labels,1);

trainKNN = zeros(train_id, 11);
testKNN = zeros(test_id, 11);

ptr = 1;

for k = 1:2:21
    trainKNN(:, ptr) = findLabelsKNN(train_pyramid, train_labels, train_pyramid, k);
    testKNN(:, ptr) = findLabelsKNN(train_pyramid, train_labels, test_pyramid, k);
    ptr = ptr + 1;
end
% Compute the accuracy of each classifier on (1) the training set, and (2) the test set, 
% by comparing its predictions with the "ground truth" labels.
trainKNN_accuracy = sum(trainKNN == train_labels)/size(train_labels,1);
testKNN_accuracy = sum(testKNN == test_labels)/size(test_labels,1);

% Plot the training and test accuracy of both types of classifiers, using the values of k on the x-axis, 
% and accuracy on the y-axis. Since SVM does not depend on the value of k, plot its performance as a straight line.
% Save the result as results.png and submit it. Label your axes and show a legend. 
% Useful functions: plot, xlabel, ylabel, legend.
figure;
hold on;

plot(1:2:21, ones(11,1)*trainSVM_accuracy, '-o');
plot(1:2:21, ones(11,1)*testSVM_accuracy, '-x');
plot(1:2:21, trainKNN_accuracy, '-o');
plot(1:2:21, testKNN_accuracy, '-x');

xlabel('K values');
ylabel('Accuracy');

legend('SVM Training Accuracy', 'SVM Test Accuracy', 'KNN Training Accuracy', 'KNN Test Accuracy');
hold off;    
saveas(gcf,'results.png')