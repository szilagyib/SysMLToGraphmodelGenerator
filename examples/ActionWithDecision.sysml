import ScalarValues::Boolean;

action def Action_With_Decision {
  in condition: Boolean;
  first start;
  then action a1: Action_1;
  then action a2: Action_2;
  then decide d1;
    if condition then a3;
    if not condition then a4;
  action a3: Action_3;
  action a4: Action_4;
  first a3 then done;
  first a4 then done;
}

action def Action_1{}
action def Action_2{}
action def Action_3{}
action def Action_4{}