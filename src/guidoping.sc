GuidoPing : GuidoAbstractModule {
    *new {
        ^super.new("/guido/ping");
    }

    processOsc { |msg|
        NodeJS.send2Cli(path, *msg[1..]);
    }
}