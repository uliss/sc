SP_Test : UnitTest {
    var checked_value;
    var expect_result;
    var value_descr;
    var flag_not;

    expect {
        arg v;
        flag_not = false;
        value_descr = v.asString;
        checked_value = v;
    }

    to {}
    be {}
    is {}
    have {}

    not { flag_not = true }

    size {
        value_descr = "size of" + checked_value.asString;
        checked_value = checked_value.size;
    }

    equal_ { |v|
        expect_result = (checked_value == v);
        this.assertResult("equal to %".format(v));
    }

    empty_ {
        expect_result = checked_value.isEmpty;
        this.assertResult("empty");
    }

    nil_ {
        expect_result = checked_value.isNil;
        this.assertResult("nil");
    }

    processFlags {
        if(flag_not == true) { expect_result = expect_result.not };
    }

    true_ {
        expect_result = (checked_value === true);
        this.assertResult("true");
    }

    false_ {
        expect_result = (checked_value === true);
        this.assertResult("false");
    }

    a_ {
        arg class;
        expect_result = checked_value.class == class;
        this.assertResult("instance of %".format(class));
    }

    kindOf_ {
        arg class;
        expect_result = checked_value.isKindOf(class);
        this.assertResult("kind of %".format(class));
    }

    sizeOf_ {
        arg n;
        expect_result = (checked_value.size == n);
        this.assertResult("size of %".format(n));
    }

    assertResult {
        arg msg;
        var error_msg;
        error_msg = value_descr + "should";
        if(flag_not == true) { error_msg = error_msg + "not"};
        error_msg = error_msg + "be" + msg;

        this.processFlags;
        this.assert(expect_result, error_msg);
    }
}