SP_RehearsalUtils {
    var latency_test;
    var instr_manager;

    *new {
        ^super.new.init;
    }

    init {
        latency_test = NodeUtility_LatencyTest.new;
        instr_manager = SP_InstrumentControl.new;
    }

    stop {
        latency_test.unbindOscAll;
        instr_manager.unbindOscAll;
    }
}

SP_AbstractOscControl {
    classvar <oscDict;

    var <osc;
    var <oscPath;

    *new {
        arg oscPath;
        ^super.new.init(oscPath);
    }

    *initClass {
        oscDict = Dictionary.new;
    }

    *freeAll {
        SP_AbstractOscControl.oscDict.keysDo { |key|
            SP_AbstractOscControl.free(key);
        };

        SP_AbstractOscControl.initClass;
    }

    *enable {
        arg path;
        var osc_f = oscDict[path];
        if(osc_f.notNil) {
            osc_f.enable;
        }
    }

    *disable {
        arg path;
        var osc_f = oscDict[path];
        if(osc_f.notNil) {
            osc_f.disable;
        }
    }

    *free {
        arg path;
        var osc_f = oscDict[path];
        if(osc_f.notNil) {
            osc_f.disable;
            osc_f.permanent = false;
            osc_f.free;
            oscDict[path] = nil;
        }
    }

    init {
        arg path;

        oscPath = path;

        try {
            if(SP_AbstractOscControl.oscDict.keys.includes(oscPath)) {
                this.free;

                "[%] OSC function already exists for this path: %".format(this.class, oscPath).warn;
            };

            osc = OSCFunc({
                arg msg;
                this.processOsc(msg);
            }, oscPath, nil, NodeJS.outOscPort);

            osc.permanent = true;
            SP_AbstractOscControl.oscDict[oscPath] = osc;
        } { |err|
            err.what.postln;
        }
    }

    enable {
        osc.enable;
    }

    disable {
        osc.disable;
    }

    free {
        osc.disable;
        osc.permanent = false;
        osc.free;
        SP_AbstractOscControl.oscDict[oscPath] = nil;
    }

    processOsc {
        arg msg;
        "[%] osc message: %".format(this.class, msg).postln;
    }
}

NodeUtility_LatencyTest : SP_AbstractOscControl {
    *new {
        ^super.new("/sc/utils/latency");
    }

    processOsc {
        arg msg;
        NodeJS.send2Cli("/cli/utils/latency", msg[1]);
    }
}

