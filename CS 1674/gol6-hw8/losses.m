% Write a script losses.m to compute and print each type of loss (hinge or cross-entropy) for each weight matrix. 
% Then, in a file answers.txt, say which weight matrix is the best one, (1) according to the hinge loss, and 
% (2) according to the cross-entropy loss.

load('weights_samples.mat');
W = {W1, W2, W3};
x = {x1, x2, x3, x4};

h_loss = zeros(3,4); % hinge loss
ce_loss = zeros(3,4); % cross-entropy loss

for i = 1:3 % Weights
    for j = 1:4 % inputs
        s = W{i} * x{j};
        h_loss(i,j) = hinge_loss(s,j);
        ce_loss(i,j) = cross_entropy_loss(s,j);
    end
end

h_loss
ce_loss