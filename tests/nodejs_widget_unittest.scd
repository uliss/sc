{ |test|
    var w;
    NodeJS.restart;
    "NodeJS Widget test".postln;

    w = NodeJS_Widget.new(\knob, [\width, 100, \height, 10]);
    test.assertEquals(w.type, \knob);
    w.params[\idx] = "knob0";
    test.assertEquals("test", \test.asString);
    test.assertEquals(w.asJSON, '{"width": 100,"oscPath": "/nodejs/ui","idx": "knob0","height": 10,"type": "knob"}');
/*    test.assertEquals(JSON.toJSON(1), "1");
    test.assertEquals(JSON.toJSON(-1), "-1");
    test.assertEquals(JSON.toJSON(123.456), "123.456");
    test.assertEquals(JSON.toJSON("string 1"), "string 1".quote);
    test.assertEquals(JSON.toJSON(true), "true");
    test.assertEquals(JSON.toJSON(false), "false");
    test.assertEquals(JSON.toJSON([]), "[]");
    test.assertEquals(JSON.toJSON([1,2,3]), "[1,2,3]");
    test.assertEquals(JSON.toJSON(\test), "test".quote);
    test.assertEquals(JSON.toJSON(\t -> 2), "{t:2}");
    test.assertEquals(JSON.toJSON((one: 1, two: 2)), '{"one": 1,"two": 2}'.asString);*/
    {NodeJS.stop;}.defer(3);
};


NodeJS_Widget.test