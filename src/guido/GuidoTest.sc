GuidoTest : SP_Test {
    var osc_args;
    classvar <>debugLevel;

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

    after {
        // NodeJS.stop;
        NodeJS.sendCallback = nil;
    }

    sendOsc_ {
        arg ...args;
        this.equal_(args, osc_args);
    }
}