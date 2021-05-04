function [predicted_labels_test] = findLabelsSVM(pyramids_train, labels_train, pyramids_test)
model = fitcecoc(pyramids_train, labels_train);
predicted_labels_test = predict(model, pyramids_test);