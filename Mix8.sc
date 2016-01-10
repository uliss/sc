Sp_AppOSC {
    var <inPort;
    var <outHost;
    var <outPort;

    *new {
        arg inPort, outHost, outPort;
        ^super.new.initAppOsc(inPort, outHost, outPort);
    }

    initAppOsc {
        arg in_Port, out_Host, out_Port;
        inPort = in_Port;
        outHost = out_Host;
        outPort = out_Port;
    }

    path {
        arg name;
        ^name;
    }
}

Sp_AppTouchOSC : Sp_AppOSC {
    *new {
        arg host = "serge-ipad";
        ^super.new(5000, host, 5001).initTouchOsc;
    }

    *iphone {
        ^ Sp_AppTouchOSC.new("serge-iphone");
    }

     *ipad {
        ^ Sp_AppTouchOSC.new("serge-ipad");
    }

    initTouchOsc {

    }
}

Sp_OscControl {
    var <>name;
    var <>debug;
    var <bus;
    var <>callback;
    var <value;

    // private
    var oscListeners;
    var midiFunc;

    *new {
        arg controlName, server = Server.default;
        ^super.new.initOscControl(controlName, server)
    }

    initOscControl {
        arg controlName, server;
        name = controlName;
        debug = false;
        callback = nil;
        bus = Bus.control(server).set(0);
        value = 0;
        // init listeners
        oscListeners = Dictionary.new;
    }

    bindMidiCC {
        arg ccNum, chan = nil, func = linlin(_, 0, 127, 0, 1);
        midiFunc = MIDIFunc.cc({ |v|
            this.set(func.value(v).lag(0.05));
        }, ccNum, chan);
    }

    *oscClientId {
        arg path, outHost, outPort;
        ^format("osc://%:%%", outHost, outPort, path);
    }

    bindOscApp {
        arg app;
        this.bindOsc(app.path(name), app.inPort, app.outHost, app.outPort);
    }

    bindOsc {
        arg path, inPort, outHost, outPort, func = nil;
        var client_id = this.class.oscClientId(path, outHost, outPort);
        var osc_func = nil;

        if(func.isNil) {
            osc_func = OSCFunc({ |msg|
                if(debug) { msg.postln };
                // write to the bus
                bus.set(msg[1]);

                // use callback
                if(callback.notNil) {
                    callback.value(msg[1]);
                };

                // if there's value change - notify other clients
                if(value != msg[1]) {
                    value = msg[1];
                    this.notifyOthers(client_id);
                };
            }, path, nil, inPort);
            // make alive after Ctrl-.
            osc_func.permanent = true;
        };


        oscListeners.add(client_id -> (
            path: path,
            func: osc_func,
            addr: NetAddr(outHost, outPort)
        ));

        if(debug) {
            format("[Sp_OscControl] OSC client binded: (%)", client_id).postln;
        };
    }

    unbindAll {
        oscListeners.keysValuesDo { |id, v|
            var f = v[\func];
            f.free;
        };

        oscListeners.clear;
    }

    notifyOthers {
        arg except;
        oscListeners.keysDo { |id|
            if(except != id) { this.notify(id) }
        };
    }

    notifyAll {
        oscListeners.keysDo { |id| this.notify(id) };
    }

    notify {
        arg id;
        var v = oscListeners.at(id);

        try {
            if(v.notNil) {
                v[\addr].sendMsg(v[\path], value);
            };
        } { /*host not found */}
    }

    set {
        arg v;
        bus.set(v);
        if(callback.notNil) {callback.value(v)};
        value = v;
        this.notifyAll;
    }

    connected {
        "  Connected clients:".postln;
        oscListeners.keysDo { |id| ("    " ++ id).postln }
    }

    save {
        Archive.global.put(("OscControl" ++ name).asSymbol, value);
    }

    restore {
        var q = Archive.global.at(("OscControl" ++ name).asSymbol);
        this.set(q);
    }
}

Sp_OscControlReader : Sp_OscControl { // for stateless controls
    *new {
        arg name, server = Server.default;
        ^super.new(name, server);
    }

    notify {} // no notification sended, readonly
    save{}    // no state save
    restore{} //
}

