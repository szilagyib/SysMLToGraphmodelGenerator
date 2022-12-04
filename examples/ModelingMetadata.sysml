package ModelingMetadataExample {
	private import Base::Anything;
	private import ScalarValues::String;
	private import ScalarValues::Integer;
	private import ScalarValues::Real;
	private import RiskMetadata::Risk;
	private import RiskMetadata::RiskLevel;
	private import RiskMetadata::Level;
	
	enum def StatusKind {
		open;
		tbd;
		tbr;
		tbc;
		done;
		closed;
	}
	
	attribute def StatusInfo {
		attribute originator : String [0..1];
		attribute owner : String [0..1];
		attribute status : StatusKind;
		attribute test_risk : Risk;
		attribute risk : Risk [0..1];
	}
	
	attribute def Rationale {
		attribute text : String;
		attribute number : Integer;
		attribute etc : Real;
	}
	
	attribute def Issue {
		attribute text : String;
	}
}