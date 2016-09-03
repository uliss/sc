TestGuidoAbstractModule : GuidoTest {
    test_new {
        var path = "/test/path1";
        var m = GuidoAbstractModule.new(path);
        this.expect(m).listen.osc_(path);
        CmdPeriod.run;
        this.expect(m).listen.osc_(path);
        m.free;
        this.expect(m).not.listen.osc_(path);
    }
}

// TestGuidoAbstractModule.run
