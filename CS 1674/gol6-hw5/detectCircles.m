function [centers] = detectCircles(im, edges, radius, top_k)
    % a: a function to find and visualize circles given an edge map
H = zeros(size(im,1,2));
q_val = 5; % quantization value, can change

for pixel = 1:size(edges,1) % for every edge pixel
    x = edges(pixel,1);
    y = edges(pixel,2);
    theta = edges(pixel,4);
    % calculate a and b
    a = x - (radius*cosd(theta)); % col
    b = y - (radius*sind(theta)); % row
    a = ceil(a/q_val); % quantize
    b = ceil(b/q_val);
    % ignore centers outside of image
    if(a > 0 && a <= size(im,2) && b > 0 && b <= size(im,1))
        H(a,b) = H(a,b) + 1;
    end
end

% calculate centers
centers = zeros(top_k,2);
[descend] = sort(H(:),'descend');
for i = 1:top_k
    [x,y] = find(H == descend(i));
    x = x(1) * q_val;
    y = y(1) * q_val;
    centers(i,1) = x;
    centers(i,2) = y;
end 

% visualize circles
figure;
imshow(im);
title(['r = ',num2str(radius)]);
viscircles(centers, radius * ones(size(centers, 1), 1));
saveas(gcf,'egg_circles.png');