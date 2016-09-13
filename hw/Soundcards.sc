SoundCard {
    classvar <>defaultCard;

    var <name;
    var <bufSize;
    var <memSize;
    var <numOutCh;
    var <numInCh;
    var <server;

    *saffire {
        arg bufSize = 256, memSize = (2**10) * 100 /* 100Mb*/, inCh = 4, outCh = 6;
        ^super.new.init("Saffire", bufSize, memSize, inCh, outCh, "127.0.0.1", 58009);
    }

    *focuwrite { this.saffire }

    *mbox {
        arg bufSize = 256, memSize = (2**10) * 100 /* 100Mb*/, inCh = 6, outCh = 6;
        ^super.new.init("Mbox Pro", bufSize, memSize, inCh, outCh, "127.0.0.1", 58010);
    }

    *builtin {
        arg bufSize = 512, memSize = (2**10) * 100 /* 100Mb*/, inCh = 2, outCh = 2;
        ^super.new.init("BuiltIn", bufSize, memSize, inCh, outCh, "127.0.0.1", 58011);
    }

    *presonus {
        arg bufSize = 256, memSize = (2**10) * 100 /* 100Mb*/, inCh = 6, outCh = 4;
        ^super.new.init("PreSonus FireStudio", bufSize, memSize, inCh, outCh, "127.0.0.1", 58012);
    }

    *soundflower {
        arg bufSize = 512, memSize = (2**10) * 100 /* 100Mb*/, inCh = 2, outCh = 16;
        ^super.new.init("Soundflower (64ch)", bufSize, memSize, inCh, outCh, "127.0.0.1", 58011);
    }

    *default {
        if(SoundCard.defaultCard.isNil) { SoundCard.defaultCard = \builtin };

        switch(SoundCard.defaultCard,
            \builtin, {^SoundCard.builtin},
            \mbox,    {^SoundCard.mbox},
            \saffire, {^SoundCard.saffire},
            \soundflower, {^SoundCard.soundflower},
            {^nil}
        )
    }

    init {
        arg sName, bufsize, memsize, inCh, outCh, host, port;
        var options;

        name = sName;
        bufSize = bufsize;
        memSize = memsize;
        numInCh = inCh;
        numOutCh = outCh;

        options = ServerOptions.new.device_(name)
        .memSize_(memSize).hardwareBufferSize_(bufSize)
        .numInputBusChannels_(numInCh)
        .numOutputBusChannels_(numOutCh);


        server = Server.new(name, NetAddr(host, port), options);
        server.makeWindow;
        server.window.alwaysOnTop = true;
        Server.default_(server);
        ^this;
    }

    printOn {
        arg stream;
        var str = format("SoundCard % (in: %, out: %, buf=%, mem=%)",
            name.quote, numInCh, numOutCh, bufSize, memSize);
        stream << str;
    }

    options {
        ^server.options;
    }
}

