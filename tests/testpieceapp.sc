NodeJS_Test : UnitTest {
    startNodeJS {
        var condition = Condition.new;
		condition.test = false;
		this.waitForBoot({
			// Setting func to true indicates that our condition has become true and we can go when signaled.
			condition.test = true;
			condition.signal
		});
		condition.wait;
        if(NodeJS.isRunning) { ^this };
        {
            NodeJS.restart;
            20.do {
                if(NodeJS.isRunning.not) { 0.5.wait } { ^nil };
            }
        }.fork;
    }
}

TestSP_PieceApp : NodeJS_Test {
    test_new {
        var n;
        try {
            NodeJS.stop;
            n = SP_PieceApp.new;
            this.assert(false, "Should throw exception");
        } { |e|
            this.assert(true, "ok");
        };

        this.startNodeJS;
        this.assert(NodeJS.isRunning, "ok");
        // this.wait(NodeJS.isRunning, "", 10);
    }
}