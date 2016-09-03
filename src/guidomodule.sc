GuidoAbstractModule {
    var <oscPath;
    var oscf;

    *new {
        arg oscPath;
        ^super.newCopyArgs(oscPath).init;
    }

    init {
        oscf = NodeJS.on(oscPath, { |m| this.processOsc(m) });
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
        NodeJS.on(oscPath, { |m|
            if(m[1].asString == name.asString) {
                func.value(m[2..])
            };
        }).oneShot;
        NodeJS.sendMsg(oscPath, name, ":back")
    }
}
