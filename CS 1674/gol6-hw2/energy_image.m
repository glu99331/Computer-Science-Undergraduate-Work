%Function [energyImage, Ix, Iy] = energy_image(im) - to compute the energy at each pixel using the magnitude of the x and y gradients.
function [energyImage, Ix, Iy] = energy_image(im)
    im = double(rgb2gray(im));
    %filter from class
    v_gradient = [1 0 -1; 2 0 -2; 1 0 -1];
    h_gradient = transpose(v_gradient);
    
    Ix = imfilter(im, h_gradient);
    Iy = imfilter(im, v_gradient);
    %energyImage is computed similar to equation 1 in the paper, but using L2 norm: sqrt((dI/dx)^2+(dI/dy)^2).
    energyImage = sqrt((Ix .^ 2) + (Iy .^ 2));
end
