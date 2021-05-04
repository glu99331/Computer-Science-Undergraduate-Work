function [loss] = cross_entropy_loss(scores, correct_class)

e = exp(scores)/sum(exp(scores));
loss = -log(e(correct_class));