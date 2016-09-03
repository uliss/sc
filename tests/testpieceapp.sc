TestGuidoPieceApp : GuidoTest {
    test_new {
        var p = GuidoPieceApp.new("Partita", "J.S.Bach", "/partita");
        this.expect(p).to.be.a_(GuidoPieceApp);
        this.expect(p.title).to.be.equal_("Partita");
        this.expect(p.composer).to.be.equal_("J.S.Bach");
        this.expect(p.oscPath).to.be.equal_("/partita");
        this.expect(p.httpPath).to.be.equal_("/piece");
        this.expect(p).to.be.identical_(GuidoPieceApp.new("Partita", "J.S.Bach", "/partita"));
        this.expect(p.phonesChannel).to.be.equal_(4);
        this.expect(p.isPlaying).to.be.false_;
        this.expect(p.isPaused).to.be.false_;
        this.expect(p.isStopped).to.be.true_;
        this.expect(p).listen.osc_("/partita");
        this.expect(p).listen.osc_("/guido/sync/piece");
        p.free;
        this.expect(p).not.listen.osc_("/partita");
        this.expect(p).not.listen.osc_("/guido/sync/piece");
    }

    test_patches {
        var p = GuidoPieceApp.new("Partita", "J.S.Bach", "/partita");
        this.expect(p.addPatch()).to.be.nil_;
        this.expect(p.addPatch(\test)).to.be.nil_;
        this.expect(p.addPatch(\test, [])).to.be.nil_;
        this.expect(p.addPatch(\test, ["utils.tone"])).to.be.not.nil_;
        this.expect(p.patch(\test)).to.be.not.nil_;
        this.expect(p.patch("test")).to.be.not.nil_;
        this.expect(p.patch(\test)).to.be.a_(Patch);
        p.removePatch(\test);
        this.expect(p.patch(\test)).to.be.nil_;
        p.free
    }

    test_params {
        var p, dict;
        p = GuidoPieceApp.new("Partita", "J.S.Bach", "/partita");
        p.addPatch(\test, ["utils.tone"]);
        this.expect(p.params[\test]).keys.containsAny_(\amp, \freq);

        {
            var opts;
            opts = p.params;

            this.expect(p.saveParams).to.be.equal_(opts);
            p.set(\test, \amp, 1);
            this.expect(p.params).to.be.not.equal_(opts);
            this.expect(opts).to.be.equal_(p.loadParamsDict);
            p.loadParams;
            this.expect(p.params).to.be.equal_(opts);
        }.value;
        p.free
    }

    test_widgets {
        var p = GuidoPieceApp.new("Partita", "J.S.Bach", "/partita");
        p.addWidget(\knob1, NodeJS_Knob.new);
        this.expect(p.widget("knob1")).to.be.not.nil_;
        p.createWidgets;
        this.expect(p).to.sendOSC_(
            "/guido/forward",
            "/guido/widget/add",
            "{\"max\": 1,\"size\": 100,\"min\": 0,\"parent\": \"ui-elements\",\"oscPath\": \"/ui\",\"value\": 0,\"idx\": \"knob1\",\"label\": \"\",\"type\": \"knob\"}");

        p.removeWidget("knob1");
        this.expect(p).to.sendOSC_("/guido/forward",
            "/guido/widget/remove", "knob1");
        this.expect(p.widget("knob1")).to.be.nil_;
        p.free;
    }
}

// TestGuidoPieceApp.run
