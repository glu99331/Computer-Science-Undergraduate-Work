function [loss] = hinge_loss(scores, correct_class)

% scores is a 4x1 set of predicted scores, one score for each class, for some sample, and
% correct_class is the correct class for that same sample.

% loss is a scalar measuring the hinge loss, as defined in class, given these scores and ground-truth class.

loss = 0;
for i = 1:size(scores,1)
    if i == correct_class
        continue
    end
    loss = loss + max(0,scores(i) - scores(correct_class) + 1);
end
