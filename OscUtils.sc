OscBroker {
    var vals;
    var midi_func;

    *new {
        ^super.new.init;
    }

    init {
        vals = Dictionary.new();
        midi_func = Dictionary.new();
    }

    value {
        arg id;
        var v = vals.at(id);

        if(v == nil) {
            format("OscValue not found: %", id).postln;
        };

        ^v;
    }

    setValue { |valueId, v|
        var v_ = this.value(valueId);
        if(v_ != nil) {
            v_.value_(v);
        }
    }

    asFloat { |valueId|
        var v_ = this.value(valueId);
        if(v_ != nil) {
            ^v_.value;
        }
    }

    addValue {
        arg path, port = 5000, func = {};
        var osc_val = OscValue.new.init(path, port);
        osc_val.func_(func);
        vals.add(path -> osc_val);
    }

    bindMidiCC {
        arg valueId, ccNum;
        var v = this.value(valueId);

        if(v != nil) {
            var m = MIDIFunc.cc({
                arg val;// num, chan, src;
                v.value_(val / 127);
            }, ccNum);

            midi_func.add(valueId -> m);
        };
    }

    bindGUI {
        arg valueId, clientId, view;
        var v = this.value(valueId);

        if(v != nil) {
            view.action_({ |gView|
                var n = NetAddr("127.0.0.1", v.oscPort);
                n.sendMsg(valueId, clientId, gView.value);
            });
        };
    }

    subscribe {
        arg valueId, clientId, clientAddr, oscPath;
        var v = this.value(valueId);

        if(v != nil) {
            v.subscribe(clientId, clientAddr, oscPath);
        };
    }

    subscribeGUI {
        arg valueId, clientId, view;
        var v = this.value(valueId);

        if(v != nil) {
            v.subscribeGUI(clientId, view);
        };
    }
}

OscValue {
    var value;
    var osc;
    var osc_port;
    var listeners;
    var gui_listeners;
    var func;

    *new {
        ^(super.new).init();
    }

    init {
        arg path, port = 5000;
        listeners = Dictionary.new;
        gui_listeners = Dictionary.new;
        osc = OSCFunc({
            arg msg, time, addr;

            if((msg.size < 3), {
                "[OscValue ERROR] invalid message format: '/sample/path ID VALUE'".postln;
            }, {
                value = msg[2];
                func.value(this);
                this.notifyOthers(msg[1]);
            });
        }, path, nil, port);

        osc_port = port;

        func = {};
        ^this;
    }

    value_ { |val|
        value = val;
        func.value(this);
        this.notifyAll;
    }

    oscPort {
        ^osc_port;
    }

    value {
        ^value;
    }

    func_{ |f|
        func = f;
    }

    subscribe {
        arg id, netaddr, path;
        listeners.add(id -> [netaddr, path]);
        format("[OscValue] Network client '%' subscribed (osc://%:%%)\n", id, netaddr.ip, netaddr.port, path).post;
    }

    subscribeGUI {
        arg id, view;
        gui_listeners.add(id -> view);
        format("[OscValue] GUI client '%' subscribed\n", id).post;
    }

    notifyAll {
        listeners.keysDo{ |id| this.notify(id)};
        gui_listeners.keysDo{ |id| this.notifyGUI(id)};
    }

    notifyOthers {
        arg except;
        listeners.keysDo { |id| if(except != id) { this.notify(id) }};
        gui_listeners.keysDo { |id| if(except != id) { this.notifyGUI(id) }};
    }

    notifyGUI {
        arg id;
        var gv = gui_listeners.at(id);
        if((gv != nil), {
            {gv.value_(value[0])}.defer;
        }, { format("[OscValue ERROR] gui listener not found: '%'", id).postln});
    }

    notify {
        arg id;
        var v = listeners.at(id);
        if(v.notNil, {
            if(value.notNil, {
                if(value.isKindOf(SequenceableCollection),
                    { v[0].sendMsg(v[1], value.join(' ')) },
                    { v[0].sendMsg(v[1], value.asString)});

                // format("send to [%]: osc://%:%% %", id, v[0].ip, v[0].port, v[1], value).postln;
            }, {
                "Nil value".postln;
            });
        }, {
            format("[OscValue ERROR] listener not found: '%'", id).postln;
        });
    }
}

