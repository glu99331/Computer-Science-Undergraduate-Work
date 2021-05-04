function [Output] = my_pool(Input, Pool_Size)

Output = zeros(size(Input,1)/Pool_Size);

idx = 1;
idx2 = 1;

for i = 1:Pool_Size:size(Input,1)-Pool_Size + 1
    idx2 = 1;
    
    for j = 1:Pool_Size:size(Input,1)-Pool_Size + 1
        Output(idx, idx2) = max(max(Input(i:i+Pool_Size-1,j:j+Pool_Size-1)));
        idx2 = idx2+1;
    end
    idx = idx+1;
end
