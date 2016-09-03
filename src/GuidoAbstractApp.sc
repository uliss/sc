GuidoAbstractApp {
    var <oscPath;
    var <httpPath;
    var <>onConnect;

    *new {
        arg oscPath, httpPath, syncOnConnect = false;
        ^super.newCopyArgs(oscPath, httpPath).init(syncOnConnect);
    }

    *hasSync {
        arg url;
        ^Library.at(\guido, \app, \sync, url.asSymbol).notNil;
    }

    *registerSync {
        arg name, func;
        NodeJS.sendMsg("/guido/module/server", "sync_add", name);

        Library.at(\guido, \app, \sync, name.asSymbol) !? ( _.free );
        Library.put(\guido, \app, \sync, name.asSymbol, func);
    }

    *unregisterSync {
        arg name;
        NodeJS.sendMsg("/guido/module/server", "sync_remove", name);
        Library.at(\guido, \app, \sync, name.asSymbol) !? ( _.free );
        Library.global.removeAt(\guido, \app, \sync, name.asSymbol);
    }

    name {
        ^oscPath.basename;
    }

    init {
        arg syncOnConnect;

        if(syncOnConnect) {
            var fn = OSCFunc({|msg|
                {
                    "[%:%] sync on connection".format(this.class, this.identityHash).postln;
                    this.sync;
                    if(onConnect.notNil) { onConnect.value };
                }.defer(2);
            }, "/guido/sync" +/+ httpPath, nil, NodeJS.outOscPort);

            GuidoAbstractApp.registerSync(httpPath, fn);
        }
    }

    open {
        ("http://localhost:" ++  NodeJS.httpPort ++ httpPath).openOS;
    }

    sendMsg {
        arg path ... args;
        NodeJS.send2Cli(oscPath ++ path, *args);
    }

    sync {}

    css {
        arg k, v;
        this.sendMsg("/css", k, v);
    }

    free {
        GuidoAbstractApp.unregisterSync(httpPath);
    }

    title_ {
        arg pageTitle, windowTitle;
        if(windowTitle.notNil) { // set both
            NodeJS.send2Cli("/guido/module/client/broadcast", "title", pageTitle.urlEncode, windowTitle.urlEncode);
        } {
            NodeJS.send2Cli("/guido/module/client/broadcast", "title", pageTitle.urlEncode);
        };
    }
}
