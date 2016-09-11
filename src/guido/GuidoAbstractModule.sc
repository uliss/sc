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
        this.addFunction(\print, { arg ... args; args.join(" ").postln });
    }

    function { |name| ^fn_map[name.asSymbol] }
    hasFunction { |name| ^this.function(name).notNil }
    addFunction { |name, func| fn_map[name.asSymbol] = func }
    removeFunction { |name| fn_map[name.asSymbol] = nil }
    removeAllFunctions { fn_map = Dictionary.new }
    functionList { ^ fn_map.keys.asArray.sort }
    callFunction {
        arg name ... args;
        var fn = fn_map[name.asSymbol];
        if(fn.notNil) { ^ fn.value(*args) } {
            "[%] unknown named function: %".format(this.class, name).warn;
            ^nil;
        };
    }

    processOsc {
        arg msg;
        var fn, fn_name = msg[1];
        if(fn_name.isNil) { ^nil };
        this.callFunction(fn_name, *msg[2..]);
    }

    start { oscf.enable }
    stop { oscf.disable }
    free {
        oscf.free;
        this.removeAllFunctions;
    }

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
