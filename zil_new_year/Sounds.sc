SoundLib {
    var paths_;
    var buffers_;

    *new {
        ^super.new.init
    }

    init {
        paths_ = Dictionary.new;
        buffers_ = Dictionary.new;

        this.add("schubert", "/Users/serj/work/music/sounds/Schubert Doppelganger.wav");
        this.add("paper1", "/Users/serj/work/music/sounds/cook/paper-crumble-1.wav");
        this.add("paper2", "/Users/serj/work/music/sounds/cook/paper-crumble-2.wav");
        this.add("paper3", "/Users/serj/work/music/sounds/cook/paper-crumble-3.wav");
        this.add("paper4", "/Users/serj/work/music/sounds/cook/paper-crinkle.wav");
        this.add("paper5", "/Users/serj/work/music/sounds/cook/paper-1.wav");
        this.add("rice1", "/Users/serj/work/music/sounds/cook/rice-1.wav");
        this.add("onion1", "/Users/serj/work/music/sounds/cook/onion-cut-3.wav");
        this.add("onion2", "/Users/serj/work/music/sounds/cook/onion-cut-2.wav");
        this.add("metal1", "/Users/serj/work/music/sounds/cook/metal-1.wav");
        this.add("metal2", "/Users/serj/work/music/sounds/cook/metal-2.wav");
        this.add("microwave1", "/Users/serj/work/music/sounds/cook/microwave-1.wav");
        this.add("microwave2", "/Users/serj/work/music/sounds/cook/microwave-2.wav");
        this.add("jingle1", "/Users/serj/work/music/sounds/new year/jingle-1.wav");
        this.add("jingle2", "/Users/serj/work/music/sounds/new year/jingle-2.wav");
        this.add("jingle3", "/Users/serj/work/music/sounds/new year/jingle-3.wav");
        this.add("jingle4", "/Users/serj/work/music/sounds/new year/jingle-crosby.wav");
        this.add("drazhe", "/Users/serj/work/music/sounds/new year/fei-drazhe-mono.wav");
        this.add("kuranty1", "/Users/serj/work/music/sounds/new year/kuranty-1.wav");
        this.add("kuranty2", "/Users/serj/work/music/sounds/new year/kuranty-2.wav");
        this.add("step-snow1", "/Users/serj/work/music/sounds/new year/step-snow.wav");
    }

    add {
        arg name, path;
        paths_.put(name, path);
        buffers_.put(name, nil);
    }

    buffer {
        arg name;
        ^buffers_[name];
    }

    loadAll {
        paths_.keysDo { |k|
            this.load(k);
        }
    }

    load {
        arg name;
        var path = paths_[name];
        if(path.notNil,
            {
                var b = Buffer.readChannel(Server.default, path);
                buffers_.put(name, b);
                ^b;
            },
            {
                format("file '%' not found", name).postln;
                ^nil;
            }
        );
    }

    play {
        arg name, loop = false, mul = 1;
        var b = buffers_[name];
        if(b.notNil,
            {
                ^b.play(loop, mul);
            },
            {
                format("name '%' not loaded", name).postln;
                ^nil;
            }
        );
    }

    listAll {
        paths_.keysDo { |k|
            k.postln;
        }
    }
}


