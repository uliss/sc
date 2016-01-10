Sp_AppOSC {
    var <inPort;
    var <outHost;
    var <outPort;
    var <prefix;

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

    appendFloatIndicator {
        arg path;
        var n = NetAddr(outHost, outPort);
        var f = OSCFunc({ |m|
            var out_path = path ++ "-ind";
            n.sendMsg(out_path, m[1].round(0.01));
        }, path, nil, inPort);
    }

    initTouchOsc {

    }
}

Sp_OscAbstractClient {
    var path;
    var port;

    *new {
        arg oscPath, oscPort;
        ^super.new.init(oscPath, oscPort);
    }

    init {
        arg osc_path, osc_port;
        path = osc_path;
        port = osc_port;
    }

    // do no remove!!!
    clear {}
    send {}
    id { ^format("%:%", path, port) }
}

Sp_OscListener : Sp_OscAbstractClient {
    var <>func;
    var oscFunc;

    *new {
        arg oscPath, inPort, func;
        ^super.new(oscPath, inPort).initListener(func);
    }

    initListener {
        arg func_;
        func = func_;
        oscFunc = OSCFunc({ func.value }, path, nil, port);
    }

    clear {
        oscFunc.free;
    }
}

Sp_OscSender : Sp_OscAbstractClient {
    // private
    var addr;
    var host;

    *new {
        arg path, host, port;
        ^super.new(path, port).initSender(host, port);
    }

    initSender {
        arg host_, port_;
        host = host_;
        addr = NetAddr(host, port);
    }

    id {
        ^format("osc://%:%%", host, port, path);
    }

    send {
        arg msg;
        try { addr.sendMsg(path, msg) } {};
    }
}

Sp_OscClientDuplex : Sp_OscAbstractClient {
    var <>listener;
    var sender;

    *new {
        arg path, inPort, outHost, outPort, oscFunc;
        ^super.new(path, inPort).initDuplex(path, inPort, outHost, outPort, oscFunc);
    }

    initDuplex {
        arg p, in_port, host, out_port, osc_func;
        path = p;
        listener = Sp_OscListener(p, in_port, osc_func);
        sender = Sp_OscSender(p, host, out_port);
    }

    clear {
        listener.clear;
    }

    id {
        ^sender.id;
    }

    send {
        arg msg;
        sender.send(msg);
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

    addClient {
        arg c;
        oscListeners[c.id] = c;

        if(debug) {
            format("[Sp_OscControl] OSC control binded: (%)", c.id).postln;
        };
    }

    bindMidiCC {
        arg ccNum, chan = nil, func = linlin(_, 0, 127, 0, 1);
        midiFunc = MIDIFunc.cc({ |v|
            this.set(func.value(v).lag(0.05));
        }, ccNum, chan);
    }

    bindOscApp {
        arg app;
        this.bindOsc(app.path(name), app.inPort, app.outHost, app.outPort);
    }

    bindOscIndicator {
        arg path, outHost, outPort;
        var client = Sp_OscSender.new(path, outHost, outPort);
        this.addClient(client);
    }

    bindOsc {
        arg path, inPort, outHost, outPort;
        var client = Sp_OscClientDuplex(path, inPort, outHost, outPort, {});
        var client_id = client.id;

        var osc_func = OSCFunc({ |msg|
            if(debug) { msg.postln };
            // write to the bus
            bus.set(msg[1]);

            // fire callback
            if(callback.notNil) {
                callback.value(msg[1]);
            };

            // if there's value change - notify other clients
            if(value != msg[1]) {
                value = msg[1];
                this.notifyOthers(client_id);
            };
        }, path, NetAddr(outHost), inPort);
        // make alive after Ctrl-.
        osc_func.permanent = true;

        client.listener.func = osc_func;
        this.addClient(client);
    }

    unbindAll {
        oscListeners.keysValuesDo { |id, c|
            c.clear
        };

        oscListeners.clear;
    }

    notifyOthers {
        arg except;
        oscListeners.do { |c|
            if(c.id != except) {
                // format("% - notify other: %", except, c.id).postln;
                c.send(value)
            }
        };
    }

    notifyAll {
        oscListeners.do { |c| c.send(value) };
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
        this.set(value);
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
        ^super.bindOscIndicator(path, outHost, outPort);
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

Sp_OscTestTone : Sp_OscControlGroup {
    *new {
        ^super.new("testTone").initTone;
    }

    initTone {
        this.add("/testTone/freq");
        this.add("/testTone/level");
        this.add("/testTone/mute");
        this.add("/testTone/pan");
    }

    mapSynthControls {
        arg synth;
        synth.map(\mute, this.bus("/testTone/mute"));
        synth.map(\amp, this.bus("/testTone/level"));
        synth.map(\pan, this.bus("/testTone/pan"));
        synth.map(\freq, this.bus("/testTone/freq"));
    }
}


