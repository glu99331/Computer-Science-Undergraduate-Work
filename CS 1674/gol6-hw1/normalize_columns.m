function [B] = normalize_columns(A)
    
    B = repmat(A,1,1)./sum(repmat(A,1,1),1);
end