SP_TaskRunner {
    var <currentTime;
    var taskDict;
    var playState;
    var timerTask;
    var <>onTimer;

    *new {
        ^super.new.init;
    }

    init {
        currentTime = 0;
        taskDict = Dictionary.new;
        playState = 0;
        timerTask = Task {
            loop {
                if(onTimer.notNil) { onTimer.value(currentTime) };
                this.runTasks;
                1.wait;
                currentTime = currentTime + 1;
            }
        };
    }

    currentTime_ {
        arg time;
        if(time.isKindOf(Float)) { time = time.asInteger };
        if(time.isKindOf(String)) { time = time.toSeconds };

        currentTime = time;
    }

    isPlaying { ^ playState == 1 }
    isStopped { ^ playState == 0 }
    isPaused  { ^ playState == 2 }

    play {
        if(playState == 1) { ^nil };

        if(this.isStopped) { timerTask.start };
        if(this.isPaused)  { timerTask.resume };

        playState = 1;
    }

    pause {
        if(playState == 2) { ^nil };

        if(this.isPlaying) { timerTask.pause };
        playState = 2;
    }

    stop {
        if(playState == 0) { ^nil };

        timerTask.stop;
        timerTask.reset;
        currentTime = 0;
        playState = 0;
    }

    addTask {
        arg time, func, name = \default, args;
        if(time.isKindOf(Float)) { time = time.asInteger };
        if(time.isKindOf(String)) { time = time.toSeconds };

        if(taskDict[time].isNil) { taskDict[time] = List.new(2) };
        taskDict[time].add(SP_Task.new(time, func, name, args));
    }

    removeTask {
        arg time;
        if(time.isKindOf(Float)) { time = time.asInteger };
        if(time.isKindOf(String)) { time = time.toSeconds };

        taskDict[time] = nil;
    }

    tasksAt {
        arg time;
        if(time.isKindOf(Float)) { time = time.asInteger };
        if(time.isKindOf(String)) { time = time.toSeconds };

        ^taskDict[time];
    }

    hasTaskAt {
        arg time;
        if(time.isKindOf(Float)) { time = time.asInteger };
        if(time.isKindOf(String)) { time = time.toSeconds };

        ^taskDict[time].notNil;
    }

    runTasks {
        var task_list = taskDict[currentTime];
        if(task_list.notNil) {
            task_list.do { |task| task.run }
        }
    }

    removeAllTasks {
        taskDict = Dictionary.new;
    }

    asList {
        ^taskDict.collect({|l|
            l.collect({|t| t.asString })
        }).asList.flatten.sort;
    }

    save {
        arg path;
        try {
            var f = File.new(path, "w");
            this.asList.do { |line|
                f.write(line);
                f.putChar(Char.nl);
            };
            f.close;
        }
        { |err|
            err.what;
            ^nil;
        }
    }

    load {
        arg path, actionResolver;
        var f;

        if(path.pathExists === false) { ^nil };

        f = File.new(path, "r");
        protect {
            f.readAllString.split(Char.nl)
            .collect({|l| l.trim }) // trim all whitespaces
            .reject({|l| l.isEmpty })  // skip empty lines
            .reject({|l| l[0] == $# }) // skip comment
            .do { |ln|
                var time, name, args, action, lst;
                lst = ln.split($ );
                time = lst[0];
                name = lst[1];
                args = lst[2..];

                if(actionResolver.notNil) {
                    action = actionResolver.value(name.asSymbol);
                } {
                    action = {};
                };

                if(action.notNil) {
                    this.addTask(time, action, name, args);
                };
            };
        }
        {
            f.close;
        }
    }
}


SP_Task {
    var <>time;
    var <>action;
    var <>name;
    var <>args;

    *new {
        arg time, action, name = \default, args = List.new(2);
        ^super.newCopyArgs(time, action, name, args);
    }

    *newFromString {
        arg string;
        var time, name, args, data;

        data = string.split($ );
        if(data.size < 2) { ^nil };

        time = data[0].toSeconds;
        name = data[1];
        args = data[2..];
        ^this.new(time, nil, name, args);
    }

    run {
        if(action.notNil) { action.value(time, *args) };
    }

    asString {
        var str = "% %".format(time.asTimeString.drop(-4), name);
        if(args.isEmpty.not) { str = str + args.join(" ")};
        ^str;
    }
}
