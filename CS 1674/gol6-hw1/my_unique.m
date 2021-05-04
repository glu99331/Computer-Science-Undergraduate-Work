function [B] = my_unique(A)
copy_A = repmat(A,1,1);
remove_rows = [];
for i = 1:size(copy_A, 1) %iterate through rows
    for j = i+1:size(copy_A, 1) %iterate through row != i
        num_matches = 0;
        for k = 1:size(copy_A, 2) %look at each value 
            if(copy_A(i,k) == copy_A(j,k))
                num_matches = num_matches + 1;
            end
            if(num_matches == size(copy_A, 2))
                remove_rows(length(remove_rows)+1) = j;
                break;
            end
        end
    end
end
keep = setdiff(1:size(copy_A,1), remove_rows); %rows to keep should be set difference between original rows A and remove rows.
B = copy_A(keep,:);
end
