TestSP_AbstractApp : SP_Test {
    var osc_msg;

    before {
        NodeJS.connected = true;
    }

    after {
        NodeJS.sendCallback = nil;
    }

    beforeEach {
        osc_msg = [];

        NodeJS.sendCallback = {
            arg ...args;
            osc_msg = args;
        };
    }

    test_SyncRegistration {
        this.expect(SP_AbstractApp.hasSync("test")).to.be.false_;

        SP_AbstractApp.registerSync("test", 1);
        this.expect(SP_AbstractApp.hasSync("test")).to.be.true_;
        this.expect(osc_msg).to.equal_(["/guido/module/server", "sync_add", "test"]);

        SP_AbstractApp.unregisterSync("test");
        this.expect(SP_AbstractApp.hasSync("test")).to.be.false_;
    }

    test_New {
        var app = SP_AbstractApp.new("/osc", "/http");
        this.expect(app.oscPath).to.be.equal_("/osc");
        this.expect(app.httpPath).to.be.equal_("/http");
        this.expect(app.name).to.be.equal_("osc");
        this.expect(SP_AbstractApp.hasSync("test")).to.be.false_;
        app.free;
    }

    test_NewSynced {
        var app = SP_AbstractApp.new("/osc", "/http", true);
        this.expect(app.oscPath).to.be.equal_("/osc");
        this.expect(app.httpPath).to.be.equal_("/http");
        this.expect(SP_AbstractApp.hasSync("/http")).to.be.true_;
        app.free;
        this.expect(SP_AbstractApp.hasSync("/http")).to.be.false_;
    }

    test_SendMsg {
        var app = SP_AbstractApp.new("/osc", "/http", true);
        app.sendMsg("/path", 1, 2, 3, [4, 5]);
        this.expect(osc_msg).equal_(["/guido/forward", "/osc/path", 1, 2, 3, [4, 5]]);
    }
}

// SP_AbstractApp.test
