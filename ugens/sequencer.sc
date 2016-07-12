Sp_Sequencer {
    var <>path;
    var currentLine;
    var lines;
    var actions;
    var routine;
    var <>tempo;

    *new { |path, startLine = 0|
        ^super.new.init(path, startLine);
    }

    bind {
        arg key, act;
        actions[key] = act;
    }

    bindClear {
        actions.clear;
    }

    init {
        arg path, startLine = 0;
        currentLine = 0;
        tempo = 60;
        actions = Dictionary.new();
        this.path = path;
        lines = FileReader.read(path, true, startRow: startLine);
        this.resetRoutine;

        actions["tempo"] = {|d| this.tempo = d[\tempo]};
        ^this;
    }

    resetRoutine { |start = 0|
        if(routine.notNil) {
            routine.free;
        };

        routine = Routine{
            var dict;

            currentLine = start;
            if(currentLine >= lines.size) {
                "Sp_Sequencer: invalid line number = %".format(currentLine).error;
                ^nil;
            };

            dict = this.next;
            while ({dict.notNil}, {
                dict.keysValuesDo {|k, v|
                    if(actions[k].notNil) {
                        actions[k].value(dict);
                    };
                };
                dict = this.next;
            });
        };
    }

    next {
        var dict, beat;
        dict = Dictionary.newFrom(lines[currentLine][1..].asList);
        currentLine = currentLine + 1;

        if(currentLine < lines.size) { ^dict } { ^nil }
    }

    play {
        arg line = 0;
        var dict;

        routine.stop;
        this.resetRoutine(line);
        routine.play;
        ^this;
    }

    stop {
       routine.stop;
        ^this;
    }
}
