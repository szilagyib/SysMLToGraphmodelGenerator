package VehicleExample {
	private import ScalarValues::Integer;
	private import ScalarValues::String;
	
	abstract part def Vehicle {
		attribute size : String;
	}
	
	part def HumanDrivenVehicle specializes Vehicle {
		ref part driver : Person;
		ref part passenger : Person[2];
		ref part p : Person[2..3];
	}
	
	part def PoweredVehicle :> Vehicle {
		part eng : Engine[1..2];
	}
	
	part def HumanDrivenPoweredVehicle :> HumanDrivenVehicle, PoweredVehicle;
	
	part def Engine;
	
	part def Person;
	
	part engine : Engine;	
}