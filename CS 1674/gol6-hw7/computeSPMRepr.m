% Inputs:
% im_size is the image size [height width] for an image,
% sift are the SIFT features for the image, and
% means are the cluster centers from the bag-of-visual-words clustering operation 
% (computed on the training images in load_split_dataset).

% Outputs:
% pyramid is a 1xD feature descriptor for the image combining the level-0 and level-1 of the 
% spatial pyramid match representation.
% level_0 is the standard bag-of-words histogram, and
% level_1 is the bag-of-words histogram at level-1 (i.e. one histogram for each quadrant of the image).
function [pyramid, level_0, level_1] = computeSPMRepr(im_size, sift, means)

% First, create a "bag of words" histogram representation of the features in the image, 
% using the function function [bow] = computeBOWRepr(descriptors, means) that you wrote for HW4 
% (if your function does not work, you can use the one provided on Canvas). 
% This will give you the representation shown in the left-hand side of the figure above, where the circles, 
% diamonds and crosses denote different "words". In the toy example above K is 3 (3 bins or clusters); 
% for this HW, use K = 50 all the time. This forms your representation of the image, at level L = 0 of the pyramid.
level_0 = computeBOWRepr(double(sift.d'), means);

% Then, divide the image into four quadrants as shown below. You need to know the locations of the 
% feature descriptors so that you know in which quadrant they fall; these are stored in the f variable 
% in each SIFT file. Compute four BOW histograms, using the computeBOWRepr function, but generating a 
% separate BOW representation for each quadrant. The concatenation of the four histograms is your level-1 
% representation of the image. The size of this representation is 1x(4*K).

quadrant_1 = []; quadrant_2 = []; quadrant_3 = []; quadrant_4 = [];
% sort each descriptor into respective bin
for i = 1:size(sift.d,2) % each col/descriptor
    x = sift.f(1,i);
    y = sift.f(2,i);
    cond_1 = (x >= 1) && (x <= im_size(1,1)/2) && (y >= im_size(1,2)/2+1) && (y <= im_size(1,2));
    cond_2 = (x >= 1) && (x <= im_size(1,1)/2) && (y <= im_size(1,2)/2) && (y >= 1);
    cond_3 = (x >= im_size(1,1)/2+1) && (x <= im_size(1,1)) && (y >= 1) && (y <= im_size(1,2)/2);
    cond_4 = (x >= im_size(1,1)/2+1) && (x <= im_size(1,1)) && (y >= im_size(1,2)/2+1) && (y <= im_size(1,2));
    if(cond_1) 
        quadrant_1 = horzcat(quadrant_1,sift.d(:,i));
    elseif(cond_2)
        quadrant_2 = horzcat(quadrant_2,sift.d(:,i));
    elseif(cond_3)
        quadrant_3 = horzcat(quadrant_3,sift.d(:,i));
    elseif(cond_4)
        quadrant_4 = horzcat(quadrant_4,sift.d(:,i));
    end
end
level_1 = [computeBOWRepr(double(quadrant_1'), means), computeBOWRepr(double(quadrant_2'), means), computeBOWRepr(double(quadrant_3'), means), computeBOWRepr(double(quadrant_4'), means)];

pyramid = [level_0, level_1];
end