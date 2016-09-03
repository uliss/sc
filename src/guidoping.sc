GuidoPing : GuidoAbstractModule {
    *new {
        ^super.new("/guido/ping");
    }

    processOsc { |msg|
        NodeJS.send2Cli(oscPath, *msg[1..]);
    }
}