Sp_OscSaveControl : Sp_OscControlReader {
    *new {
        arg name;
        ^super.new(format("/%/save", name));
    }

}

Sp_OscRestoreControl : Sp_OscControlReader {
    *new {
        arg name;
        ^super.new(format("/%/restore", name));
    }
}

Sp_OscControlWriter : Sp_OscControl {
    *new {
        arg name, server = Server.default;
        ^super.new(name, server);
    }

    bindOsc {
        arg path, inPort, outHost, outPort;
        ^super.bindOsc(path, inPort, outHost, outPort, {});
    }
}

Sp_OscControlVu : Sp_OscControlWriter {
    *new {
        arg name;
        ^super.new(format("/%/vu", name));
    }

    save{}    // no state save
    restore{}
}

Sp_OscControlGroup {
    var <>name;
    var <controls;

    // private
    var debug_;

    *new {
        arg name;
        ^super.new.init(name);
    }

    init {
        arg n;
        name = n;
        controls = Dictionary.new;
        debug_ = false;

        controls[\save] = Sp_OscSaveControl.new(name).callback_({this.save});
        controls[\restore] = Sp_OscRestoreControl.new(name).callback_({this.restore});
        controls[\vu] = Sp_OscControlVu.new(name);
    }

    bindOscApp {
        arg app;
        controls.do { |ctrl|
            ctrl.bindOscApp(app);
        }
    }

    bindMidiCC {
        arg values;
        values.do { |v, idx|
            if(idx >= controls.size) {^this};

            controls[idx].bindMidiCC(v);
        }
    }

    control {
        arg idx;
        ^controls.at(idx);
    }

    debug {
        arg value = true;
        debug_ = value;

        controls.do { |ctrl|
            ctrl.debug = value;
        }
    }

    add {
        arg idx;
        var ctrl = Sp_OscControl.new(idx);
        controls[idx] = ctrl;
        ^ctrl;
    }

    addn {
        arg ... names;
        names.do { |name|
            this.add(name);
        }
    }

    bus {
        arg idx;
        ^controls[idx].bus;
    }

    controlNames {
        ^controls.keys;
    }

    clear {
        controls.clear;
    }

    printOn {
        arg stream;
        stream << this.class.name << '(';
        stream << name.quote << " : [";
        stream << controls.keys.asList.join(",");
        stream << "])" << Char.nl;
    }

    connected {
        controls.do { |c|
            Post << "Control " << c.name.quote << ":" << Char.nl;
            c.connected;
        }
    }

    save {
        if(debug_) { format("[%] save", this.class.name).postln };

        controls.do { |c| c.save }
    }

    restore {
        if(debug_) { format("[%] restore", this.class.name).postln };
        controls.do { |c| c.restore }
    }

    set {
        arg idx, v;
        controls[idx].set(v);
    }

    setn {
        arg ... values;
        values.do { |v, idx|
            if(idx > controls.size) {^this};

            this.set(idx, v);
        }
    }

    setAll {
        arg v;
        controls.do { |c| c.set(v) }
    }
}

Sp_OscViolaIn : Sp_OscControlGroup {
    var <>channel;

    // private
    var osc_vu;

    *new {
        arg channel = 0;
        ^super.new("viola").initOscViolaIn(channel);
    }

    initOscViolaIn {
        arg chan;
        channel = chan;

        this.add("/viola/mute");
        this.add("/viola/level");
        this.add("/viola/compress");
        this.add("/viola/pass");
        this.add("/violaFull/rec");
        this.add("/violaFull/play");
    }

    mapSynthControls {
        arg synth;
        synth.synth_play.map(\amp, this.bus("/viola/level"));
        synth.synth_viola.map(\amp, this.bus("/viola/level"));

        controls["/viola/mute"].callback_({|m| synth.mute(m == 1)});
        controls["/viola/pass"].callback_({|m| synth.filter(m == 0)});
        controls["/viola/compress"].callback_({|m| synth.reverb(m == 1)});

        osc_vu = OSCFunc({|m| controls[\vu].set(m[3].ampdb + 100 / 100)}, '/violaIn/vu');

        this.restore();
    }
}


