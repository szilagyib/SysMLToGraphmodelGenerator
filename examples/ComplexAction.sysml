import ScalarValues::Boolean;
	
action def Complex_Action {
  in condition_1: Boolean;
  in condition_2: Boolean;
  in condition_3: Boolean;
  first start;
  then merge m1;
  then action a1: Action_1;
  then merge m2;
  then decide d1;
	if condition_1 then a2;
    else a3;
  first a2 then d2;
  decide d2;
  	if condition_2 then m2;
   	else a4;
  first a3 then d3;
  decide d3;
  	if condition_3 then m3;
    else done;
  first a4 then m3;
  merge m3;
  then m1;
  action a2: Action_2;
  action a3: Action_3;
  action a4: Action_4;
}
		
  action def Action_1{}
  action def Action_2{}
  action def Action_3{}
  action def Action_4{}
