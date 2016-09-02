TestNodeJS : UnitTest {
    setUp {
        // NodeJS.start;
    }

    tearDown {
        // NodeJS.stop;
    }

    test_Init {
        this.assertEquals(NodeJS.outOscPort, 5001);
        this.assertEquals(NodeJS.inOscPort, 5000);
    }

    test_Stop {
        this.assert(NodeJS.stop);
        this.assertEquals(NodeJS.connected, false);
    }


    test_Send {
        var msg, func;
        msg = List.new;
        NodeJS.connected = false;
        this.assertEquals(NodeJS.sendMsg("test"), false);
        NodeJS.connected = true;
        NodeJS.sendCallback = {
            arg addr ... args;
            msg.add(addr);
            msg = msg ++ args;
        };
        this.assert(NodeJS.sendMsg("test", 1, 2, 3), true);
        this.assertEquals(msg, ["test", 1,2,3]);
        NodeJS.connected = false;
    }

    test_Redirect {
        var msg = List.new;
        NodeJS.connected = true;
        NodeJS.sendCallback = {
            arg addr ... args;
            msg.add(addr);
            msg = msg ++ args;
        };
        NodeJS.redirect("/ui");
        this.assertEquals(msg, ["/guido/module/client", "redirect", "/ui"]);
        NodeJS.connected = false;
    }

    test_Reload {
        var msg = List.new;
        NodeJS.connected = true;
        NodeJS.sendCallback = {
            arg addr ... args;
            msg.add(addr);
            msg = msg ++ args;
        };
        NodeJS.reload;
        this.assertEquals(msg, ["/guido/module/client", "reload"]);
        NodeJS.connected = false;
    }

    test_Css {
        var msg = List.new;
        NodeJS.connected = true;
        NodeJS.sendCallback = {
            arg addr ... args;
            msg.add(addr);
            msg = msg ++ args;
        };
        NodeJS.css("html", "color", "red");
        this.assertEquals(msg, ["/guido/module/client", "css", "html", "color", "red"]);
        NodeJS.connected = false;
    }

    test_Send2Cli {
        var msg = List.new;
        NodeJS.connected = true;
        NodeJS.sendCallback = {
            arg addr ... args;
            msg.add(addr);
            msg = msg ++ args;
        };
        NodeJS.send2Cli("/ui", "arg1", 2);
        this.assertEquals(msg, ["/guido/forward", "/ui", "arg1", 2]);
        NodeJS.connected = false;
    }
}


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
        this.assertEquals(w.asJSON, '{"oscPath": "/nodejs/ui","idx": "pan0","type": "pan","label": "Label"}'.asString);
    }

    test_Value {
        var w = NodeJS_Widget.new(\test);
        this.assert(w.value.isNil);
        w.value = 45;
        this.assertEquals(w.value, 45);
        w.params[\idx] = \test0;
        this.assertEquals(w.asJSON, '{"oscPath": "/nodejs/ui","value": 45,"idx": "test0","type": "test"}'.asString);
    }

    test_Idx {
        var w1, w2;
        w1 = NodeJS_Widget.new(\pan);
        w2 = NodeJS_Widget.new(\pan);
        this.assertEquals((w2.idx - w1.idx), 1);
    }
}