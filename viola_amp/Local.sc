Local_ViolaAmp1 {
    var app_ipad;
    var add_iphone;
    var <osc_control;
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

    save {
        osc_control.save;
    }

    restore {
        osc_control.restore;
    }

    osc_set {
        arg name, value;
        var v;

        switch(value.class.name,
            \True, {v = 1},
            \False, {v = 0},
            {v = value}
        );

        osc_control.set("/viola/" ++ name, v);
    }

    pan {
        arg pos = 0;
        out.synth_out.set(\pan, pos);
    }

    amp {
        arg amp;
        out.synth_out.set(\amp, amp);
    }

    compress {
        arg value = true;
        this.osc_set("compress", value);
    }

    level {
        arg amp;
        this.osc_set("level", amp);
    }

    mute {
        arg value = true;
        this.osc_set("mute", value);
    }

    reverb {
        arg room = 0.72, damp = 0.5, mix = 0.6;
        viola.synth_reverb.set(\room, room, \damp, damp, \mix, mix);
    }

    pass {
        arg value = true;
        this.osc_set("pass", value);
    }

    testPlay {
        arg time = 0;
        osc_control.set("/violaFull/play", 1);
        viola.play(true, time);
    }

    testStop {
        osc_control.set("/violaFull/play", 0);
    }
}