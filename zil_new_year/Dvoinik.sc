DvoinikScene : SynthScene {
    *new {
        arg param = [];
        ^super.new("Dvoinik", "/dvoinik", synthName: \dvoinik, synthParam: param).initDv;
    }

    initDv {
        task = Task {
            inf.do { |i|
                this.dbg(i.asTimeString[..7]);
                1.wait;
            }
        };
    }

    start {
        arg ... args;
        super.start(*args);
        task.reset;
        task.play;
    }

    stop {
        super.stop;
        task.stop;
    }

    pause {
        super.pause;
        task.pause;
    }

    resume {
        super.resume;
        task.resume;
    }

    reset {
        task.reset;
    }

    release { |value|
        super.release(value);
        {task.stop}.defer(value);
    }
}
