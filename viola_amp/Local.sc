Local_ViolaAmp1 {
    var app_ipad;
    var add_iphone;
    var osc_control;
    var <viola;
    var out;

    *new {
        arg debug = false;
        ^super.new.init(debug);
    }

    init {
        arg debug = false;
        viola = Sp_SynthViolaIn.new;
        viola.run(true);

        osc_control = Sp_OscViolaIn.new;
        osc_control.debug(debug);
        osc_control.mapSynthControls(viola);

        viola.synth_reverb.set(\room, 0.72);
        viola.synth_reverb.set(\damp, 0.5);
        viola.synth_reverb.set(\mix, 0.6);

        out = Sp_SynthOut.new;
        out.run(viola);
    }

    connectIpad {
        app_ipad = Sp_AppTouchOSC.ipad;
        app_ipad.appendFloatIndicator("/viola/level");
        osc_control.bindOscApp(app_ipad);
    }

    connectIphone {
        add_iphone = Sp_AppTouchOSC.iphone;
        add_iphone.appendFloatIndicator("/viola/level");
        osc_control.bindOscApp(add_iphone);
    }

    pan {
        arg pos = 0;
        out.synth_out.set(\pan, pos);
    }

    amp {
        arg amp;
        out.synth_out.set(\amp, amp);
    }

    mute {
        arg value = true;
        viola.mute(value);
    }

    reverb {
        arg room = 0.72, damp = 0.5, mix = 0.6;
        viola.synth_reverb.set(\room, room, \damp, damp, \mix, mix);
    }

    testPlay {
        arg time = 0;
        viola.play(true, time);
    }

    testStop {
        viola.play(false);
    }
}