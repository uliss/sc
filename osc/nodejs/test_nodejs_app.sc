TestSP_AbstractApp : UnitTest {
    var osc_msg;

    before {
        NodeJS.connected = true;
    }

    after {
        NodeJS.sendCallback = nil;
    }

    beforeEach {
        osc_msg = [];

        NodeJS.sendCallback = {|m|
            osc_msg = m;
        };
    }

    test_SyncRegistration {
        this.assert(SP_AbstractApp.hasSync("test").not);

        SP_AbstractApp.registerSync("test", 1);
        this.assert(SP_AbstractApp.hasSync("test"));
        this.assertEquals(osc_msg, ["/guido/module/server", "sync_add", "test"], "OSC send msg");

        SP_AbstractApp.unregisterSync("test");
        this.assert(SP_AbstractApp.hasSync("test").not);
    }

    test_New {
        var app = SP_AbstractApp.new("/osc", "/http");
        this.assert(app.oscPath == "/osc");
        this.assert(app.httpPath == "/http");
        this.assert(SP_AbstractApp.hasSync("/http").not);
        app.free;
    }

    test_NewSynced {
        var app = SP_AbstractApp.new("/osc", "/http", true);
        this.assert(app.oscPath == "/osc");
        this.assert(app.httpPath == "/http");
        this.assert(SP_AbstractApp.hasSync("/http"), "new sync added");
        app.free;
        this.assert(SP_AbstractApp.hasSync("/http").not, "sync removed");
    }
}

// SP_AbstractApp.test
