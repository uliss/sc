// {SinOsc.ar(SinOsc.kr(2, 0, 100, 400))}.play;

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
                v.value_([val / 127]);
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
                value = msg[2..];
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
