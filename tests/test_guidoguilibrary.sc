TestGuidoGuiLibrary : GuidoTest {
    test_new {
        var l = GuidoGuiLibrary.new;
        var l2 = GuidoGuiLibrary.new;
        var w;

        this.expect(l).to.be.not.identical_(l2);

        this.expect(l.groups).to.be.equal_([\freeverb, \monitor]);
        this.expect(l.groupWidgets(\unknown)).to.be.nil_;
        this.expect(l.groupWidgets(\freeverb)).to.be.not.empty_;
        this.expect(l.groupWidgetAssoc(\monitor).collect({|k| k.key })).to.be.equal_([ \box, \mute, \vol ]);
        w = l.groupWidgets(\monitor);
         this.expect(l.groupWidgetAssoc(\monitor)).to.be.equal_([ \box -> w[0], \mute -> w[1], \vol -> w[2] ]);
    }
}

// TestGuidoGuiLibrary.run
