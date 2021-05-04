function [features] = compute_features(x, y, scores, Ix, Iy)
% Part I: Feature Description (30 points)

% The output features is an Nxd matrix, each row of which contains the d-dimensional descriptor for the n-th keypoint. 
% We'll simplify the histogram creation procedure a bit, compared to the original implementation presented in class. 
% In particular, we'll compute a descriptor with dimensionality d=8 (rather than 4x4x8), which contains an 8-dimensional 
% histogram of gradients computed from a 11x11 grid centered around each detected keypoint (i.e. -5:+5 neighborhood
% horizontally and vertically).

% [4 pts] If any of your detected keypoints are less than 5 pixels from the top/left or 5 pixels from 
% the bottom/right of the image, i.e. pixels lacking 5+5 neighbors in either the horizontal or vertical 
% direction, erase this keypoint from the x, y, scores vectors at the start of your code and do not 
% compute a descriptor for it.
ignore_x  = [];
ignore_y = [];

Ix = double(Ix);
Iy = double(Iy);

for i = size(scores, 1):-1:1
    check_x_dim = x(i) <= 5 || x(i) > (size(Ix, 2)-5);
    check_y_dim = y(i) <= 5 || y(i) > (size(Ix, 1)-5);
    if(check_x_dim || check_y_dim)
       x(i) = [];
       y(i) = [];
       scores(i) = [];
    end
end

% [8 pts] To compute the gradient magnitude m(x, y) and gradient angle θ(x, y) at point (x, y), 
% take I to be the image and use the formula below shown in class and Matlab's atand, which returns 
% values in the range [-90, 90]. If the gradient magnitude is 0, then both the x and y gradients are 0, 
% and you should ignore the orientation for that pixel (since it won't contribute to the histogram).
mag = zeros(size(Ix,1),size(Ix,2)); 
theta = zeros(size(Ix,1),size(Ix,2)); 

for i = 1:size(x,1)
    r = y(i)-5:y(i)+5;
    c = x(i)-5:x(i)+5;
    mag(r,c) = sqrt(Ix(r,c).^2 + Iy(r,c).^2);
    
    if(mag(r,c) ~= 0)
        theta(r,c) = atand(Iy(r,c)./Ix(r,c));
    end
end

% [6 pts] Quantize the gradient orientations in 8 bins (so put values between -90 and -67.5 degrees in one bin, 
% the -67.5 to -45 degree angles in another bin, etc.) For example, you can have a quantization matrix with the 
% same size as the image, that says to which bin (1 through 8) the gradient of each pixel belongs.
grad_o = zeros(size(Ix,1),size(Ix,2)); 

for i = 1:size(x,1)
    r = y(i)-5:y(i)+5;
    c = x(i)-5:x(i)+5;
    for j = r
        for k = c
            theta1 = theta(x(i),y(i));
            bd1 = theta1 >= -90 && theta1 < -67.5;
            bd2 = theta1 >= -67.5 && theta1 < -45;
            bd3 = theta1 >= -45 && theta1 < -22.5;
            bd4 = theta1 >= -22.5 && theta1 < 0;
            bd5 = theta1 >= 0 && theta1 < 22.5;
            bd6 = theta1 >= 22.5 && theta1 < 45;
            bd7 = theta1 >= 45 && theta1 < 67.5;
            bd8 = theta1 >= 67.5 && theta1 <= 90;
            if(bd1)
                grad_o(j,k) = 1;
            elseif(bd2)
                grad_o(j,k) = 2;
            elseif(bd3)
                grad_o(j,k) = 3;
            elseif(bd4)
                grad_o(j,k) = 4;
            elseif(bd5)
                grad_o(j,k) = 5;
            elseif(bd6)
                grad_o(j,k) = 6;
            elseif(bd7)
                grad_o(j,k) = 7;
            elseif(bd8)
                grad_o(j,k) = 8;
            end
        end
    end
end

% [6 pts] Now, let's compute the SIFT histogram for each keypoint (based on its 11x11 grid centered around 
% each detected keypoint, i.e. -5:+5 neighborhood horizontally and vertically). To populate the SIFT histogram, 
% consider each of the 8 bins. To populate the first bin, sum the gradient magnitudes that are between 
% -90 and -67.5 degrees. Repeat analogously for all bins.
hist = zeros(size(x,1),8);
for i = 1:size(x,1)
    r = y(i)-5:y(i)+5;
    c = x(i)-5:x(i)+5;
    for j = r
        for k = c
            hist(i, grad_o(j,k)) = hist(i, grad_o(j,k)) + mag(j,k);
        end
    end
end

% [6 pts] Finally, you should clip all values to 0.2 as discussed in class, and normalize each descriptor to be 
% of unit length, e.g. using hist_final = hist_final / sum(hist_final); Normalize both before and after the clipping 
% (i.e., 1. normalize, 2. clip, 3. normalize again). You do not have to implement any more sophisticated detail from 
% the Lowe paper.
for i = 1:size(hist,1)
     hist(i,:) = hist(i,:)/sum(hist(i,:));
end
for i = 1:size(hist,1)
    for j = 1:8
        if(hist(i,j) > 0.2)
            hist(i,j) = 0.2;
        end
    end
end
for i = 1:size(hist,1)
     hist(i,:) = hist(i,:)/sum(hist(i,:));
end

features = hist;
