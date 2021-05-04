% [2 pts] Generate a 1000000x1 (one million by one) vector of random numbers from a Gaussian (normal) distribution
% with mean of 0 and standard deviation of 5. Use Matlab's randn function.
x = 5*randn(1000000,1);

% [3 pts] Add 1 to every value in the previous list, by using a loop. 
% To determine how many times to loop, use Matlab's size function. 
% Time this operation and print the number in the code. Write that number down in answers.txt. 
% Use Matlab's documentation to find out how to time operations.
tic
for i = 1:size(x)
    x(i) = x(i) + 1;
end
toc

tic
x = x+1;
res = toc;
fprintf("Elapsed time is %.6f seconds.\n", res);

% [2 pts] Plot the exponential function 2.^x, for non-negative even values of x smaller than 100, without using loops.
figure;
vec = 0:2:98;
plot(2.^x);
title('Plot of 2.^x for non-negative even values less than 100'); 

% [3 pts] Create two matrices A and B which, when added together, result in a 10x10 matrix C 
% containing all numbers from 1 to 100. 
% In other words, when I add A and B and convert the result to vector form, 
% I should get a vector containing all numbers from 1 to 100. 
% In code, C = A + B; assert(all(C(:) == (1:100)') == 1); 
% Each matrix should only be created with a single command (no loops).
A = [1:10; 11:20; 21:30; 31:40; 41:50; 51:60; 61:70; 71:80; 81:90; 91:100]';
B = zeros(10,10);
C = A+B;
assert(all(C(:) == (1:100)') == 1);

% [2 pts] Create a script that prints all the values between 1 and 10, in random order, 
% with pauses of 1 second between each two prints.
p = randperm(10);
for i = 1:10
    fprintf("%d\n", p(i));
    if(mod(i,2) == 0)
        pause(1);
    end
end

% Generate two random matrices A and B, and compute their product by hand, using loops. 
% The size of the first matrix should be [5, 3] and of the second [3, 5]. 
% Check your code by comparing the loop-computed product with the product that you get from Matlab's A*B.
v1 = rand(5,3);
v2 = rand(3,5);
res = zeros(5,5);

for i = 1:5
    for j = 1:5
        for k = 1:3
            res(i,j) = res(i,j) + v1(i,k)*v2(k,j);
        end
    end
end
test = v1*v2;
disp(res);
disp(test);

%Write a function function [B] = normalize_rows(A) which uses a single command
%(one line and no loops) to make the sum in each row of the matrix 1. 
%Use the repmat function. The sum of the entries in each row should be 1, 
%in the matrix output by your function. 
%Note that you can't obliterate the original matrix, i.e. 
%you can't just create rows with N values of 1/N each-- 
%you have to transform the original values but preserve their order.

row_norm = normalize_rows(res);
%Now write a function function [B] = normalize_columns(A) 
%which instead ensures that the sum in each column is 1.
col_norm = normalize_columns(res);
%Create a function function [val] = fib(n) that 
%returns the n-th number (n >= 1) in the Fibonacci sequence 1, 1, 2, 3, 5, 8, 13...
f = fib(2);

%Implement a function function [N] = my_unique(M) that takes in a matrix M, 
%removes duplicate rows from that input matrix and outputs the result as matrix N. 
%You cannot call Matlab's unique function. Loops are allowed.
test_vec = [4 8 2; 4 8 2; 1 4 2; 7 9 0; 1 2 3; 1 3 2; 7 9 0];
uq_test = my_unique(test)

%Read this image into Matlab as a matrix, and write down its dimensions.
I = imread('pittsburgh.png'); % Read a PNG image

%Convert the image into grayscale.
figure;
im_gray = rgb2gray(I);
imshow(im_gray);

%Use the function sum and a logical operator which measures equality to a scalar, 
%to determine and write down how many pixels in the grayscale image are equal to 6. 
%Don't use loops.
num_sixes = sum(im_gray(:) == 6,'all');
fprintf("The number of pixels that are 6 are: %d\n", num_sixes);

%Find the darkest pixel in the image, and write its value and [row, column] 
%in your answer sheet. Don't use loops. Hint: Convert to a vector first, 
%and use Matlab's ind2sub function. 
%Use Matlab's help to find out how to use that function.
img_as_colvec = im_gray(:);
[val, ind] = min(img_as_colvec);
[col,row] = ind2sub(size(im_gray), ind);
fprintf("The darkest pixel is: %d, located at (%d, %d)\n", val, col, row);

%Consider a 31x31 square (a square with side equal to 31 pixels) that is 
%centered on the darkest pixel from the previous question. 
%Replace all pixels in that square with white pixels (pixels with value 255). 
%Do this with loops.
c_start = col - 15;
c_end = col + 15;

r_start  = row - 15;
r_end = row + 15;

for i = c_start:c_end
    for j = r_start:r_end
        im_gray(i,j) = 255;
    end
end
figure;
imshow(im_gray);

%Take the previous image with the white square in it. 
%Place a 121x121 gray square (e.g. pixel values 150) at 
%the center of this image. This time you are NOT allowed to use loops.
center = [size(im_gray,1)/2, size(im_gray,2)/2];
c_start = center(2) - 60;
c_end = center(2) + 60;

r_start  = center(1) - 60;
r_end = center(1) + 60;
im_gray(r_start:r_end, c_start:c_end) = 150;

%Make a new figure, display the modified image 
%(which includes both a white square and gray square), 
%and save the new figure to a file using saveas(gcf, 'new_image.png').
figure;
imshow(im_gray);
saveas(gcf, 'new_image.png');

%Using the original pittsburgh.png image, 
%compute the scalar average pixel value 
%along each channel (R, G, B) separately, 
%then subtract the average value per channel. 
%Display the resulting image and write it to a file mean_sub.png. 
%If you choose to do this using a new matrix as your image, 
%make sure it is of the same class/datatype (uint8) as the 
%original image; a simple cast would do the job. You may use loops.
mean_sub = I;
avg_r = mean(mean_sub(:,:,1), 'all');
avg_b = mean(mean_sub(:,:,2), 'all');
avg_g = mean(mean_sub(:,:,3), 'all');

mean_sub(:,:,1) = mean_sub(:,:,1) - avg_r;
mean_sub(:,:,2) = mean_sub(:,:,2) - avg_g;
mean_sub(:,:,3) = mean_sub(:,:,3) - avg_b;

figure;
imshow(mean_sub);
saveas(gcf, 'mean_sub.png');