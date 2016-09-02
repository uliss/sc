GuidoConnectionManager : GuidoAbstractModule {
    var <>onConnect;
    var <>onDisconnect;

    *new {
        ^super.new("/guido/module/manager");
    }

    processOsc { |msg|
        switch(msg[1].asSymbol,
            \connected, { if(onConnect.notNil) { onConnect.value(msg[2..]) } },
            \disconnected, { if(onDisconnect.notNil) { onDisconnect.value(msg[2..]) } }
        )
    }

    list {
        arg func;
        this.getValue("list", func);
    }
}