OscVUDispatcher : OscBroker {
    var broker;
    var num;

    *new {
        arg n = 2, port = 5000;
        ^super.new.init(n, port);
    }

    init {
        arg n, port;
        broker = OscBroker.new;
        num = n;

        num.do { |i|
            broker.addValue(format("/sp/vu/ch%", i), port);
        }
    }

    subscribe {
        arg host = "127.0.0.1", port = 3000, path = "/sp/vu/ch%";
        num.do { |i|
            broker.subscribe(format(path, i), "vu" + host, NetAddr(host, port), format(path, i));
        }
    }
}

OscServerStat {
    var <>timeout;
    var <>inPort, <>inPath;
    var <>outAddr, <>outPath;
    var oscControl;

    var send_task;

    *new {
        arg timeout = 5, out_addr = NetAddr("127.0.0.1", 5000), out_path = "/sc/stat", in_port = 5001, in_path = "/nodejs/stat/control";
        ^super.new.init(timeout, out_addr, out_path, in_port, in_path);
    }

    *pollMIDI {
        MIDIClient.prInitClient;
        MIDIClient.list;
        MIDIClient.disposeClient;
    }

    init { |t, out_addr, out_path, in_port, in_path|
        timeout = t;
        inPort = in_port;
        inPath = in_path;
        outAddr = out_addr;
        outPath = out_path;

        oscControl = OSCFunc({|msg|
            // msg.postln;
            switch(msg[1],
                1, { this.start },
                0, { this.stop  }
            );
        }, inPath, nil, inPort);

        send_task = Task({
            inf.do {
                var d, json, opt = Server.default.options;
                OscServerStat.pollMIDI;
                d = Dictionary[
                    "runningServers" -> Server.allRunningServers,
                    "peakCPU" -> Server.default.peakCPU,
                    "avgCPU" -> Server.default.avgCPU,
                    "midiDevices" -> MIDIClient.sources.collect({|v| v.device}),
                    "audioDevices" -> ServerOptions.devices,
                    "serverOptions" -> Dictionary[
                        "device" -> opt.device,
                        "sampleRate" -> Server.default.sampleRate,
                        "blockSize" -> opt.blockSize,
                        "hardwareBufferSize" -> opt.hardwareBufferSize,
                        "numInputBusChannels" -> opt.numInputBusChannels,
                        "numOutputBusChannels" -> opt.numOutputBusChannels
                    ]
                ];

                json = JSON.convert(d);
                // json.postln;

                outAddr.sendMsg(outPath, json);
                timeout.wait;
            }
        });
    }

    start {
        // "Start sending server stat info".postln;
        send_task.play;
    }

    pause {
        send_task.pause;
    }

    stop {
        // "Stop sending server stat info".postln;
        send_task.stop;
    }
}

OscSpeakerTest {
    var <numChannels;
    var <inPort, <inPath;
    var oscControl;
    var sounds;

    *new {
        arg num_channels = 2, in_port = 5001, in_path = "/nodejs/speaker/control";
        ^super.new.init(num_channels, in_port, in_path);
    }

    init {
        arg num_channels, in_port, in_path;
        numChannels = num_channels;
        inPort = in_port;
        inPath = in_path;
        sounds = Array.newClear(numChannels);

        oscControl = OSCFunc({ |msg|
            var id = msg[1], action = msg[2];
            // msg.postln;
            if(id >= 0 && id < numChannels) {
                switch(action,
                    1, {this.play(id)},
                    0, {this.stop(id)}
                );
            };
        }, inPath, nil, inPort);
    }

    play {
        arg idx;
        var defName = "speaker_test_" ++ idx;
        var freqs = [0, 4, 7, 11, 14, 17, 21, 24] + 60;

        Routine({
            if(SynthDescLib.global.synthDescs.at(defName).isNil) {
                SynthDef(defName, { |bus = 0|
                    var snd = SinOsc.ar(freqs[idx].midicps) * EnvGate.new;
                    snd = snd * EnvGen.ar(Env.perc, levelScale: 0.5, levelBias: 0.2);
                    Out.ar(bus, snd);
                }).add;

                0.1.wait;
                if(sounds[idx].isNil) {
                    sounds[idx] = Synth(defName, [\bus, idx]);
                }
            };
        }).play;
    }

    stop {
        arg idx = nil;
        if(idx.isNil, {
            this.stopAll;
        }, {
            if(sounds[idx].notNil) {
                sounds[idx].release(0.4);
                sounds[idx] = nil;
            }
        });
    }

    stopAll {
        numChannels.do {
            arg i;
            sounds[i].release(0);
            sounds[i] = nil;
        }
    }
}
