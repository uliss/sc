GuidoAbstractModule {
    var <path;
    var oscf;

    *new {
        arg path;
        ^super.new.init(path)
    }

    init {
        arg p;
        path = p;
        oscf = NodeJS.on(path, { |m| this.processOsc(m) });
    }

    processOsc {
        arg msg;
        "[%] sould be implemeneted".format(thisMethod);
    }

    start { oscf.enable }
    stop { oscf.disable }
    free { oscf.free }

    getValue {
        arg name, func;
        NodeJS.on(path, { |m|
            if(m[1].asString == name.asString) {
                func.value(m[2..])
            };
        }).oneShot;
        NodeJS.sendMsg(path, name, ":back")
    }
}