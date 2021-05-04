--PART 1
-- Q1
CREATE OR REPLACE FUNCTION can_pay_loan(customer_ssn char(9))
    RETURNS BOOLEAN AS
$$
DECLARE
    can_pay BOOLEAN := false;
BEGIN
    SELECT (a.ssn = $1)
    INTO can_pay
    FROM account a
             left join loan l on a.ssn = l.ssn
    WHERE a.ssn = $1 AND a.balance > l.amount
       OR l.ssn is null;

    RETURN can_pay;
END;
$$ LANGUAGE plpgsql;

select can_pay_loan('123456789');

-- Q2
CREATE OR REPLACE FUNCTION check_customers_can_pay(rand_number INTEGER, discount INTEGER)
    RETURNS text AS
$$
DECLARE
    report       TEXT DEFAULT '';
    rec_customer RECORD;
    count integer := 0;
    cur_customers CURSOR
        FOR SELECT name, ssn,phone
            FROM customer;
BEGIN
    -- Open the cursor
    OPEN cur_customers;

    LOOP
        -- fetch row into the film
        FETCH cur_customers INTO rec_customer;
        -- exit when no more row to fetch
        EXIT WHEN NOT FOUND;

        -- build the output
        IF count = rand_number THEN
            IF can_pay_loan(rec_customer.ssn) THEN
                report := report ||', ['||rec_customer.phone||'] '|| rec_customer.name || ' you are getting the special double discount of ' || 2*discount ||'% if you pay today' ;
            END IF;
        ELSE
            IF can_pay_loan(rec_customer.ssn) THEN
                report := report ||', ['||rec_customer.phone||'] '|| rec_customer.name || ' you are getting the discount of ' || discount ||'% if you pay today' ;
            END IF;
        END IF;
        count := count + 1;
    END LOOP;

    -- Close the cursor
    CLOSE cur_customers;

    RETURN report;
END;
$$
    LANGUAGE plpgsql;

select check_customers_can_pay(0,1);

-- Part 2
-- Q1 & Q2
create or replace function func_1() returns trigger as
$$
begin
  update customer
  set num_accounts = num_accounts + 1
  where ssn = new.ssn;
  return new;
end;
$$ language plpgsql;

drop trigger if exists trig_1 on account;
create trigger trig_1
  after insert
  on account
  for each row
execute procedure func_1();

insert into account
values ('333', '123456789', '1234', '2010-10-10', 300, null);

select *
from customer
where ssn = '123456789';


-- Q3 & Q4
create or replace function func_2() returns trigger as
$$
begin
  update customer
  set num_accounts = num_accounts - 1
  where ssn = old.ssn;
  return new;
end;
$$ language plpgsql;

drop trigger if exists trig_2 on account;
create trigger trig_2
  after delete
  on account
  for each row
execute procedure func_2();

delete
from account
where ssn = '123456789';

select *
from customer
where ssn = '123456789';


-- Q5 & Q6
create or replace function func_3() returns trigger as
$$
begin
  insert into loan
  values (new.ssn, new.code, current_date, abs(new.balance), null);
  new.balance := 0;
  return new;
end;
$$ language plpgsql;

drop trigger if exists trig_3 on account;
create trigger trig_3
  before
    update of balance
  on account
  for each row
  when (new.balance < 0)
execute procedure func_3();

update account
set balance = -50
where acc_no = '124';

select *
from account
where acc_no = '124';

select *
from loan;


-- Q7 & Q8
create or replace function func_4() returns trigger as
$$
declare
  totalBalance numeric(15, 2);
  totalLoan    numeric(15, 2);
begin
  select sum(balance) into totalBalance
  from account;
  select sum(amount) into totalLoan
  from loan;
  if totalBalance < totalLoan * 2 then
    insert into alert
    values (current_date, totalBalance, totalLoan);
  end if;
  return new;
end;
$$ language plpgsql;

drop trigger if exists trig_4_account on account;
create trigger trig_4_account
  after update or delete
  on account
execute procedure func_4();

drop trigger if exists trig_4_loan on loan;
create trigger trig_4_loan
  after insert or update
  on loan
execute procedure func_4();

update account
set balance = 50
where acc_no = '124';

select *
from alert;

commit;

