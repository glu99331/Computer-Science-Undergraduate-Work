function [Output] = my_conv(Image, Filter, Padding, Stride)

conv = zeros(((size(Image,1)+Padding*2)-size(Filter,1))/Stride+1, ((size(Image,2)+Padding*2)-size(Filter,1))/Stride+1);

padded_im = zeros(size(Image,1)+2*Padding, size(Image,2)+2*Padding);
padded_im(Padding+1:Padding+size(Image,1), Padding+1:Padding+size(Image,2)) = Image;

idx = 1;
idx2 = 1;

for i = 1:Stride:size(padded_im,1)-size(Filter,1)+1
    idx2 = 1 %idx for inner loop
    for j = 1:Stride:size(padded_im,2)-size(Filter,2)+1
        temp = padded_im(i:i+size(Filter,1)-1,j:j+size(Filter,2)-1);
        temp_f = Filter(1:size(Filter,1),1:size(Filter,2));
        conv(idx,idx2) = sum(sum(temp.*temp_f,2));
        idx2 = idx2+1;
    end
    idx = idx+1;
end
Output = conv;