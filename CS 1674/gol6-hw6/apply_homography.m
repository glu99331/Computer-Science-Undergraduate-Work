function [p2] = apply_homography(p1, H) 

%Apply the homography and convert back from homogeneous coordinates, 
%as shown in slide 42 (i.e., [wx' wy' w'] to [x' y']).

p2 = H * p1;
w = p2(size(p2,1));
p2 = [p2(1)/w; p2(2)/w];