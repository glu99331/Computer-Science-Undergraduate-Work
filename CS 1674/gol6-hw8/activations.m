%  First, encode all inputs and weights as matrices/vectors in Matlab. In our example, D=4, M=3, K=2.
x = [10, 1, 2, 3]; 
w1 = [0.5, 0.6, 0.4, 0.3; 0.02, 0.25, 0.4, 0.3; 0.82, 0.1, 0.35, 0.3]; 
w2 = [0.7, 0.45, 0.5; 0.17, 0.9, 0.8];
D = 4; M = 3; K = 2;

% Second, write code to compute and print the value of z2, if a tanh activation is used. 
% You can use Matlab's tanh function.
z = zeros(1,M);
for j = 1:M 
    for i = 1:D
        z(1,j) = z(1,j) + w1(j,i) * x(1,i);
    end
end

z2 = tanh(z(2));

% Third, write code to compute and print the value of y1. RELU activation is used at the hidden layer, and 
% sigmoid activation is used at the output layer. Don't use the Matlab functions, instead use the formulas 
% for these functions that were shown in class and implement them yourself. 
% You don't have to implement the exp function, just call it.

z = max(0,z); %compute RELU
y = zeros(1,K);
for k = 1:K
    for j = 1:M
        y(1,k) = y(1,k) + w2(k,j) * z(1,j);
    end
end
y = 1.0./(1.0 + exp(-y)); %compute sigmoid
y1 = y(1)

