NodeJS_Widget {
    classvar idx_count = 1;
    var <idx;
    var <>type;
    var <>params;
    var <>action;
    var added;
    var osc;

    *toUI {
        NodeJS.sendMsg("/sc/redirect", "/ui");
    }

    *new {
        arg type, params = [];
        ^super.new.init(type, params);
    }

    init {
        arg t, p = [];
        type = t;
        idx = idx_count;
        added = false;
        params = Dictionary.new;
        params[\idx] = type ++ idx;
        params[\parent] = "ui-elements";
        params = params ++ Dictionary.newFrom(p);
        params[\type] = type;
        params[\oscPath] = "/ui";

        idx_count = idx_count + 1;
        osc = OSCFunc({|m|
            params[\value] = m[1];
            if(action.notNil) {
                action.value(m);
            }
        }, "/sc/ui/" ++ this.id, nil, NodeJS.outOscPort);
    }

    colors_ {
        arg border = "#FAA", fill = "#0F0", accent = "#F00";
        params[\colors] = [border, fill, accent];
    }

    add {
        added = true;
        NodeJS.sendMsg("/node/widget/add", this.asJSON);
        ^this;
    }

    remove {
        added = false;
        NodeJS.sendMsg("/node/widget/remove", this.id);
        ^this;
    }

    update {
        NodeJS.sendMsg("/node/widget/update", this.asJSON);
        ^this;
    }

    id {
        ^params[\idx];
    }

    value {
        ^params[\value];
    }

    value_ { |v|
        params[\value] = v;
        if(added) { this.update }
    }

    asJSON {
        ^JSON.toJSON(params);
    }

    label {
        ^params[\label];
    }

    label_ { |txt|
        params[\label] = txt;
    }

    css {
        arg k, v;
        NodeJS.css("#" ++ this.id, k, v);
    }
}

NodeJS_Knob : NodeJS_Widget {
    *new {
        arg value = 0.0, min = 0.0, max = 1.0, size = 100, label = "", params = [];
        ^super.new("knob", [
            \size, size,
            \label, label,
            \min, min,
            \max, max,
            \value, value
        ] ++ params);
    }
}

NodeJS_Pan : NodeJS_Widget {
    *new {
        arg value = 0, size = 50, params = [];
        var p = super.new("pan", [
            \size, size,
            \value, value
        ] ++ params);
        p.label = p.id;
        ^p;
    }
}

NodeJS_Slider : NodeJS_Widget {
    *new {
        arg value = 0.0, min = 0.0, max = 1.0, size = 180, label = "", horizontal = 0, relative = 0, params = [];
        var p = super.new("slider", [
            \horizontal, horizontal,
            \min, min,
            \max, max,
            \value, value,
            \size, size,
            \label, label,
            \relative, relative] ++ params);
        ^p;
    }
}

NodeJS_Toggle : NodeJS_Widget {
    *new {
        arg value = 0, size = 100, label = "", params = [];
        var p = super.new("toggle", [
            \value, value,
            \label, label,
            \size, size] ++ params);
        ^p;
    }
}

NodeJS_Button : NodeJS_Widget {
    *new {
        arg size = 100, label = "", params = [];
        var p = super.new("button", [\size, size, \label, label] ++ params);
        ^p;
    }
}

NodeJS_Pianoroll : NodeJS_Widget {
    *new {
        arg size = 600, octaves = 3, midibase = 48, params = [];
        var p = super.new("pianoroll", [
            \size, size,
            \octaves, octaves,
            \midibase, midibase] ++ params);
        ^p;
    }
}

NodeJS_XFade : NodeJS_Widget {
    *new {
        arg size = 200, label = "", params = [];
        var p = super.new("crossfade", [
            \size, size,
            \label, label] ++ params);
        ^p;
    }
}

NodeJS_Matrix : NodeJS_Widget {
    *new {
        arg size = 200, row = 4, col = 4, label = "", params = [];
        var p = super.new("matrix", [
            \size, size,
            \row, row,
            \col, col,
            \label, label] ++ params);
        ^p;
    }

}

NodeJS_UI1 {
    var <knob;
    var <toggle;
    var <button;
    var <slider;
    var lines;

    *new {
        arg num = 6;
        ^super.new.init(num);
    }

    init { |n|
        knob = Array.new(n);
        toggle = Array.new(n);
        button = Array.new(n);
        slider = Array.new(n);
        lines = List.new;

        n.do { |i|
            var k, t, b, s;
            var l;

            l = "knob" ++ i;
            k = NodeJS_Knob.new(params: [\idx, l]);
            knob.add(k);
            k.label = l;

            l = "toggle" ++ i;
            t = NodeJS_Toggle.new(size: 100, params: [\idx, l]);
            toggle.add(t);
            t.label = l;

            l = "button" ++ i;
            b = NodeJS_Button.new(100, params: [\idx, l]);
            button.add(b);
            b.label = l;

            l = "slider" ++ i;
            s = NodeJS_Slider.new(0, params:[\idx, l]);
            slider.add(s);
        }
    }

    add {
        knob.do { |k| k.add };
        this.addNewline;
        toggle.do { |t| t.add };
        this.addNewline;
        button.do { |b| b.add };
        this.addNewline;
        slider.do { |s|
            s.add;
            s.css("margin", "5px 33px");
        };
    }

    addNewline {
        var nl = NodeJS_Widget.new("newline");
        nl.add;
    }

    remove {
        knob.do { |k| k.remove };
        toggle.do { |t| t.remove };
        button.do { |b| b.remove };
        slider.do { |s| s.remove };
    }

    labels_ {
        arg type, values = [];
        var elems, dict;

        switch(type.toString,
            "knob",   {elems = `knob},
            "toggle", {elems = `toggle},
            "button", {elems = `button},
            "slider", {elems = `slider}
            );

        dict = Dictionary.newFrom(values);
        dict.postln;
        dict.keysValuesDo{ |k,v|
            if(elems[k].notNil) {
                elems[k].label = v;
                elems[k].update;
            };
        };
    }
}

