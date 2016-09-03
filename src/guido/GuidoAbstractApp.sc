GuidoAbstractApp : GuidoAbstractModule {
    var <httpPath;
    var <>onConnect;

    *new {
        arg oscPath, httpPath, syncOnConnect = false;
        ^super.new(oscPath).initApp(httpPath, syncOnConnect);
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

    initApp {
        arg path, syncOnConnect;

        httpPath = path;

        if(syncOnConnect) {
            var fn = OSCFunc({|msg|
                {
                    "[%:%] sync on connection".format(this.class, this.identityHash).postln;
                    this.sync;
                    if(onConnect.notNil) { onConnect.value };
                }.defer(2);
            }, "/guido/sync" +/+ httpPath, nil, NodeJS.outOscPort);
            // set permanent
            fn.permanent = true;

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
        super.free;
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
