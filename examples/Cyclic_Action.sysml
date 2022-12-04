action def Cyclic_Action {
  first start;
  then merge continue;
  then action a1: Action_1;
  then action a2: Action_2;
  then action a3: Action_3;
  then continue;
}

action def Action_1{}
action def Action_2{}
action def Action_3{}