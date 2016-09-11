SP_Test : UnitTest {
    var checked_value;
    var thrown;
    var expect_result;
    var value_descr;
    var flag_not;
    var flag_throw;
    var msg_chain;

    expect {
        arg v;
        try {
            msg_chain = List.new;
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

    to { msg_chain.add("to") }
    be { msg_chain.add("be") }
    is { msg_chain.add("is") }
    have { msg_chain.add("have") }

    not {
        msg_chain.add("not");
        flag_not = flag_not.not
    }

    size {
        value_descr = "size of" + checked_value.asString;
        checked_value = checked_value.size;
    }

    keys {
        value_descr = "keys of" + checked_value.asString;
        checked_value = checked_value.keys;
    }

    processFlags {
        if(flag_not == true) { expect_result = expect_result.not };
    }

    equal_ { |v|
        expect_result = (checked_value == v);
        ^this.assertResult("equal %".format(v));
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
        value_descr = "% (instance of %)".format(checked_value, checked_value.class);
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

    identical_ {
        arg obj;
        expect_result = (checked_value === obj);
        ^this.assertResult("identical to %".format(obj));
    }

    includes_ {
        arg v;
        expect_result = (checked_value.includes(v));
        ^this.assertResult("includes %".format(v));
    }

    contains_ {
        arg v;
        expect_result = (checked_value.includes(v));
        ^this.assertResult("contains %".format(v));
    }

    containsAny_ {
        arg ...args;
        expect_result = (checked_value.includesAny(args));
        ^this.assertResult("contains any of %".format(args));
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

    exists_ {
        expect_result = (checked_value.pathExists !== false);
        ^this.assertResult("exists");
    }

    assertResult {
        arg msg;
        var error_msg;

        error_msg = (["expect", value_descr] ++ msg_chain ++ [msg]).join(" ");

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
