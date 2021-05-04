% Function [] = generate_results(filename, reduceAmt, reduceWhat) - to run the functions you wrote, 
% and the provided functions, on an image, and show the results. In your function, after reading in the image, 
% you should call reduceWidth or reduceHeight as many times as needed to reduce the 
% width/height by the requested amount, but should only set the display flag to true in the first iteration.
function [] = generate_results(filename, reduceAmt, reduceWhat)
    im = imread(filename);
    %resize img to make it faster
    im = imresize(im, 0.7);
    %call energy image
    ei = energy_image(im); 
    direction = "";
    %determine direction to generate cumulative_minimum_energy_map
    if(strcmp(reduceWhat, "WIDTH"))
        direction = "HORIZONTAL";
    elseif(strcmp(reduceWhat, "HEIGHT"))
        direction = "VERTICAL";
    end
    
    fname_no_type = filename(1:strfind(filename,'.')-1);
    %energy_p1 = strcat('_energy.png');
    %energy_res = strcat(fname_no_type, energy_p1);
    M = cumulative_minimum_energy_map(ei, direction);
    %saveas(gcf, energy_res);
    %You should call reduceWidth or reduceHeight as many times as needed to reduce the 
    %width/height by the requested amount, but should only set the display flag to true 
    %in the first iteration. 
    for i = 1:reduceAmt
        if(i == 1)
            if(strcmp(direction, "VERTICAL"))
                shrunk = reduceHeight(im, true);
            elseif(strcmp(direction, "HORIZONTAL"))
                shrunk = reduceWidth(im, true);
            end
        else
            if(strcmp(direction, "VERTICAL"))
                shrunk = reduceHeight(shrunk, false);
            elseif(strcmp(direction, "HORIZONTAL"))
                shrunk = reduceWidth(shrunk, false);
            end
        end
    end
    
    r = size(im, 1);
    c = size(im, 2);
    if(strcmp(direction, "VERTICAL"))
        r = r - reduceAmt;
    elseif(strcmp(direction, "HORIZONTAL"))
        c = c - reduceAmt;
    end
    
    bf_shrink = imresize(im, [r,c]);
    
    figure;
    subplot(1,3,1);
    % original image
    subplot(1,3,1); imshow(im); title('Original Image');
    % content-aware-reduced image
    subplot(1,3,2); imshow(shrunk); title('Content-Aware Resizing');
    % Resizing using imresize 
    subplot(1,3,3); imshow(bf_shrink); title('Content-Agnostic Reduction');
    
    context_aware_p1 = strcat('_', lower(reduceWhat), '_reduced.png');
    context_aware_res = strcat(fname_no_type, context_aware_p1);
    
    %fprintf('%s\n', context_aware_res);
    
    directory = 'C:/Users/gordo/Desktop/hw2_starter/'
    
    context_aware_res_with_directory = strcat(directory, context_aware_res);
    %energy_res_with_directory = strcat(directory, energy_res);
    
    imwrite(shrunk,context_aware_res_with_directory);
    sgtitle(sprintf('Image %s Reduced by %d Pixels',reduceWhat,reduceAmt));
    
    saveas(gcf, 'interesting.png');
    
    