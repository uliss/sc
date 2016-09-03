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
    }

    test_params {
        var p = GuidoPieceApp.new("Partita", "J.S.Bach", "/partita");
        p.addPatch(\test, ["utils.tone"]);
        this.expect(p.params[\test]).keys.containsAny_(\amp, \freq);
    }
}

// TestGuidoPieceApp.run
