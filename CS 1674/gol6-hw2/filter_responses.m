%Part I: Image Responses with Filters (10 points)
F = makeLMfilters;
set(0, 'DefaultFigureVisible', 'off');
%make a cell array:
cell_arr = cell([6,48]);
fnames = {'cardinal1.jpg', 'cardinal2.jpg', 'leopard1.jpg', 'leopard2.jpg', 'panda1.jpg', 'panda2.jpg'};

%Read in all images, convert them to grayscale, and resize them to the same square size 
%(e.g. 100x100), so that the visual map of responses (filtering outputs) 
%can be more comparable across images. 
for i = 1:size(fnames,2)
    curr_img = imread(fnames{i});    
    curr_img = rgb2gray(curr_img);
    curr_img = imresize(curr_img, [100 100]);
    fnames{i} = curr_img;
    %Compute the cross-correlation of each image with each of the 48 filters using imfilter.
    for j = 1:48
        cell_arr{i,j} = imfilter(fnames{i}, F(:,:,j));
    end
    
end

%for each filter: generate a 2x4 subplot 
%show filter and blank subplot
%reponses to cardinal images, leopard images, and panda images
for i=1:48
    figure;
    subplot(2,4,1),imagesc(F(:,:,i));
    subplot(2,4,3), imagesc(cell_arr{1,i}), title('cardinal1.jpg');
    subplot(2,4,4), imagesc(cell_arr{2,i}), title('cardinal2.jpg');
    subplot(2,4,5), imagesc(cell_arr{3,i}), title('leopard1.jpg');
    subplot(2,4,6), imagesc(cell_arr{4,i}), title('leopard2.jpg');
    subplot(2,4,7), imagesc(cell_arr{5,i}), title('panda1.jpg');
    subplot(2,4,8), imagesc(cell_arr{6,i}), title('panda2.jpg');
    saveas(gcf, strcat('responses_to_filter_', num2str(i), '.png'));
end
