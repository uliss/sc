TestGuidoAbstractApp : GuidoTest {
    test_SyncRegistration {
        this.expect(GuidoAbstractApp.hasSync("/test")).to.be.false_;

        GuidoAbstractApp.registerSync("/test", 1);
        this.expect(this).to.sendOSC_("/guido/module/server", "sync_add", "/test");

        this.expect(GuidoAbstractApp.hasSync("/test")).to.be.true_;

        GuidoAbstractApp.unregisterSync("/test");
        this.expect(GuidoAbstractApp.hasSync("/test")).to.be.false_;
        this.expect(this).to.sendOSC_("/guido/module/server", "sync_remove", "/test");
    }

    test_New {
        // no sync
        var app = GuidoAbstractApp.new("/osc", "/http", false);
        this.expect(app.oscPath).to.be.equal_("/osc");
        this.expect(app.httpPath).to.be.equal_("/http");
        this.expect(app.name).to.be.equal_("osc");
        this.expect(GuidoAbstractApp.hasSync("/http")).to.be.false_;
        app.free;
    }

    test_NewSynced {
        var app = GuidoAbstractApp.new("/osc", "/http", true);
        this.expect(app.oscPath).to.be.equal_("/osc");
        this.expect(app.httpPath).to.be.equal_("/http");
        this.expect(GuidoAbstractApp.hasSync("/http")).to.be.true_;
        this.expect(app).listen.osc_("/guido/sync/http");
        app.free;
        this.expect(GuidoAbstractApp.hasSync("/http")).to.be.false_;
        this.expect(app).not.listen.osc_("/guido/sync/http");
    }

    test_SendMsg {
        var app = GuidoAbstractApp.new("/osc", "/http", true);
        app.sendMsg("/path", 1, 2, 3, [4, 5]);
        this.expect(app).to.sendOSC_("/guido/forward", "/osc/path", 1, 2, 3, [4, 5]);
    }
}

// TestGuidoAbstractApp.run
