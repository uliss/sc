DvoinikScene : SynthScene {
    var person1, person2;

    *new {
        arg param = [], kinectPerson1, kinectPerson2;
        ^super.new("Dvoinik", "/dvoinik", synthName: \dvoinik, synthParam: param).initDv(kinectPerson1, kinectPerson2);
    }

    initDv {
        arg p1, p2;
        person1 = p1;
        person2 = p2;

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
