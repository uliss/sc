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
    }
}

// TestGuidoPieceApp.run
