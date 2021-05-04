function z = fib(n)
    if(n <= 2)
        z = 1;
    else
        z = fib(n-1) + fib(n-2); 
    end
end


