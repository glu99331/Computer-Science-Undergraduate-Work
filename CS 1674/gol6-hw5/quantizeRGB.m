function [outputImg, meanColors, clusterIds] = quantizeRGB(origImg, k)
% performs clustering in the 3-dimensional RGB space, and "quantizes" the image. 
% Use the built-in Matlab function kmeans (and read the documentation to see how to use it). 
% At the end of your function, show the (1) original and (2) quantized image, in a 1x2 subplot. 
% Make sure to label the figure with the value of k that was used.

% If the variable origImg is a 3d matrix (numrowsxnumcolsx3) containing a color image with 
% numpixels pixels (first two dimensions) in each color channel (third dimension), then
% X = reshape(origImg, [numpixels, 3]); will yield a matrix with the RGB features as its rows. 
% It is in this space (samples = rows, features = columns) that you want to apply k-means.
X = double(reshape(origImg, [size(origImg,1) * size(origImg,2), 3]));
[clusterIds, mean_cols] = kmeans(double(X),k); 

M = reshape(clusterIds, size(origImg,1),size(origImg,2));
outputImg = zeros(size(origImg));

% replace the R,G,B value at each pixel with the average R,G,B value in the cluster to which that pixel belongs. 
for x = 1:size(origImg,1)
    for y = 1:size(origImg,2)
        for i = 1:k
            if M(x,y) == i
                for c = 1:3
                     outputImg(x,y,c) = mean_cols(i,c);   
                end
            end
        end
    end
end
outputImg = uint8(outputImg);

%plot
figure;
imshow(outputImg);
title(['Quantized image (RGB) for K = ',num2str(k)]);
saveas(gcf,'k10.png');
