%Part II: Hybrid Images (10 points)
im1 = imread('woman_happy.png');
im1 = rgb2gray(im1);
im1 = imresize(im1, [512 512]);

im2 = imread('woman_neutral.png');
im2 = rgb2gray(im2);
im2 = imresize(im2, [512 512]);
%Apply a Gaussian filter to both, using e.g. imgaussfilt(im1, 10, 'FilterSize', 31). 
%Save the results as im1_blur, im2_blur.
im1_blur = imgaussfilt(im1, 10, 'FilterSize', 31);
im2_blur = imgaussfilt(im2, 10, 'FilterSize', 31);
%Obtain the detail image, by subtracting im2_blur from im2, and save the result as im2_detail.
im2_detail = im2_blur - im2;

figure;
hybrid = im1_blur + im2_detail;
imshow(hybrid);
saveas(gcf, 'hybrid.png');
