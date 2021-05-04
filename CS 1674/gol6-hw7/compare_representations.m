% Call your computeSPMRepr to compute the spatial pyramid match representation on top of the extracted 
% SIFT features, for all train/test images. Store the resulting representations (level-0, level-1, and 
% pyramid separately) in appropriate variables (with rows corresponding to number of samples, and 
% columns corresponding to feature dimensions).
train_pyramid = zeros(size(train_images,1), size(means,1)*5);
l0_train = zeros(size(train_images,1), size(means,1));
l1_train = zeros(size(train_images,1), size(means,1)*4);

for i = 1:train_id
    im_size = [train_images(i,1), train_images(i,2)];
    [train_pyramid(i,:), l0_train(i,:), l1_train(i,:)] = computeSPMRepr(im_size, train_sift{i}, means);
end

test_pyramid = zeros(size(test_images,1), size(means,1)*5);
l0_test = zeros(size(test_images,1), size(means,1));
l1_test = zeros(size(test_images,1), size(means,1)*4);
for i = 1:test_id
    im_size = [test_images(i,1), test_images(i,2)];
    [test_pyramid(i,:), l0_test(i,:), l1_test(i,:)] = computeSPMRepr(im_size, test_sift{i}, means);
end

% Use an SVM classifier. Compare the quality of three representations, pyramid, level_0 and level_1. 
% In other words, compare the full SPM representation to its constituent parts, which are the level-0 
% histogram and the concatenations of four histograms in level-1. Compute the accuracy at each level, 
% by measuring what fraction of the images was assigned the correct label. 
% 
% In a file results1.txt, describe your findings, and give your explanation of the performance of the 
% different representations.
pyramid_SVM = findLabelsSVM(train_pyramid, train_labels, test_pyramid);
l0_SVM = findLabelsSVM(l0_train, train_labels, l0_test);
l1_SVM = findLabelsSVM(l1_train, train_labels, l1_test);

correct_pyramid = sum(pyramid_SVM == test_labels);
correct_l0 = sum(l0_SVM == test_labels);
correct_l1 = sum(l1_SVM == test_labels);

accuracy_pyramid = correct_pyramid/size(test_labels,1);
accuracy_l0 = correct_l0/size(test_labels,1);
accuracy_l1 = correct_l1/size(test_labels,1);