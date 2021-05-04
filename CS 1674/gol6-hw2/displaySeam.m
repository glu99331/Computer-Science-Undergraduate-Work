%Function displaySeam(im, seam, seamDirection) - to display the selected type of seam on top of the input image.
function displaySeam(im, seam, seamDirection)
    hold on;
    if strcmp(seamDirection,'HORIZONTAL')
    %cols = sz(1);
        plot(1:size(seam),seam,'r');
    elseif strcmp(seamDirection,'VERTICAL')
    %rows = sz(2);
        plot(seam,1:size(seam),'r');
    end
    hold off;
    

end
