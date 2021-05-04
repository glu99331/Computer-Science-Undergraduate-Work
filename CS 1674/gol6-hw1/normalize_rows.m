function [B] = normalize_rows(A)
    copy_A = repmat(A,1,1);
    B = copy_A./sum(copy_A,2);
end