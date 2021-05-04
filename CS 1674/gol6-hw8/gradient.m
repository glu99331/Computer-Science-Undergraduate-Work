% loop over the dimensions of the weight vector and numerically compute the derivative for each dimension, 
% as shown in class. Then concatenate the derivatives together, and output the resulting vector as the gradient. 
% Use the hinge loss to compute the loss for that weight vector over all examples. Use h=0.0001.

% In the same script, also compute a weight update (one iteration) with learning rate of 0.001.

% Make W1 into a vector via W1(:) to iterate through each weight. 
% Use reshape to reshape any intermediate W1_plus_h back into a 4x25 matrix when you need to compute the scores s = W*x.
% Make sure to change each dimension of the weight vector one at a time. 
% Store the original version of the weight vector before any changes were made to it, and reset the weight vector 
% to that original each time you loop over the dimensions.
% How do I check if the new W is better? One obvious way is to check if your new W leads to a lower loss. 
% Once you update W in gradient.m, compute the hinge loss in loss.m from Part II with the updated W. 
% If the loss is actually lower, your gradient descent step is likely to be working.

W = W1(:);
x = {x1, x2, x3, x4};
h = 0.0001;
g_dW = zeros(size(W));


for i = 1:size(W,1)
   W_loss = 0; 
   for j = 1:size(x,1)
       s = reshape(W, 4, 25) * x{j};
       W_loss = W_loss + hinge_loss(s,j);
   end
   
   Wh = W;
   Wh(j) = Wh(j) + h;
   Wh_loss = 0; 
   
   for j = 1:size(x,1) 
       s = reshape(Wh, 4, 25) * x{j};
       Wh_loss = Wh_loss + hinge_loss(s,j);    
   end
   
   g_dW(i) = (Wh_loss - W_loss)/h;
end    

W1_it = W - (0.001 * g_dW); 