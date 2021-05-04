% In this problem, you will implement feature extraction using the Harris corner detector, as discussed in class. Use the following signature: function [x, y, scores, Ih, Iv] = extract_keypoints(image);
% 
% Input:
% image is a color image of class uint8 which you should convert to grayscale and double in your function.
% Outputs:
% Each of x,y is an nx1 vector that denotes the x and y locations, respectively, of each of the n detected keypoints, i.e. points that (1) have "cornerness" R scores greater than a threshold, and (2) who survive the non-maximum suppression. Keep in mind that x denotes the horizontal direction, hence columns of the image, and y denotes the vertical direction, hence rows, counting from the top-left of the image.
% scores is an nx1 vector that contains the R score for each detected keypoint.
% Ih,Iv are matrices with the same number of rows and columns as your input image, and store the gradients in the x (horizontal) and y (vertical) directions at each pixel. We'll use these outputs for the next assignment.
function [x, y, scores, Ih, Iv] = extract_keypoints(image)
    % [10 pts] Let's do some preprocessing. First, set some parameters at the beginning of 
    % your function: set the value of k (from the "Harris Detector: Algorithm" slide) to 0.05, 
    % and use a window size of 5. Second, read in the image, and convert it to grayscale. 
    % Compute the horizontal image gradient Ih and the vertical image gradient Iv 
    % (use imfilter and Matlab will pad the image automatically around the border). 
    % Finally, initialize a matrix R of the same size as the image that will store the 
    % "cornerness" scores for each pixel.
    k = 0.05;
    w = 5;
    img = imread(image);
    img = rgb2gray(img);
    g = [1 0 -1; 2 0 -2; 1 0 -1];

    Ih = imfilter(im2double(img), g);
    Iv = imfilter(im2double(img), g');
    Iv2 = Iv.^2;
    Ih2 = Ih.^2;
    IhIv = Ih.*Iv;


    R = zeros(size(img));

%    [15 pts] Use a double loop to compute the cornerness score R(i, j) at each pixel 
%    i, j. This score depends on a 2x2 matrix M computed for each pixel, as shown in 
%    the slides. This matrix relies on all neighbors of i, j that are less than 
%    half_window_size away from it. Thus, the matrix M for a given pixel is a 
%    summation of window_size^2 matrices, each of size 2x2. Each of the 2x2 entries 
%    is the product of gradient image values at a particular pixel. 
%    After computing M, use the formula from class to compute the R(i, j) 
%    score for that pixel. If a pixel does not have enough neighbors 
%    (i.e. pixels on the border of the image), set its R score to -Inf. 
%    You can directly call the det and trace functions.
    has_all_neighbors = true;
    for i = 1:size(R,1)
        for j = 1:size(R,2)
            M = zeros(2, 2);
            for x = i-1:i+1
                for y = j-1:j+1
                    x_bnd_check = (x > 0 && x <= size(R,1));
                    y_bnd_check = (y > 0 && y <= size(R,2));
                    if (x_bnd_check && y_bnd_check)
                        M(1, 1) = M(1, 1) + Ih2(x, y);
                        M(1, 2) = M(1, 2) + IhIv(x, y);
                        M(2, 1) = M(2, 1) + IhIv(x, y);
                        M(2, 2) = M(2, 2) + Iv2(x, y);
                        has_all_neighbors = true;
                    else
                        has_all_neighbors = false;
                    end
                end

            end
            if(has_all_neighbors == true)
                R(i, j) = det(M) - k.*(trace(M).^2);
            else
                R(i, j) = -inf;
            end
        end
    end

    % [5 pts] After computing all R(i, j) scores, it is time to threshold them in order to 
    % find which pixels correspond to keypoints. You can set the threshold for the "cornerness" 
    % score R however you like; for example, you can set it to 5 times the average R score. 
    % Alternatively, you can simply output the top n keypoints or the 
    % top 1% highest-scoring keypoints.
    threshold = 5 * mean2(R);
    candidate_keypoints = {};

    for i=1:size(R, 1)
        for j=1:size(R,2)
            if R(i, j) > threshold
                candidate_keypoints = [candidate_keypoints, [i, j]];
            end
        end
    end

%     [10 pts] Perform non-maximum suppression by removing those keypoints whose R score 
%     is not larger than all of their 8 neighbors; if a keypoint does not have 8 neighbors, 
%     remove it. The scores/x/y that you output should correspond to the final set of 
%     keypoints, after non-max suppression. then set the keypoint scores and coordinates 
%     at those indices to [].
    scores = []; x = []; y = []; min_among_neighbors = true;
    for k = 1:size(candidate_keypoints,2)
        horizontal_lb = candidate_keypoints{k}(1)-1;
        horizontal_ub = candidate_keypoints{k}(1)+1;
        for i = horizontal_lb:horizontal_ub
            vertical_lb = candidate_keypoints{k}(2)-1;
            vertical_ub = candidate_keypoints{k}(2)+1;
            for j = vertical_lb:vertical_ub
                at_corner = (i == candidate_keypoints{k}(1) && j == candidate_keypoints{k}(2));
                in_xbounds = (i < 1 || i > size(R,1));
                in_ybounds = (j < 1 || j > size(R,2));
                if (at_corner == false && (in_xbounds || in_ybounds))
                    min_among_neighbors = false;
                elseif  (at_corner == false && ((R(candidate_keypoints{k}(1), candidate_keypoints{k}(2)) <= R(i, j))))
                    min_among_neighbors = false;
                end
            end
        end
        if min_among_neighbors == true
            scores = [scores  R(candidate_keypoints{k}(1), candidate_keypoints{k}(2))];
            x = [x candidate_keypoints{k}(2)];
            y = [y candidate_keypoints{k}(1)];
        end
        min_among_neighbors = true;
    end

    % [10 pts] Perform non-maximum suppression by removing those
    % keypoints whose R score is not larger than all of their 8
    % neighbors; if a keypoint does not have 8 neighbors, remove
    % it. The scores/x/y that you output should correspond to the final
    % set of keypoints, after non-max suppression. Tip: Don't remove
    % indices while looping over pixels; instead keep a vector of
    % indices you want to remove, then set the keypoints at those
    % indices to [].

    % inBounds = true;
    % for i = 1:size(candidate_keypoints,1)
    %     for j = 1:size(candidate_keypoints,2)

    %     count = 0;
    %     for j = 1:size(candidate_keypoints,2)
    %          fprintf("count: %d\n", count);
    %         check_x = (i - 1) > 1 && (i+1) < size(candidate_keypoints, 1);
    %         check_y = (j - 1) > 1 && (j+1) < size(candidate_keypoints, 2);
    %         if(check_x && check_y)
    %             fprintf("here!!\n");
    %             check_x1 = candidate_keypoints(i-1,j) > candidate_keypoints(i,j);
    %             check_x2 = candidate_keypoints(i+1,j) > candidate_keypoints(i,j);
    %             check_y1 = candidate_keypoints(i,j-1) > candidate_keypoints(i,j);
    %             check_y2 = candidate_keypoints(i,j+1) > candidate_keypoints(i,j);
    %             check_x1y1 = candidate_keypoints(i-1,j-1) > candidate_keypoints(i,j);
    %             check_x1y2 = candidate_keypoints(i-1,j+1) > candidate_keypoints(i,j);
    %             check_x2y1 = candidate_keypoints(i+1,j-1) > candidate_keypoints(i,j);
    %             check_x2y2 = candidate_keypoints(i+1,j+1) > candidate_keypoints(i,j);
    %             
    %             if(check_x1 && check_x2 && check_y1 && check_y2 && check_x1y1 && check_x1y2 && check_x2y1 && check_x2y2)
    %                 count = count + 1;
    %             elseif(check_x1 && check_x2 && check_y1 && check_y2 && check_x1y1 && check_x1y2 && check_x2y1 && check_x2y2 && count == 9)
    %                 scores = [scores, candidate_keypoints(i,j)]
    %                 x = [x, i];
    %                 y = [y,j];
    %             else
    %             end
    %         end
    %     end
    % end
    
%     [10 pts] Show your detected keypoints on three images. 
%     You can use any images from the past assignments, or you can use any images you want.
%     For each image, display the image and visualize the keypoints you have detected on it,
%     for example by drawing a circle for each keypoint. Use the scores variable and make 
%     keypoints with higher scores correspond to larger circles, e.g. plot(x(i), y(i), 
%     'ro', 'MarkerSize', scores(i) / 1000000000); (You don't have to divide by this value
%     exactly.) Name your visualizations vis1.png, vis2.png, vis3.png. Here is a sample of
%     keypoints that you might get on an image of a cardinal.
%     Note that there's no "perfect" solution: visually check if the keypoints make sense. 
%     If the circles do not scale nicely with the image, try resizing the image window 
%     (e.g., change the figure window size). This change the image size but not the 
%     circle sizes. Once you find a sweatspot, you can save the current figure with
%     (saveas(gcf, 'filename.png')).
    figure; imshow(image);
    hold on;
    for i = 1:size(x, 2)
        plot(x(i), y(i), 'ro', 'MarkerSize', abs(scores(i)/5));
    end
    hold off;