TestGuidoSheetMusicPiece : GuidoTest {
    beforeEach {
        super.beforeEach;
        NodeJS_Widget.idx_count = 1;
    }

    test_new {
        var listeners = this.oscListeners;
        var p = TestGuidoSheetMusicPiece.new("Partita", "J.S.Bach", "/partita");


        p.free;
        this.expect(listeners).to.be.equal_(this.oscListeners);
    }
}

// TestGuidoSheetMusicPiece.run
