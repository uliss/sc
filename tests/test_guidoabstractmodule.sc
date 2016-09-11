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

    test_func {
        var a = 0, b = Array.new(10);
        var m = GuidoAbstractModule.new("/test1");
        m.addFunction("testA", { a = a + 1 });
        this.expect(m.hasFunction(\testA)).to.be.true_;
        m.callFunction(\testA);
        this.expect(a).to.be.equal_(1);
        m.callFunction(\testA);
        this.expect(a).to.be.equal_(2);

        m.addFunction(\testB, { |a1, a2, a3| b.add(a1); b.add(a2); b.add(a3) });
        m.callFunction(\testB, 1, 2, 3);
        this.expect(b).to.be.equal_([1 ,2, 3]);

        this.expect(m.functionList).to.be.equal_([\testA, \testB]);

        m.removeFunction(\testB);
        this.expect(m.hasFunction(\testB)).to.be.false_;

        m.free;
        this.expect(m.hasFunction(\testA)).to.be.false_;
    }

    test_osc {
        var tmp = 0;
        var m = GuidoAbstractModule.new("/test1");
        m.addFunction(\addN, { |n| tmp = tmp + n });
        this.receiveOSC("/test1", "addN", 10);
        this.expect(tmp).to.be.equal_(10);
        this.receiveOSC("/test1", "addN", 10);
        this.expect(tmp).to.be.equal_(20);
        m.free;
    }
}

// TestGuidoAbstractModule.run
