TestSP_PieceApp : UnitTest {
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