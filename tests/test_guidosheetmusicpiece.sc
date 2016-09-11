TestGuidoSheetMusicPiece : GuidoTest {
    beforeEach {
        super.beforeEach;
        NodeJS_Widget.idx_count = 1;
    }

    test_new {
        var listeners = this.oscListeners;
        var p = GuidoSheetMusicPiece.new("Partita", "J.S.Bach", "/partita");


        p.free;
        this.expect(listeners).to.be.equal_(this.oscListeners);
    }

    test_load {
        var p = GuidoSheetMusicPiece.new("Partita", "J.S.Bach", "/partita");
        p.schedTurnNext("1:00");
        p.schedTurnToPage("0:25", 10);
        p.saveTasks(true);

        p.removeAllTasks;

        {
            var f = File.new(p.tasksFilename, "r");
            var lines = f.readAllString.split($\n);
            f.close;

            this.expect(lines.size).to.be.equal_(3);
            this.expect(lines[0]).to.be.equal_("00:00:25 page 10");
            this.expect(lines[1]).to.be.equal_("00:01:00 turn");
        }.value;

        p.loadTasks;
        this.expect(p.hasTask(25)).to.be.true_;
        this.expect(p.hasTask(60)).to.be.true_;

        p.currentTime = 25;
        p.play;
        p.stop;


        p.free;
    }

    test_named_task {
        var p = GuidoSheetMusicPiece.new("Partita", "J.S.Bach", "/partita");

        this.expect(p.namedTaskFunction(\print)).to.be.not.nil_;
        this.expect(p.namedTaskFunction(nil)).to.be.not.nil_;
        this.expect(p.namedTaskFunction(\unknown)).to.be.nil_;

        p.free;
    }
}

// TestGuidoSheetMusicPiece.run
