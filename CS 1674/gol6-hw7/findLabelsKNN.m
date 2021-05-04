function [predicted_labels_test] = findLabelsKNN(pyramids_train, labels_train, pyramids_test, k)
predicted_labels_test = zeros(1,size(pyramids_test,1));
for i = 1:size(pyramids_test,1)
    dist = pdist2(pyramids_test(i, :), pyramids_train, 'euclidean');
    [sortedVals, indices] = sort(dist);
    topIndices = indices(1:k);
    copyLabels = labels_train(topIndices); 
    m = mode(copyLabels);
    predicted_labels_test(1,i) = m; 
end

end