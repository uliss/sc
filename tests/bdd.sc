SP_Test : UnitTest {
    var checked_value;
    var thrown;
    var expect_result;
    var value_descr;
    var flag_not;
    var flag_throw;

    expect {
        arg v;
        try {
            thrown = nil;
            flag_not = false;
            flag_throw = false;
            value_descr = v.asString;
            checked_value = v;

            if(v.isKindOf(Function)) { v.value }
        } {
            |err|
            thrown = err;
        }
    }

    to {}
    be {}
    is {}
    have {}

    not { flag_not = flag_not.not }

    size {
        value_descr = "size of" + checked_value.asString;
        checked_value = checked_value.size;
    }

    processFlags {
        if(flag_not == true) { expect_result = expect_result.not };
    }

    equal_ { |v|
        expect_result = (checked_value == v);
        ^this.assertResult("equal to %".format(v));
    }

    empty_ {
        expect_result = checked_value.isEmpty;
        ^this.assertResult("empty");
    }

    nil_ {
        expect_result = checked_value.isNil;
        ^this.assertResult("nil");
    }

    true_ {
        expect_result = (checked_value === true);
        ^this.assertResult("true");
    }

    false_ {
        expect_result = (checked_value === false);
        ^this.assertResult("false");
    }

    a_ {
        arg class;
        expect_result = checked_value.class == class;
        ^this.assertResult("instance of %".format(class));
    }

    kindOf_ {
        arg class;
        expect_result = checked_value.isKindOf(class);
        ^this.assertResult("kind of %".format(class));
    }

    sizeOf_ {
        arg n = 0;
        expect_result = (checked_value.size == n);
        ^this.assertResult("size of %".format(n));
    }

    throw_ {
        arg class = Error;
        if(thrown.notNil) {
            expect_result = thrown.isKindOf(class);
            this.processFlags;

            if(expect_result == true) {
                this.passed(currentMethod);
            } {
                this.failed(currentMethod, message: "Unexpected exception thrown: %".format(class));
            }
        } {
            expect_result = false;
            this.processFlags;

            if(expect_result == true) {
                this.passed(currentMethod);
            } {
                this.failed(currentMethod, message: "Expected exception (%) not thrown".format(class));
            }
        };
    }

    assertResult {
        arg msg;
        var error_msg;
        error_msg = value_descr + "should";
        if(flag_not == true) { error_msg = error_msg + "not"};
        error_msg = error_msg + "be" + msg;

        this.processFlags;
        // this.assert(expect_result, error_msg);
        if(expect_result) {
            this.passed(currentMethod);
        } {
            this.failed(currentMethod, message: error_msg);
        };
        ^expect_result;
    }

    test {
        this.expect(false).to.be.false_;
        this.expect(false).to.not.be.true_;
        this.expect(true).to.be.true_;
        this.expect(true).to.be.not.false_;
        this.expect(111).to.be.equal_(111);
    }
}
