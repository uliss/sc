GMix8 : CompositeView {
    var txt, i1, i2, i3, i4;

    *new {
        arg parent, bounds = Rect(0, 0, 400, 70);
        ^super.new(parent, bounds).init
    }

    init {
        i1 = LevelIndicator.new(super, Rect(0, 0, 10, 150));
        i2 = LevelIndicator.new(super, Rect(12, 0, 10, 150));
        i3 = LevelIndicator.new(super, Rect(24, 0, 10, 150));
        i4 = LevelIndicator.new(super, Rect(36, 0, 10, 150));

        // super.layout_ = HLayout(i1, i1);
    }
}

GMix : CompositeView {
    var <slider, level;

    *new {
        arg parent, bounds = Rect(0, 0, 40, 160), init = 0.0, title = "";
        ^super.new(parent, bounds).init(init, title);
    }

    init { |initVal, title|
        slider = Slider.new(this, Rect(0, 0, 15, 120)).thumbSize_(8);
        slider.value_(initVal);
        level = GLevel.new(this, Rect(15, 0, 30, 140), title);
        level.level.peakLevel_(0);
        level.level.value_(0);
    }

    value_ { |argVal|
        slider.value_(argVal);
    }

    valueAction_ { |argVal|
        slider.valueAction_(argVal);
    }

    value {
        ^slider.value;
    }

    peak_ { |argVal|
        level.level.peakLevel_(argVal);
    }

    rms_ { |argVal|
        level.level.value_(argVal);
    }

    vu_ { |rms, peak|
        this.rms_(rms);
        this.peak_(peak);
    }

    title_{ |title|
        level.title(title);
    }
}

GMixN : CompositeView {
    var mix_arr, osc_dest, connected;

    *new {
        arg parent, bounds = Rect(0, 0, 100, 160), number = 5, init = 0.0, oscPath = '/GMixN', oscPort = 10001;
        ^super.new(parent, bounds).init(number, init, oscPath, oscPort);
    }

    init { |num, initVal, oscPath, oscPort|
        osc_dest = NetAddr.new("127.0.0.1", oscPort);
        mix_arr = Array.new(num);

        num.do( { |i|
            mix_arr.add(GMix.new(this, title: "ch" ++ i));
            mix_arr[i].moveTo(i * 36, 0);
            mix_arr[i].value_(initVal);

            mix_arr[i].slider.action_({ |v|
                osc_dest.sendMsg(oscPath, i, v.value);
            });

           // send init via OSC
            osc_dest.sendMsg(oscPath, i, initVal);
        });

        super.resizeTo(num * 36, 160);

        this.onClose = {
            osc_dest.free;
        }
    }

    at { |idx|
        ^mix_arr[idx];
    }

    title_ { |idx, argTitle|
        mix_arr[idx].title_(argTitle);
    }

    rms_ { |idx, rms|
        mix_arr[idx].rms_(rms);
    }

    peak_ { |idx, peak|
        mix_arr[idx].peak_(peak);
    }

    value_ { |idx, value|
        mix_arr[idx].value_(value);
    }

    vu_ { |idx, rms, peak|
        mix_arr[idx].vu_(rms, peak);
    }
}
