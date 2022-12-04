import ScalarValues::Boolean;

action def Action_With_Condition {
  in condition: Boolean;
  first start;
  then action a1: Action_1;
  then action a2: Action_2;
    if condition then a3;
  action a3: Action_3;
  then done;
}

action def Action_1{}
action def Action_2{}
action def Action_3{}