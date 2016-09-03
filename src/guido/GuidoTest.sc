GuidoTest : SP_Test {
    var osc_args;
    classvar <>debugLevel;

    listen {
        msg_chain.add("listen");
    }

    *initClass {
        debugLevel = "error";
    }

    *run {
        NodeJS.start({super.run}, debugLevel ?? "debug");
    }

    before {
        NodeJS.sendCallback = {
            arg ... args;
            osc_args = args;
        }
    }

    beforeEach {
        osc_args = nil
    }

    after {
        // NodeJS.stop;
        NodeJS.sendCallback = nil;
    }

    sendOSC_ {
        arg ...args;
        expect_result = (osc_args == args);
        this.assertResult("send OSC message: %, but % was send".format(args, osc_args));
    }

    osc_ {
        arg path;
        expect_result = AbstractResponderFunc.allFuncProxies["OSC unmatched".asSymbol].select({|fn| fn.path.asSymbol == path.asSymbol}).isEmpty.not;
        this.assertResult("OSC addr:" + path);
    }

    oscListeners {
        ^AbstractResponderFunc.allFuncProxies["OSC unmatched".asSymbol].collect({|fn| fn.path}).asSet;
    }

    numOscListeners {
        ^ this.oscListeners.size;
    }

    sendOSC {
        arg path ... args;
        NodeJS.sendMsg(path, *args);
    }

    // emulate incoming message
    receiveOSC {
        arg path ... args;
        AbstractResponderFunc.allFuncProxies["OSC unmatched".asSymbol].select({|fn| fn.path.asSymbol == path.asSymbol}).do { |osc_fn|
            var fn_args = List.new;
            osc_fn.func.value([path] ++ args);
        }
    }
}
