TestGuidoGuiLibrary : GuidoTest {
    test_new {
        var l = GuidoGuiLibrary.new;
        var l2 = GuidoGuiLibrary.new;

        this.expect(l).to.be.identical_(l2);

        this.expect(l.groups).to.be.equal_([\freeverb, \monitor]);
        this.expect(l.groupWidgets(\unknown)).to.be.nil_;
        this.expect(l.groupWidgets(\freeverb)).to.be.not.empty_;
    }
}

// TestGuidoGuiLibrary.run
