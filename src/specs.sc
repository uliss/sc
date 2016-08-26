BpmTempoSpec : ControlSpec {
    *new {
        arg bpm = 60;
        ^super.new(30, 300, \lin, 1, bpm, "bpm");
    }
}

PathSpec : NonControlSpec {
    var <>path;

    *new { arg path;
		^super.new.path_(path)
	}

    *initClass {
        specs.addAll([
            \path -> PathSpec("")
        ])
    }

    storeArgs { ^[path] }
    defaultControl { ^"" }
}

InOutBusSpec : ControlSpec {
    *new {
        arg default = 0;
        ^super.new(0, 63, \lin, 1, default, "bus");
    }

    *initClass {
        specs.addAll([
            \bus -> InOutBusSpec(0)
        ])
    }
}

PanSpec : ControlSpec {
    *new {
        arg pos = 0;
        ^super.new(-1.0, 1.0, \lin, 0, pos);
    }
}

AmpSpec : ControlSpec {
    *new {
        arg amp = 1;
        ^super.new(0, 1, \amp, 0, amp);
    }
}
