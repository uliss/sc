GuidoAbstractModule {
    var <oscPath;
    var oscf;
    var fn_map;

    *new {
        arg oscPath;
        ^super.newCopyArgs(oscPath).init;
    }

    init {
        oscf = NodeJS.on(oscPath, { |m| this.processOsc(m) });
        oscf.permanent = true;
        fn_map = Dictionary.new;
    }

    addNamedFunction { |name, func| fn_map[name.asSymbol] = func }
    removeNamedFunction { |name| fn_map[name.asSymbol] = nil }
    removeAllNamedFunction { fn_map = Dictionary.new }
    namedFunction { |name| ^fn_map[name.asSymbol] }

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
