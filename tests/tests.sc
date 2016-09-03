+ UnitTest {
    before {}
    after {}
    beforeEach {}
    afterEach {}

    run { | reset = true, report = true, call_state = false|
        var function;
        if(reset) { this.class.reset };
        if(report) { ("RUNNING UNIT TEST" + this).inform };
        this.class.forkIfNeeded {
            this.before;
            this.findTestMethods.do { |method|
                this.beforeEach;
                this.setUp;

                currentMethod = method;

                {
                    this.perform(method.name);
                    // unfortunately this removes the interesting part of the call stack
                }.try({ |err|
                    var err_msg = "exception was thrown - %: %".format(err.class, err.errorString[7..]);
                    this.failed(method, err_msg);
                });

                this.afterEach;
                this.tearDown;
            };
            this.after;
            if(report) { this.class.report };
            nil
        };
    }

    failed { | method, message, report = true |
        var r = UnitTestResult(this, method, message);
        failures = failures.add(r);
        if(report){
            "".error;
            Post << "FAIL:";
            r.report;
        };
    }

    *report {
        Post.nl;
        "UNIT TEST.............".warn;
        if(failures.size > 0) {
            "There were failures:".error;
            failures.do {
                arg results;
                results.report
            };
        } {
            "There were no failures".inform;
        }
    }
}

+ UnitTestResult {
    report {
        var name = if(testMethod.notNil) { testMethod.name } { "unit test result" };
        Post << "  [" << name << "]";
        message !? ( Post << " " << (message !? (_.quote) ?? ""));
        Post << Char.nl;
    }
}

