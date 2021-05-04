% 1) Load them into Matlab and show them in separate figures, followed by the command impixelinfo after each figure. 
% This will allow you to see pixel coordinates at the bottom of the
% figures, when you hover over the image.
img1 = imread('uttower1.jpeg');
img2 = imread('uttower2.jpeg');
% img1 = imread('keble1.png');
% img2 = imread('keble2.png');
figure;
imshow(img1);
impixelinfo;

figure;
imshow(img2);
impixelinfo;

% 2) Examine the images, and manually determine at least four pairs of points 
% (in each pair, one point should be from the first image, and one from the second image) 
% that are distinctive. Write them down in matrix form in the script, with rows being the 
% points and columns being the x and y locations. This will give you the PA, PB to use below.
% PA = [163 77;324 106;314 12;182 154];
% PB = [67 88;226 122;218 31;85 166];
PA = [297 461; 313 616; 111 590; 267 231; 500 399; 451 470];
PB = [748 494; 777 650; 580 627; 706 267; 959 423; 909 499];

% 3) Use the function H = estimate_homography(PA, PB) you wrote that computes a 
% homography between the points from the first image (in matrix PA) and second image (in matrix PB).
H = estimate_homography(PA,PB);

% 4) Now pick one new point from the first image, write it down in your script, 
% and use the computed homography to compute where it "lands" in the second image. 
% Use your apply_homography function to do this. 
% Create a 1x2 subplot which shows (1) the first image, with the p1 point selected shown in green, 
% and (2) the second image, with the p2 point computed using the homography, shown in yellow. 
% Save and submit your result as keble_onept.png.
% p1 = [218; 180; 1];
p1 = [443; 297; 1];
p2 = apply_homography(p1,H);
figure;
subplot(1,2,1);
imshow(img1);
hold on;
plot(p1(1),p1(2),'.g','MarkerSize',20);
hold off;

subplot(1,2,2);
imshow(img2);
hold on;
plot(p2(1),p2(2),'.y','MarkerSize',20);
hold off;

saveas(gcf,'uttower_onept.png')

% 5) Now stitch a mosaic from the two images. Save and submit your result as keble_mosaic.png.
% [2 pts] Create a new canvas which replicates the size of image2 3 times in the horizontal and 3 times in the vertical direction, and puts image2 in the middle of this canvas.
% [8 pts] For each pixel at location p1 in image1, apply the estimated homography to determine location p2 where to send the pixel from p1 into the canvas you created. 
% The new location you compute might be negative, which means it indexes the black part of the canvas; 
% you will need to add two values to the first and second component of p2, 
% to convert from image2's coordinate system to the large canvas' coordinate system. 
% The location you computed might be a non-integer so round both the x and y components up 
% and down (using ceil and floor), resulting in up to four locations in image2, for each pixel in image1.
% [3 pts] Add each pixel from image1 to (four locations in) the large canvas.
% [2 pt] After iterating over all pixels in image1, show the stitched result.
w = size(img2,2);
h = size(img2,1);
canvas = zeros(3*h,3*w,3);
canvas(height+1:2*h,w+1:2*w,1:3) = im2double(img2);

for i = 1:size(img1,1)
    for j = 1:size(img1,2) 
        p2 = apply_homography([j;i;1],H);
        n1 = [width + floor(p2(1)),height + floor(p2(2))];
        n2 = [width + ceil(p2(1)),height + floor(p2(2))];
        n3 = [width + floor(p2(1)),height + ceil(p2(2))];
        n4 = [width + ceil(p2(1)),height + ceil(p2(2))];
        
        canvas(n1(2),n1(1),1:3) = im2double(im1(i,j,1:3));
        canvas(n2(2),n2(1),1:3) = im2double(im1(i,j,1:3));
        canvas(n3(2),n3(1),1:3) = im2double(im1(i,j,1:3));
        canvas(n4(2),n4(1),1:3) = im2double(im1(i,j,1:3));
        
    end
end
figure;
imshow(canvas);
saveas(gcf,'uttower_mosaic.png')