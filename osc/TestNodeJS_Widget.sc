TestNodeJS_Widget : UnitTest {
    setUp {
        NodeJS.start;
    }

    tearDown {
        {NodeJS.stop}.defer(3);
    }

    test_Init {
        var w = NodeJS_Widget.new(\knob, [\width, 100, \height, 10]);
        this.assertEquals(w.type, \knob);
        this.assertEquals(w.params[\width], 100);
        this.assertEquals(w.params[\height], 10);
        this.assert(w.params[\idx].beginsWith("knob"));
        this.assert(w.params[\oscPath].notNil);
        this.assertEquals(w.params[\oscPath], "/nodejs/ui");
        this.assert(w.action.isNil);
        this.assert(w.label.isNil);
        w.params[\idx] = \knob0;
        this.assertEquals(w.asJSON, '{"width": 100,"oscPath": "/nodejs/ui","idx": "knob0","height": 10,"type": "knob"}'.asString);
        this.assert(w.value.isNil);
    }

    test_Label {
        var w = NodeJS_Widget.new(\pan);
        this.assert(w.label.isNil);
        w.label = "Label";
        this.assertEquals(w.label, "Label");
        w.params[\idx] = \pan0;
        this.assertEquals(w.asJSON, '{"oscPath": "/nodejs/ui","type": "pan","idx": "pan0","label": "Label"}'.asString);
    }

    test_Value {
        var w = NodeJS_Widget.new(\test);
        this.assert(w.value.isNil);
        w.value = 45;
        this.assertEquals(w.value, 45);
        w.params[\idx] = \test0;
        this.assertEquals(w.asJSON, '{"oscPath": "/nodejs/ui","value": 45,"type": "test","idx": "test0"}'.asString);
    }

    test_Idx {
        var w1, w2;
        w1 = NodeJS_Widget.new(\pan);
        w2 = NodeJS_Widget.new(\pan);
        this.assertEquals((w2.idx - w1.idx), 1);
    }
}