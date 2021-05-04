% In this part, you will compute a bag-of-words histogram representation of an image. 
% Conceptually, the histogram for image Ij is a k-dimensional vector: 
% F(Ij) = [ freq1, j    freq2, j    ...    freqk, j ], where each entry
% freqi, j counts the number of occurrences of the i-th visual word in image j, and 
% k is the number of total words in the vocabulary. 
% (Acknowledgement: Notation from Kristen Grauman's assignment.)
function [bow_repr] = computeBOWRepr(features, means)
%[2 pt] A bag-of-words histogram has as many dimensions as the number of clusters k, 
%so initialize the bow variable accordingly.
bow_repr = zeros(1,size(means,1));

% [4 pts] Next, for each feature (i.e. each row in features), compute its distance to 
% each of the cluster means, and find the closest mean. A feature is thus conceptually 
% "mapped" to the closest cluster. You can do this efficiently using 
% Matlab's pdist2 function (with inputs features, means).
closest = inf(length(features),2);
for i = 1:size(features,1)
    for j = 1:size(means,1)
        dist = pdist2(features(i,:),means(j,:));
        update_closest = dist < closest(i,2);
        if(update_closest)
            closest(i,1) = j;
            closest(i,2) = dist;
        end
    end
end

%[4 pts] To compute the bag-of-words histogram, count how many features are mapped to each cluster.
for i = 1:size(closest,1)
    bow_repr(closest(i,1)) = bow_repr(closest(i,1)) + 1;
end

%[2 pts] Finally, normalize the histogram by dividing each entry by the sum of the entries. 
%(just a simple normalization. No clipping.)
bow_repr(1,:) = bow_repr(1,:)/sum(bow_repr(1,:));