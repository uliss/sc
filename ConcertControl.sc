ConcertControl {
    var <place, <date;
    var parts, <oscPort;
    var osc_out_addr;
    var osc_in;

    *new {
        arg place, date, oscPort = NodeJS.outOscPort;
        ^super.new.init(place, date, oscPort);
    }

    init {
        arg place_, date_, oscPort_;
        place = place_;
        date = date_;
        oscPort = oscPort_;

        parts = List.new;

        osc_in = OSCFunc({
            this.sendInfo;
        }, "/concert/info/get", nil, oscPort_);

        osc_out_addr = NetAddr("localhost", NodeJS.inOscPort);
        this.sendInfo
    }

    sendInfo {
        var date_fmt = date.format("%e %B %Y").asString;
        var data = Dictionary.new;
        data.put(\place, place);
        data.put(\date, date_fmt);
        osc_out_addr.sendMsg("/sc/concert/info", JSON.convert(data));

        parts.do {
            arg entry;
            osc_out_addr.sendMsg("/sc/concert/add", JSON.convert(entry));
        };
    }

    add {
        arg composer, title, id, f_synth, oscFunc = nil;
        var entry, osc_f;
        var synth_n;

        if(oscFunc.isNil, {
            osc_f = OSCFunc({
                arg msg;
                msg.postln;
                switch(msg[1],
                    \start, {
                        synth_n = f_synth.value;
                        synth_n.run(true)
                    },
                    \stop, {
                        synth_n.free;
                    },
                    \pause, {synth_n.run(false)},
                    { format("[ConcertControl] unknown action: '%'").postln; }
                );

            }, "/concert/" ++ id, nil, oscPort);
        }, {
            osc_f = oscFunc;
        } );


        entry = Dictionary.new;
        entry.put(\composer, composer);
        entry.put(\title, title);
        entry.put(\f_synth, f_synth);
        entry.put(\synth, synth_n);
        entry.put(\osc, osc_f);
        entry.put(\id, id);

        // remove old entries
        parts = parts.reject({arg item; item[\id] == id;});
        parts.add(entry);
    }

    play {
        arg id;
        var synth;

        parts.do { |entry|
            if(entry[\id] == id) {
                entry[\synth] = entry[\f_synth].value;
            };
        }
    }

    pause {
        arg id;
        var synth;

        parts.do { |entry|
            if(entry[\id] == id) {
                entry[\synth].run(false);
            };
        }
    }

    release {
        arg id, tm = 1;
        var synth;

        parts.do { |entry|
            if(entry[\id] == id) {
                entry[\synth].set(\fadeTime, tm);
                entry[\synth].release;
            };
        }
    }

    stop {
        arg id;
        var synth;

        parts.do { |entry|
            if(entry[\id] == id) {
                entry[\synth].free;
            };
        }
    }

    list {
        parts.do { |entry|
            entry.postln;
        }
    }
}
