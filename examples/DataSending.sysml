import ScalarValues::Boolean;
	
action def Data_Sending {
  in need_ack: Boolean;
  in ack_recieved: Boolean;
  in data_left: Boolean;
  first start;
  then action oc: OpenConnection;
  then merge m1;
  then action pd: PrepareData;
  then merge m2;
  then action sd: SendData;
  then decide d1;
	if need_ack then wt;
    else m3;
  merge m3;
  first wt then d2;
  decide d2;
  	if ack_recieved then m3;
   	else m2;
  first m3 then d3;
  decide d3;
  	if data_left then m1;
    else cc;
  first cc then done;
  action cc: CloseConnection;
  action wt: WaitTimeout;
}
		
action def OpenConnection{}
action def PrepareData{}
action def SendData{}
action def WaitTimeout{}
action def CloseConnection{}
