function H = estimate_homography(PA, PB)
% Compute a homography between the points from the first image (in matrix PA) and second image (in matrix PB). 
% 
% Inputs: PA and PB are 4x2 matrices (or they can have more than 4 rows); 
% each row contains the (x, y) coordinates of matching points 
% (a row in the first matrix should be the match for a row in the second matrix). 
%     
% Output: H is a 3x3 matrix. 
%     
% You need to set up a system of equations A as shown in slide 36 and 38 here. 
% Once you set up your system A, solve for H using: [~, ~, V] = svd(A); h = V(:, end); H = reshape(h, 3, 3)';


% A is 2 x 9
% h is 9 x 1
% set up A
numPts = size(PA,1);
A = zeros(numPts*2,9);
pt = 1; % track which point

for i=1:2:(numPts*2) % for each point
    A(i,:) = [PA(pt,1)*-1, PA(pt,2)*-1,-1,0,0,0,PA(pt,1)*PB(pt,1),PA(pt,2)*PB(pt,1),PB(pt,1)];
    A(i+1,:) = [0,0,0,PA(pt,1)*-1,PA(pt,2)*-1,-1,PA(pt,1)*PB(pt,2),PA(pt,2)*PB(pt,2),PB(pt,2)];
    pt = pt + 1; % next point
end

% solve for H
[~, ~, V] = svd(A);
h = V(:, end); 
H = reshape(h, 3, 3)';